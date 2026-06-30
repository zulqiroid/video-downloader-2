package com.core.ads.data.interstitial

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.core.ads.data.cache.AdShowTimestampStore
import com.core.ads.data.cache.CachedAd
import com.core.ads.data.cache.PlacementAdCache
import com.core.ads.data.cache.PlacementLoadingStore
import com.core.ads.data.cache.PlacementShowingStore
import com.core.ads.data.cache.PlacementStateStore
import com.core.ads.domain.analytics.AdBlockReason
import com.core.ads.domain.analytics.AdErrorInfo
import com.core.ads.domain.analytics.AdRequestSource
import com.core.ads.domain.analytics.AdRevenueInfo
import com.core.ads.domain.analytics.AdsAnalyticsEvent
import com.core.ads.domain.analytics.AdsAnalyticsTracker
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.display.AdsDisplayGuard
import com.core.ads.domain.display.FullScreenAdTransitionGuard
import com.core.ads.domain.interstitial.InterstitialAdController
import com.core.ads.domain.interstitial.InterstitialAdShowResult
import com.core.ads.domain.interstitial.InterstitialAdState
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
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.StateFlow

class GoogleInterstitialAdController(
    context: Context,
    private val configStore: AdsConfigStore,
    private val policyEvaluator: AdsPolicyEvaluator,
    private val analyticsTracker: AdsAnalyticsTracker,
    private val adsDisplayGuard: AdsDisplayGuard,
    private val fullScreenAdTransitionGuard: FullScreenAdTransitionGuard
) : InterstitialAdController {

    private val appContext: Context = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private val cache = PlacementAdCache<InterstitialAd>()
    private val stateStore = PlacementStateStore { InterstitialAdState() }
    private val loadingStore = PlacementLoadingStore()
    private val showingStore = PlacementShowingStore()
    private val showTimestampStore = AdShowTimestampStore()

    override val states: StateFlow<Map<String, InterstitialAdState>> =
        stateStore.states

    override fun preload(
        placement: AdPlacement
    ) {
        runOnMainThread {
            loadAdIfNeeded(
                placement = placement,
                source = AdRequestSource.MANUAL_PRELOAD
            )
        }
    }

    override fun showIfAvailable(
        activity: Activity,
        placement: AdPlacement,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        runOnMainThread {
            AdsLogger.d("Interstitial show requested. placement=${placement.value}")

            val resolvedConfig = configStore.current.resolveInterstitialConfig(placement)
            val cachedAd = resolvedConfig?.let {
                cache.getFreshAd(
                    placement = placement,
                    maxAgeMillis = it.maxAdAgeMillis
                )
            }

            val showCooldownRemainingMillis = resolvedConfig?.let {
                showTimestampStore.getEffectiveRemainingCooldownMillis(
                    placement = placement,
                    placementMinIntervalMillis = it.minIntervalBetweenShowsMillis,
                    globalMinIntervalMillis = it.globalMinIntervalBetweenShowsMillis
                )
            } ?: 0L

            analyticsTracker.track(
                AdsAnalyticsEvent.ShowRequested(
                    format = AdFormat.INTERSTITIAL,
                    placement = placement,
                    adUnitId = cachedAd?.adUnitId ?: resolvedConfig?.adUnitId,
                    adUnitSource = cachedAd?.adUnitSource ?: resolvedConfig?.toAdUnitSource()
                )
            )

            val decision = policyEvaluator.evaluate(
                AdsPolicyContext(
                    operation = AdsOperation.SHOW,
                    format = AdFormat.INTERSTITIAL,
                    placement = placement,
                    activity = activity,
                    isLoading = loadingStore.isLoading(placement),
                    isShowing = showingStore.isAnyShowing(),
                    hasFreshCache = cachedAd != null,
                    cooldownRemainingMillis = showCooldownRemainingMillis
                )
            )

            when (decision) {
                is AdsPolicyDecision.Blocked -> {
                    AdsLogger.w(
                        "Interstitial show blocked. " +
                                "placement=${placement.value}, reason=${decision.reason}, message=${decision.message}"
                    )

                    stateStore.update(placement) {
                        it.copy(lastErrorMessage = decision.message)
                    }

                    analyticsTracker.track(
                        AdsAnalyticsEvent.ShowBlocked(
                            format = AdFormat.INTERSTITIAL,
                            placement = placement,
                            reason = decision.reason,
                            message = decision.message
                        )
                    )

                    if (decision.reason == AdBlockReason.CACHE_NOT_AVAILABLE) {
                        onComplete(InterstitialAdShowResult.NotAvailable)

                        loadAdIfNeeded(
                            placement = placement,
                            source = AdRequestSource.AUTO_PRELOAD
                        )
                    } else {
                        onComplete(
                            InterstitialAdShowResult.Skipped(
                                reason = decision.message
                            )
                        )
                    }

                    return@runOnMainThread
                }

                is AdsPolicyDecision.Allowed -> Unit
            }

            val adToShow = cachedAd ?: run {
                val message = "Interstitial cached ad became unavailable before show."
                AdsLogger.w(message)

                analyticsTracker.track(
                    AdsAnalyticsEvent.ShowBlocked(
                        format = AdFormat.INTERSTITIAL,
                        placement = placement,
                        reason = AdBlockReason.CACHE_NOT_AVAILABLE,
                        message = message
                    )
                )

                onComplete(InterstitialAdShowResult.NotAvailable)

                loadAdIfNeeded(
                    placement = placement,
                    source = AdRequestSource.AUTO_PRELOAD
                )

                return@runOnMainThread
            }

            val displayMarked = adsDisplayGuard.markShowing(
                format = AdFormat.INTERSTITIAL,
                placement = placement
            )

            if (!displayMarked) {
                val message = "Another full-screen ad is already showing."

                analyticsTracker.track(
                    AdsAnalyticsEvent.ShowBlocked(
                        format = AdFormat.INTERSTITIAL,
                        placement = placement,
                        reason = AdBlockReason.DISPLAY_GUARD_BLOCKED,
                        message = message
                    )
                )

                onComplete(
                    InterstitialAdShowResult.Skipped(
                        reason = message
                    )
                )

                return@runOnMainThread
            }

            showingStore.markShowing(placement)

            stateStore.update(placement) {
                it.copy(
                    isShowing = true,
                    lastErrorMessage = null
                )
            }

            analyticsTracker.track(
                AdsAnalyticsEvent.ShowEligible(
                    format = AdFormat.INTERSTITIAL,
                    placement = placement,
                    adUnitId = adToShow.adUnitId,
                    adUnitSource = adToShow.adUnitSource
                )
            )

            var completed = false
            var adActuallyShown = false
            var shownAtMillis = 0L

            fun completeOnce(result: InterstitialAdShowResult) {
                if (completed) return
                completed = true
                AdsLogger.d(
                    "Interstitial complete. placement=${placement.value}, result=$result"
                )
                onComplete(result)
            }

            adToShow.ad.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdShowedFullScreenContent() {
                    adActuallyShown = true
                    shownAtMillis = currentTimeMillis()

                    AdsLogger.i("Interstitial shown. placement=${placement.value}")

                    analyticsTracker.track(
                        AdsAnalyticsEvent.ShowStarted(
                            format = AdFormat.INTERSTITIAL,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )
                }

                override fun onAdImpression() {
                    analyticsTracker.track(
                        AdsAnalyticsEvent.Impression(
                            format = AdFormat.INTERSTITIAL,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )
                }

                override fun onAdClicked() {
                    analyticsTracker.track(
                        AdsAnalyticsEvent.Clicked(
                            format = AdFormat.INTERSTITIAL,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )
                }

                override fun onAdDismissedFullScreenContent() {
                    AdsLogger.i("Interstitial dismissed. placement=${placement.value}")

                    fullScreenAdTransitionGuard.suppressAppOpenFor(
                        durationMillis = 10_000L,
                        reason = "Interstitial dismissed."
                    )

                    analyticsTracker.track(
                        AdsAnalyticsEvent.Dismissed(
                            format = AdFormat.INTERSTITIAL,
                            placement = placement,
                            adUnitId = adToShow.adUnitId
                        )
                    )

                    handleAdFinished(
                        placement = placement,
                        ad = adToShow.ad,
                        wasShown = adActuallyShown,
                        shownAtMillis = shownAtMillis
                    )

                    completeOnce(InterstitialAdShowResult.Shown)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    AdsLogger.e(
                        "Interstitial failed to show. " +
                                "placement=${placement.value}, error=${adError.message}"
                    )

                    fullScreenAdTransitionGuard.suppressAppOpenFor(
                        durationMillis = 5_000L,
                        reason = "Interstitial failed to show."
                    )

                    analyticsTracker.track(
                        AdsAnalyticsEvent.ShowFailed(
                            format = AdFormat.INTERSTITIAL,
                            placement = placement,
                            adUnitId = adToShow.adUnitId,
                            error = adError.toAdErrorInfo()
                        )
                    )

                    handleAdFinished(
                        placement = placement,
                        ad = adToShow.ad,
                        wasShown = false,
                        shownAtMillis = 0L
                    )

                    stateStore.update(placement) {
                        it.copy(lastErrorMessage = adError.message)
                    }

                    completeOnce(
                        InterstitialAdShowResult.Failed(
                            reason = adError.message
                        )
                    )
                }
            }

            try {

                fullScreenAdTransitionGuard.suppressAppOpenFor(
                    durationMillis = 10_000L,
                    reason = "Interstitial is showing or closing."
                )


                adToShow.ad.show(activity)
            } catch (exception: Exception) {
                val message = "Exception during interstitial show: ${exception.message}"

                AdsLogger.e(message, exception)

                analyticsTracker.track(
                    AdsAnalyticsEvent.ShowFailed(
                        format = AdFormat.INTERSTITIAL,
                        placement = placement,
                        adUnitId = adToShow.adUnitId,
                        error = AdErrorInfo(
                            message = message,
                            cause = exception::class.java.simpleName
                        )
                    )
                )

                handleAdFinished(
                    placement = placement,
                    ad = adToShow.ad,
                    wasShown = false,
                    shownAtMillis = 0L
                )

                stateStore.update(placement) {
                    it.copy(lastErrorMessage = message)
                }

                completeOnce(
                    InterstitialAdShowResult.Failed(
                        reason = message
                    )
                )
            }
        }
    }

    override fun clear(
        placement: AdPlacement
    ) {
        runOnMainThread {
            AdsLogger.d("Clearing interstitial placement=${placement.value}")

            cache.remove(placement)
            loadingStore.markNotLoading(placement)
            showingStore.markNotShowing(placement)
            showTimestampStore.clear(placement)
            stateStore.reset(placement)
        }
    }

    override fun clearAll() {
        runOnMainThread {
            AdsLogger.d("Clearing all interstitial ads")

            cache.clear()
            loadingStore.clear()
            showingStore.clear()
            showTimestampStore.clearAll()
            stateStore.clear()
        }
    }

    private fun loadAdIfNeeded(
        placement: AdPlacement,
        source: AdRequestSource
    ) {
        val resolvedConfig = configStore.current.resolveInterstitialConfig(placement)

        val hasFreshCache = resolvedConfig?.let {
            cache.hasFreshAd(
                placement = placement,
                maxAgeMillis = it.maxAdAgeMillis
            )
        } ?: false

        analyticsTracker.track(
            AdsAnalyticsEvent.LoadRequested(
                format = AdFormat.INTERSTITIAL,
                placement = placement,
                source = source,
                adUnitId = resolvedConfig?.adUnitId,
                adUnitSource = resolvedConfig?.toAdUnitSource()
            )
        )

        val decision = policyEvaluator.evaluate(
            AdsPolicyContext(
                operation = AdsOperation.LOAD,
                format = AdFormat.INTERSTITIAL,
                placement = placement,
                isLoading = loadingStore.isLoading(placement),
                isShowing = showingStore.isAnyShowing(),
                hasFreshCache = hasFreshCache
            )
        )

        when (decision) {
            is AdsPolicyDecision.Blocked -> {
                AdsLogger.w(
                    "Interstitial load blocked. " +
                            "placement=${placement.value}, reason=${decision.reason}, message=${decision.message}"
                )

                analyticsTracker.track(
                    AdsAnalyticsEvent.LoadBlocked(
                        format = AdFormat.INTERSTITIAL,
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
                    stateStore.update(placement) {
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
                    placement = placement,
                    adUnitId = decision.adUnitId,
                    adUnitSource = decision.adUnitSource
                )
            }
        }
    }

    private fun startLoading(
        placement: AdPlacement,
        adUnitId: String,
        adUnitSource: com.core.ads.domain.analytics.AdUnitSource
    ) {
        val loadStartedAtMillis = currentTimeMillis()

        loadingStore.markLoading(placement)

        stateStore.update(placement) {
            it.copy(
                isLoading = true,
                isAvailable = false,
                lastErrorMessage = null
            )
        }

        AdsLogger.d(
            "Loading interstitial. placement=${placement.value}, adUnitSource=$adUnitSource"
        )

        InterstitialAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    runOnMainThread {
                        loadingStore.markNotLoading(placement)

                        val cachedAd = CachedAd(
                            ad = interstitialAd,
                            adUnitId = adUnitId,
                            adUnitSource = adUnitSource,
                            loadedAtMillis = currentTimeMillis()
                        )

                        interstitialAd.setOnPaidEventListener { adValue ->
                            analyticsTracker.track(
                                AdsAnalyticsEvent.PaidEvent(
                                    format = AdFormat.INTERSTITIAL,
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

                        cache.put(
                            placement = placement,
                            cachedAd = cachedAd
                        )

                        stateStore.update(placement) {
                            it.copy(
                                isLoading = false,
                                isAvailable = true,
                                lastLoadedAtMillis = cachedAd.loadedAtMillis,
                                lastErrorMessage = null
                            )
                        }

                        analyticsTracker.track(
                            AdsAnalyticsEvent.Loaded(
                                format = AdFormat.INTERSTITIAL,
                                placement = placement,
                                adUnitId = adUnitId,
                                adUnitSource = adUnitSource,
                                loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                            )
                        )

                        AdsLogger.i(
                            "Interstitial loaded. placement=${placement.value}"
                        )
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    runOnMainThread {
                        loadingStore.markNotLoading(placement)
                        cache.remove(placement)

                        stateStore.update(placement) {
                            it.copy(
                                isLoading = false,
                                isAvailable = false,
                                lastErrorMessage = loadAdError.message
                            )
                        }

                        analyticsTracker.track(
                            AdsAnalyticsEvent.LoadFailed(
                                format = AdFormat.INTERSTITIAL,
                                placement = placement,
                                adUnitId = adUnitId,
                                adUnitSource = adUnitSource,
                                error = loadAdError.toAdErrorInfo(),
                                loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                            )
                        )

                        AdsLogger.e(
                            "Interstitial failed to load. " +
                                    "placement=${placement.value}, error=${loadAdError.message}"
                        )
                    }
                }
            }
        )
    }

    private fun handleAdFinished(
        placement: AdPlacement,
        ad: InterstitialAd,
        wasShown: Boolean,
        shownAtMillis: Long
    ) {
        val cached = cache.get(placement)

        if (cached?.ad === ad) {
            cache.remove(placement)
        }

        showingStore.markNotShowing(placement)

        adsDisplayGuard.markFinished(
            format = AdFormat.INTERSTITIAL,
            placement = placement
        )

        val finalShownAtMillis = when {
            wasShown && shownAtMillis > 0L -> shownAtMillis
            wasShown -> currentTimeMillis()
            else -> 0L
        }

        if (wasShown) {
            showTimestampStore.markShown(
                placement = placement,
                shownAtMillis = finalShownAtMillis
            )
        }

        stateStore.update(placement) {
            it.copy(
                isShowing = false,
                isAvailable = false,
                lastShownAtMillis = if (wasShown) finalShownAtMillis else it.lastShownAtMillis
            )
        }

        loadAdIfNeeded(
            placement = placement,
            source = AdRequestSource.AUTO_PRELOAD
        )
    }

    private fun com.core.ads.domain.ResolvedInterstitialAdConfig.toAdUnitSource():
            com.core.ads.domain.analytics.AdUnitSource {
        return if (isUsingPlacementAdUnitId) {
            com.core.ads.domain.analytics.AdUnitSource.PLACEMENT
        } else {
            com.core.ads.domain.analytics.AdUnitSource.GLOBAL
        }
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
}