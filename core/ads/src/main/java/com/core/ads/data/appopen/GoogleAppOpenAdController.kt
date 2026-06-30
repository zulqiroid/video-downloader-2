package com.core.ads.data.appopen

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.core.ads.data.cache.CachedAd
import com.core.ads.domain.analytics.AdBlockReason
import com.core.ads.domain.analytics.AdErrorInfo
import com.core.ads.domain.analytics.AdRequestSource
import com.core.ads.domain.analytics.AdRevenueInfo
import com.core.ads.domain.analytics.AdUnitSource
import com.core.ads.domain.analytics.AdsAnalyticsEvent
import com.core.ads.domain.analytics.AdsAnalyticsTracker
import com.core.ads.domain.appopen.AppOpenAdController
import com.core.ads.domain.appopen.AppOpenAdShowResult
import com.core.ads.domain.appopen.AppOpenAdState
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.display.AdsDisplayGuard
import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.policy.AdsOperation
import com.core.ads.domain.policy.AdsPolicyContext
import com.core.ads.domain.policy.AdsPolicyDecision
import com.core.ads.domain.policy.AdsPolicyEvaluator
import com.core.ads.utils.AdsLogger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GoogleAppOpenAdController(
    context: Context,
    private val configStore: AdsConfigStore,
    private val policyEvaluator: AdsPolicyEvaluator,
    private val analyticsTracker: AdsAnalyticsTracker,
    private val adsDisplayGuard: AdsDisplayGuard
) : AppOpenAdController {

    private val appContext: Context = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private var cachedAd: CachedAd<AppOpenAd>? = null
    private var isLoadingAd: Boolean = false
    private var isShowingAd: Boolean = false
    private var lastShownAtMillis: Long = 0L

    private val mutableState = MutableStateFlow(AppOpenAdState())
    override val state: StateFlow<AppOpenAdState> = mutableState.asStateFlow()

    override fun preload() {
        runOnMainThread {
            loadAdIfNeeded(
                source = AdRequestSource.AUTO_PRELOAD,
                preferredAdUnitId = null,
                preferredAdUnitSource = AdUnitSource.GLOBAL
            )
        }
    }

    override fun preloadForSplash() {
        runOnMainThread {
            val splashAdUnitId = resolveSplashAdUnitId()

            if (splashAdUnitId == null) {
                val message = "Splash AppOpen ad unit ID is missing."

                AdsLogger.w(message)

                mutableState.update {
                    it.copy(lastErrorMessage = message)
                }

                return@runOnMainThread
            }

            loadAdIfNeeded(
                source = AdRequestSource.APP_START,
                preferredAdUnitId = splashAdUnitId,
                preferredAdUnitSource = resolveSplashAdUnitSource(splashAdUnitId)
            )
        }
    }

    override fun showSplashIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit
    ) {
        runOnMainThread {
            val splashAdUnitId = resolveSplashAdUnitId()

            if (splashAdUnitId == null) {
                val message = "Splash AppOpen skipped. Ad unit ID is missing."

                AdsLogger.w(message)

                mutableState.update {
                    it.copy(lastErrorMessage = message)
                }

                onComplete(
                    AppOpenAdShowResult.Skipped(
                        reason = message
                    )
                )

                return@runOnMainThread
            }

            val freshSplashAd = getFreshCachedAd(
                expectedAdUnitId = splashAdUnitId
            )

            if (freshSplashAd == null) {
                AdsLogger.d("Splash AppOpen not ready. Preloading splash ad and continuing.")

                onComplete(AppOpenAdShowResult.NotAvailable)

                loadAdIfNeeded(
                    source = AdRequestSource.APP_START,
                    preferredAdUnitId = splashAdUnitId,
                    preferredAdUnitSource = resolveSplashAdUnitSource(splashAdUnitId)
                )

                return@runOnMainThread
            }

            showIfAvailable(
                activity = activity,
                onComplete = onComplete
            )
        }
    }

    override fun showIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit
    ) {
        runOnMainThread {
            val placement = AdPlacement.APP_OPEN
            val config = configStore.current.appOpenAdConfig
            val freshCachedAd = getFreshCachedAd()

            val cooldownRemainingMillis = getCooldownRemainingMillis()

            analyticsTracker.track(
                AdsAnalyticsEvent.ShowRequested(
                    format = AdFormat.APP_OPEN,
                    placement = placement,
                    adUnitId = freshCachedAd?.adUnitId ?: config.adUnitId,
                    adUnitSource = AdUnitSource.GLOBAL
                )
            )

            val decision = policyEvaluator.evaluate(
                AdsPolicyContext(
                    operation = AdsOperation.SHOW,
                    format = AdFormat.APP_OPEN,
                    placement = placement,
                    activity = activity,
                    isLoading = isLoadingAd,
                    isShowing = isShowingAd,
                    hasFreshCache = freshCachedAd != null,
                    cooldownRemainingMillis = cooldownRemainingMillis
                )
            )

            when (decision) {
                is AdsPolicyDecision.Blocked -> {
                    AdsLogger.w(
                        "AppOpen show blocked. reason=${decision.reason}, message=${decision.message}"
                    )

                    mutableState.update {
                        it.copy(lastErrorMessage = decision.message)
                    }

                    analyticsTracker.track(
                        AdsAnalyticsEvent.ShowBlocked(
                            format = AdFormat.APP_OPEN,
                            placement = placement,
                            reason = decision.reason,
                            message = decision.message
                        )
                    )

                    if (decision.reason == AdBlockReason.CACHE_NOT_AVAILABLE) {
                        onComplete(AppOpenAdShowResult.NotAvailable)
                        loadAdIfNeeded(source = AdRequestSource.AUTO_PRELOAD)
                    } else {
                        onComplete(
                            AppOpenAdShowResult.Skipped(
                                reason = decision.message
                            )
                        )
                    }

                    return@runOnMainThread
                }

                is AdsPolicyDecision.Allowed -> Unit
            }

            val adToShow = freshCachedAd ?: run {
                val message = "AppOpen cached ad became unavailable before show."

                analyticsTracker.track(
                    AdsAnalyticsEvent.ShowBlocked(
                        format = AdFormat.APP_OPEN,
                        placement = placement,
                        reason = AdBlockReason.CACHE_NOT_AVAILABLE,
                        message = message
                    )
                )

                onComplete(AppOpenAdShowResult.NotAvailable)
                loadAdIfNeeded(source = AdRequestSource.AUTO_PRELOAD)
                return@runOnMainThread
            }

            val displayMarked = adsDisplayGuard.markShowing(
                format = AdFormat.APP_OPEN,
                placement = placement
            )

            if (!displayMarked) {
                val message = "Another full-screen ad is already showing."

                analyticsTracker.track(
                    AdsAnalyticsEvent.ShowBlocked(
                        format = AdFormat.APP_OPEN,
                        placement = placement,
                        reason = AdBlockReason.DISPLAY_GUARD_BLOCKED,
                        message = message
                    )
                )

                onComplete(
                    AppOpenAdShowResult.Skipped(
                        reason = message
                    )
                )

                return@runOnMainThread
            }

            isShowingAd = true

            mutableState.update {
                it.copy(
                    isShowing = true,
                    lastErrorMessage = null
                )
            }

            analyticsTracker.track(
                AdsAnalyticsEvent.ShowEligible(
                    format = AdFormat.APP_OPEN,
                    placement = placement,
                    adUnitId = adToShow.adUnitId,
                    adUnitSource = adToShow.adUnitSource
                )
            )

            var completed = false
            var adActuallyShown = false
            var shownAtMillis = 0L

            fun completeOnce(result: AppOpenAdShowResult) {
                if (completed) return
                completed = true
                AdsLogger.d("AppOpen complete. result=$result")
                onComplete(result)
            }

            adToShow.ad.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdShowedFullScreenContent() {
                    adActuallyShown = true
                    shownAtMillis = currentTimeMillis()

                    AdsLogger.i("AppOpen ad shown")

                    analyticsTracker.track(
                        AdsAnalyticsEvent.ShowStarted(
                            format = AdFormat.APP_OPEN,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )
                }

                override fun onAdImpression() {
                    analyticsTracker.track(
                        AdsAnalyticsEvent.Impression(
                            format = AdFormat.APP_OPEN,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )
                }

                override fun onAdClicked() {
                    analyticsTracker.track(
                        AdsAnalyticsEvent.Clicked(
                            format = AdFormat.APP_OPEN,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )
                }

                override fun onAdDismissedFullScreenContent() {
                    AdsLogger.i("AppOpen ad dismissed")

                    analyticsTracker.track(
                        AdsAnalyticsEvent.Dismissed(
                            format = AdFormat.APP_OPEN,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )

                    handleAdFinished(
                        ad = adToShow.ad,
                        wasShown = adActuallyShown,
                        shownAtMillis = shownAtMillis
                    )

                    completeOnce(AppOpenAdShowResult.Shown)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    AdsLogger.e("AppOpen failed to show: ${adError.message}")

                    analyticsTracker.track(
                        AdsAnalyticsEvent.ShowFailed(
                            format = AdFormat.APP_OPEN,
                            placement = placement,
                            adUnitId = adToShow.adUnitId,
                            error = adError.toAdErrorInfo()
                        )
                    )

                    handleAdFinished(
                        ad = adToShow.ad,
                        wasShown = false,
                        shownAtMillis = 0L
                    )

                    mutableState.update {
                        it.copy(lastErrorMessage = adError.message)
                    }

                    completeOnce(
                        AppOpenAdShowResult.Failed(
                            reason = adError.message
                        )
                    )
                }
            }

            try {
                adToShow.ad.show(activity)
            } catch (exception: Exception) {
                val message = "Exception during AppOpen show: ${exception.message}"

                AdsLogger.e(message, exception)

                analyticsTracker.track(
                    AdsAnalyticsEvent.ShowFailed(
                        format = AdFormat.APP_OPEN,
                        placement = placement,
                        adUnitId = adToShow.adUnitId,
                        error = AdErrorInfo(
                            message = message,
                            cause = exception::class.java.simpleName
                        )
                    )
                )

                handleAdFinished(
                    ad = adToShow.ad,
                    wasShown = false,
                    shownAtMillis = 0L
                )

                mutableState.update {
                    it.copy(lastErrorMessage = message)
                }

                completeOnce(
                    AppOpenAdShowResult.Failed(
                        reason = message
                    )
                )
            }
        }
    }

    override fun clear() {
        runOnMainThread {
            cachedAd = null
            isLoadingAd = false
            isShowingAd = false
            lastShownAtMillis = 0L

            adsDisplayGuard.markFinished(
                format = AdFormat.APP_OPEN,
                placement = AdPlacement.APP_OPEN
            )

            mutableState.value = AppOpenAdState()
        }
    }

    private fun loadAdIfNeeded(
        source: AdRequestSource,
        preferredAdUnitId: String? = null,
        preferredAdUnitSource: AdUnitSource = AdUnitSource.GLOBAL
    ) {
        val placement = AdPlacement.APP_OPEN
        val config = configStore.current.appOpenAdConfig
        val targetAdUnitId = preferredAdUnitId.cleanOrNull()
        val freshCachedAd = getFreshCachedAd(
            expectedAdUnitId = targetAdUnitId
        )

        analyticsTracker.track(
            AdsAnalyticsEvent.LoadRequested(
                format = AdFormat.APP_OPEN,
                placement = placement,
                source = source,
                adUnitId = targetAdUnitId ?: config.adUnitId,
                adUnitSource = AdUnitSource.GLOBAL
            )
        )

        val decision = policyEvaluator.evaluate(
            AdsPolicyContext(
                operation = AdsOperation.LOAD,
                format = AdFormat.APP_OPEN,
                placement = placement,
                isLoading = isLoadingAd,
                isShowing = isShowingAd,
                hasFreshCache = freshCachedAd != null
            )
        )

        when (decision) {
            is AdsPolicyDecision.Blocked -> {
                AdsLogger.w(
                    "AppOpen load blocked. reason=${decision.reason}, message=${decision.message}"
                )

                analyticsTracker.track(
                    AdsAnalyticsEvent.LoadBlocked(
                        format = AdFormat.APP_OPEN,
                        placement = placement,
                        reason = decision.reason,
                        message = decision.message
                    )
                )

                val shouldShowAsError = decision.reason !in setOf(
                    AdBlockReason.ALREADY_LOADING,
                    AdBlockReason.CACHE_AVAILABLE
                )

                if (shouldShowAsError) {
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            lastErrorMessage = decision.message
                        )
                    }
                }

                return
            }

            is AdsPolicyDecision.Allowed -> {
                startLoading(
                    adUnitId = targetAdUnitId ?: decision.adUnitId,
                    adUnitSource = if (targetAdUnitId != null) {
                        preferredAdUnitSource
                    } else {
                        decision.adUnitSource
                    }
                )
            }
        }
    }

    private fun startLoading(
        adUnitId: String,
        adUnitSource: AdUnitSource
    ) {
        val placement = AdPlacement.APP_OPEN
        val loadStartedAtMillis = currentTimeMillis()

        isLoadingAd = true

        mutableState.update {
            it.copy(
                isLoading = true,
                isAvailable = false,
                lastErrorMessage = null
            )
        }

        AdsLogger.d("Loading AppOpen ad")

        AppOpenAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    runOnMainThread {
                        isLoadingAd = false

                        val loadedAtMillis = currentTimeMillis()

                        appOpenAd.setOnPaidEventListener { adValue ->
                            analyticsTracker.track(
                                AdsAnalyticsEvent.PaidEvent(
                                    format = AdFormat.APP_OPEN,
                                    placement = placement,
                                    adUnitId = adUnitId,
                                    revenue = AdRevenueInfo(
                                        valueMicros = adValue.valueMicros,
                                        currencyCode = adValue.currencyCode,
                                        precisionType = adValue.precisionType
                                    )
                                )
                            )
                        }

                        cachedAd = CachedAd(
                            ad = appOpenAd,
                            adUnitId = adUnitId,
                            adUnitSource = adUnitSource,
                            loadedAtMillis = loadedAtMillis
                        )

                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                isAvailable = true,
                                lastLoadedAtMillis = loadedAtMillis,
                                lastErrorMessage = null
                            )
                        }

                        analyticsTracker.track(
                            AdsAnalyticsEvent.Loaded(
                                format = AdFormat.APP_OPEN,
                                placement = placement,
                                adUnitId = adUnitId,
                                adUnitSource = adUnitSource,
                                loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                            )
                        )

                        AdsLogger.i("AppOpen ad loaded")
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    runOnMainThread {
                        isLoadingAd = false
                        cachedAd = null

                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                isAvailable = false,
                                lastErrorMessage = loadAdError.message
                            )
                        }

                        analyticsTracker.track(
                            AdsAnalyticsEvent.LoadFailed(
                                format = AdFormat.APP_OPEN,
                                placement = placement,
                                adUnitId = adUnitId,
                                adUnitSource = adUnitSource,
                                error = loadAdError.toAdErrorInfo(),
                                loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                            )
                        )

                        AdsLogger.e("AppOpen failed to load: ${loadAdError.message}")
                    }
                }
            }
        )
    }

    private fun handleAdFinished(
        ad: AppOpenAd,
        wasShown: Boolean,
        shownAtMillis: Long
    ) {
        if (cachedAd?.ad === ad) {
            cachedAd = null
        }

        isShowingAd = false

        adsDisplayGuard.markFinished(
            format = AdFormat.APP_OPEN,
            placement = AdPlacement.APP_OPEN
        )

        val finalShownAtMillis = when {
            wasShown && shownAtMillis > 0L -> shownAtMillis
            wasShown -> currentTimeMillis()
            else -> 0L
        }

        if (wasShown) {
            lastShownAtMillis = finalShownAtMillis
        }

        mutableState.update {
            it.copy(
                isShowing = false,
                isAvailable = false,
                lastShownAtMillis = if (wasShown) {
                    finalShownAtMillis
                } else {
                    it.lastShownAtMillis
                }
            )
        }

        /**
         * Do not preload immediately inside this callback.
         *
         * Lifecycle observer / caller can decide when to preload next.
         * This avoids a race where a new AppOpen ad loads while Activity state
         * is still transitioning after dismiss.
         */
    }

    private fun getFreshCachedAd(
        expectedAdUnitId: String? = null
    ): CachedAd<AppOpenAd>? {
        val cached = cachedAd ?: return null
        val normalizedExpectedAdUnitId = expectedAdUnitId.cleanOrNull()

        if (
            normalizedExpectedAdUnitId != null &&
            cached.adUnitId != normalizedExpectedAdUnitId
        ) {
            AdsLogger.d(
                "Cached AppOpen ad unit does not match expected unit. " +
                        "cached=${cached.adUnitId}, expected=$normalizedExpectedAdUnitId"
            )
            return null
        }

        val maxAgeMillis = configStore.current.appOpenAdConfig.maxAdAgeMillis

        return if (cached.isFresh(maxAgeMillis = maxAgeMillis)) {
            cached
        } else {
            cachedAd = null

            mutableState.update {
                it.copy(
                    isAvailable = false,
                    lastErrorMessage = "Cached AppOpen ad expired."
                )
            }

            null
        }
    }

    private fun getCooldownRemainingMillis(): Long {
        if (lastShownAtMillis == 0L) return 0L

        val minInterval = configStore.current
            .appOpenAdConfig
            .minIntervalBetweenShowsMillis

        if (minInterval <= 0L) return 0L

        val elapsed = currentTimeMillis() - lastShownAtMillis
        val remaining = minInterval - elapsed

        return if (remaining > 0L) remaining else 0L
    }

    private fun LoadAdError.toAdErrorInfo(): AdErrorInfo {
        return AdErrorInfo(
            code = code,
            domain = domain,
            message = message,
            cause = cause?.message
        )
    }

    private fun AdError.toAdErrorInfo(): AdErrorInfo {
        return AdErrorInfo(
            code = code,
            domain = domain,
            message = message,
            cause = cause?.message
        )
    }

    private fun runOnMainThread(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }

    private fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    private fun resolveSplashAdUnitId(): String? {
        val config = configStore.current.appOpenAdConfig

        return config.splashAdUnitId.cleanOrNull()
            ?: config.adUnitId.cleanOrNull()
    }

    private fun resolveSplashAdUnitSource(
        splashAdUnitId: String
    ): AdUnitSource {
        val config = configStore.current.appOpenAdConfig

        return if (splashAdUnitId == config.splashAdUnitId.cleanOrNull()) {
            AdUnitSource.PLACEMENT
        } else {
            AdUnitSource.GLOBAL
        }
    }

    private fun String?.cleanOrNull(): String? {
        return this?.trim()?.takeIf { it.isNotBlank() }
    }
}