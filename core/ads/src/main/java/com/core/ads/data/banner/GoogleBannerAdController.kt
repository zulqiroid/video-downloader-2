package com.core.ads.data.banner

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import com.core.ads.data.cache.PlacementStateStore
import com.core.ads.domain.BannerAdConfig
import com.core.ads.domain.ResolvedBannerAdConfig
import com.core.ads.domain.analytics.AdBlockReason
import com.core.ads.domain.analytics.AdErrorInfo
import com.core.ads.domain.analytics.AdRequestSource
import com.core.ads.domain.analytics.AdRevenueInfo
import com.core.ads.domain.analytics.AdUnitSource
import com.core.ads.domain.analytics.AdsAnalyticsEvent
import com.core.ads.domain.analytics.AdsAnalyticsTracker
import com.core.ads.domain.banner.BannerAdController
import com.core.ads.domain.banner.BannerAdState
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.policy.AdsOperation
import com.core.ads.domain.policy.AdsPolicyContext
import com.core.ads.domain.policy.AdsPolicyDecision
import com.core.ads.domain.policy.AdsPolicyEvaluator
import com.core.ads.utils.AdsLogger
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.LinkedHashMap

class GoogleBannerAdController(
    context: Context,
    private val configStore: AdsConfigStore,
    private val policyEvaluator: AdsPolicyEvaluator,
    private val analyticsTracker: AdsAnalyticsTracker
) : BannerAdController {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val stateStore = PlacementStateStore { BannerAdState() }

    private val runtimes = LinkedHashMap<String, BannerRuntime>()

    private val retryRunnables = mutableMapOf<String, Runnable>()

    private val retryAttempts = mutableMapOf<String, Int>()

    /**
     * Used to distinguish user/composition load request from internal retry request.
     */
    private val internalRetryLoadKeys = mutableSetOf<String>()

    override val states: StateFlow<Map<String, BannerAdState>> =
        stateStore.states

    override fun loadAndAttach(
        activity: Activity,
        container: ViewGroup,
        placement: AdPlacement,
        adWidthDp: Int
    ) {
        runOnMainThread {

            val placementKey = placement.value
            val isInternalRetryLoad = internalRetryLoadKeys.remove(placementKey)

            if (!isInternalRetryLoad) {
                cancelBannerRetry(placementKey)
                retryAttempts.remove(placementKey)
            }


            AdsLogger.d(
                "Banner loadAndAttach requested. " +
                        "placement=${placement.value}, width=$adWidthDp"
            )

            if (activity.isFinishing || activity.isDestroyed) {
                blockInvalidActivity(
                    placement = placement,
                    activity = activity
                )
                return@runOnMainThread
            }

            val resolvedConfig = configStore.current.resolveBannerConfig(placement)

            val existingRuntime = runtimes[placement.value]

            if (
                existingRuntime != null &&
                resolvedConfig != null &&
                existingRuntime.matches(
                    config = resolvedConfig,
                    adWidthDp = adWidthDp
                )
            ) {
                attachExistingBanner(
                    placement = placement,
                    container = container,
                    runtime = existingRuntime
                )

                trimDetachedBannerRuntimes(
                    exceptPlacementKey = placement.value
                )

                return@runOnMainThread
            }

            analyticsTracker.track(
                AdsAnalyticsEvent.LoadRequested(
                    format = AdFormat.BANNER,
                    placement = placement,
                    source = AdRequestSource.SCREEN_ENTER,
                    adUnitId = resolvedConfig?.adUnitId,
                    adUnitSource = resolvedConfig?.toAdUnitSource()
                )
            )

            val decision = policyEvaluator.evaluate(
                AdsPolicyContext(
                    operation = AdsOperation.LOAD,
                    format = AdFormat.BANNER,
                    placement = placement,
                    isLoading = existingRuntime?.isLoading == true,
                    isShowing = false,
                    hasFreshCache = existingRuntime?.isLoaded == true
                )
            )

            when (decision) {
                is AdsPolicyDecision.Blocked -> {
                    handleLoadBlocked(
                        placement = placement,
                        container = container,
                        decision = decision
                    )
                    return@runOnMainThread
                }

                is AdsPolicyDecision.Allowed -> {
                    val safeResolvedConfig = resolvedConfig ?: run {
                        handleMissingConfig(placement)
                        return@runOnMainThread
                    }

                    createLoadAndAttachBanner(
                        activity = activity,
                        container = container,
                        placement = placement,
                        config = safeResolvedConfig,
                        adUnitId = decision.adUnitId,
                        adUnitSource = decision.adUnitSource,
                        adWidthDp = adWidthDp
                    )
                }
            }
        }
    }

    override fun detach(
        placement: AdPlacement,
        container: ViewGroup?
    ) {
        runOnMainThread {
            val runtime = runtimes[placement.value] ?: return@runOnMainThread

            val attachedContainer = runtime.attachedContainerReference?.get()

            /**
             * If container is provided, detach only if caller owns the current attachment.
             *
             * This prevents old Compose onDispose from detaching a newly attached banner.
             */
            if (container != null && attachedContainer !== container) {
                AdsLogger.d(
                    "Banner detach ignored. placement=${placement.value}, " +
                            "caller does not own active container."
                )
                return@runOnMainThread
            }

            AdsLogger.d("Detaching banner. placement=${placement.value}")

            cancelBannerRetry(placement.value)

            (runtime.adView.parent as? ViewGroup)?.removeView(runtime.adView)

            runtime.attachedContainerReference = null
            runtime.detachedAtMillis = currentTimeMillis()
            runtime.lastAccessedAtMillis = currentTimeMillis()

            trimDetachedBannerRuntimes(
                exceptPlacementKey = placement.value
            )

            /**
             * Do not pause here.
             *
             * In Compose tab switching, detach is not Activity pause.
             * Pausing on every tab switch makes banners fragile and can leave them blank.
             *
             * Real Activity pause is handled by pause().
             */
        }
    }

    override fun resume(
        placement: AdPlacement
    ) {
        runOnMainThread {
            runtimes[placement.value]?.adView?.resume()
        }
    }

    override fun pause(
        placement: AdPlacement
    ) {
        runOnMainThread {
            runtimes[placement.value]?.adView?.pause()
        }
    }

    override fun destroy(
        placement: AdPlacement
    ) {
        runOnMainThread {
            AdsLogger.d("Destroying banner. placement=${placement.value}")

            cancelBannerRetry(placement.value)
            retryAttempts.remove(placement.value)
            internalRetryLoadKeys.remove(placement.value)

            removeAndDestroyRuntime(placement)
            stateStore.reset(placement)
        }
    }

    override fun destroyAll() {
        runOnMainThread {
            AdsLogger.d("Destroying all banners")

            val runtimeValues = runtimes.values.toList()
            runtimes.clear()

            runtimeValues.forEach { runtime ->
                destroyRuntime(runtime)
            }

            stateStore.clear()

            retryRunnables.values.forEach { runnable ->
                mainHandler.removeCallbacks(runnable)
            }
            retryRunnables.clear()
            retryAttempts.clear()
            internalRetryLoadKeys.clear()
        }
    }

    private fun createLoadAndAttachBanner(
        activity: Activity,
        container: ViewGroup,
        placement: AdPlacement,
        config: ResolvedBannerAdConfig,
        adUnitId: String,
        adUnitSource: AdUnitSource,
        adWidthDp: Int
    ) {
        removeAndDestroyRuntime(placement)
        container.removeAllViews()

        val runtimeId = UUID.randomUUID().toString()

        val adSize = resolveAdSize(
            activity = activity,
            config = config,
            adWidthDp = adWidthDp
        )

        val adView = AdView(activity).apply {
            this.adUnitId = adUnitId
            setAdSize(adSize)
        }

        val runtime = BannerRuntime(
            runtimeId = runtimeId,
            adView = adView,
            adUnitId = adUnitId,
            adUnitSource = adUnitSource,
            adWidthDp = adWidthDp,
            isAdaptive = config.isAdaptive,
            isCollapsible = config.isCollapsible,
            collapseGravity = config.collapseGravity,
            isLoading = true,
            isLoaded = false,
            retryAttempt = retryAttempts[placement.value] ?: 0,
            lastAccessedAtMillis = currentTimeMillis(),
            detachedAtMillis = null,
            attachedContainerReference = WeakReference(container)
        )

        runtimes[placement.value] = runtime

        trimDetachedBannerRuntimes(
            exceptPlacementKey = placement.value
        )

        attachAdViewToContainer(
            runtime = runtime,
            container = container
        )

        stateStore.update(placement) {
            it.copy(
                isLoading = true,
                isLoaded = false,
                errorMessage = null
            )
        }

        val loadStartedAtMillis = currentTimeMillis()

        adView.setOnPaidEventListener { adValue: AdValue ->
            if (!isCurrentRuntime(placement, runtimeId)) return@setOnPaidEventListener

            analyticsTracker.track(
                AdsAnalyticsEvent.PaidEvent(
                    format = AdFormat.BANNER,
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

        adView.adListener = object : AdListener() {

            override fun onAdLoaded() {
                runOnMainThread {
                    val currentRuntime = runtimes[placement.value]

                    if (currentRuntime?.runtimeId != runtimeId) {
                        AdsLogger.d(
                            "Ignoring stale banner onAdLoaded. placement=${placement.value}"
                        )
                        return@runOnMainThread
                    }

                    AdsLogger.i("Banner loaded. placement=${placement.value}")

                    currentRuntime.isLoading = false
                    currentRuntime.isLoaded = true
                    currentRuntime.loadedAtMillis = currentTimeMillis()

                    cancelBannerRetry(placement.value)
                    retryAttempts.remove(placement.value)

                    stateStore.update(placement) {
                        it.copy(
                            isLoading = false,
                            isLoaded = true,
                            errorMessage = null
                        )
                    }

                    analyticsTracker.track(
                        AdsAnalyticsEvent.Loaded(
                            format = AdFormat.BANNER,
                            placement = placement,
                            adUnitId = adUnitId,
                            adUnitSource = adUnitSource,
                            loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                        )
                    )
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                runOnMainThread {
                    val currentRuntime = runtimes[placement.value]

                    if (currentRuntime?.runtimeId != runtimeId) {
                        AdsLogger.d(
                            "Ignoring stale banner onAdFailedToLoad. placement=${placement.value}"
                        )
                        return@runOnMainThread
                    }

                    AdsLogger.w(
                        "Banner failed. " +
                                "placement=${placement.value}, error=${loadAdError.message}"
                    )

                    stateStore.update(placement) {
                        it.copy(
                            isLoading = false,
                            isLoaded = false,
                            errorMessage = loadAdError.message
                        )
                    }

                    analyticsTracker.track(
                        AdsAnalyticsEvent.LoadFailed(
                            format = AdFormat.BANNER,
                            placement = placement,
                            adUnitId = adUnitId,
                            adUnitSource = adUnitSource,
                            error = loadAdError.toAdErrorInfo(),
                            loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                        )
                    )

                    removeAndDestroyRuntime(placement)
                    container.removeAllViews()

                    val nextRetryAttempt = currentRuntime.retryAttempt + 1

                    removeAndDestroyRuntime(placement)
                    container.removeAllViews()

                    scheduleBannerRetryIfNeeded(
                        activity = activity,
                        container = container,
                        placement = placement,
                        adWidthDp = adWidthDp,
                        nextRetryAttempt = nextRetryAttempt,
                        errorMessage = loadAdError.message
                    )
                }
            }

            override fun onAdImpression() {
                if (!isCurrentRuntime(placement, runtimeId)) return

                analyticsTracker.track(
                    AdsAnalyticsEvent.Impression(
                        format = AdFormat.BANNER,
                        placement = placement,
                        adUnitId = adUnitId
                    )
                )
            }

            override fun onAdClicked() {
                if (!isCurrentRuntime(placement, runtimeId)) return

                analyticsTracker.track(
                    AdsAnalyticsEvent.Clicked(
                        format = AdFormat.BANNER,
                        placement = placement,
                        adUnitId = adUnitId
                    )
                )
            }

            override fun onAdOpened() {
                AdsLogger.d("Banner opened. placement=${placement.value}")
            }

            override fun onAdClosed() {
                AdsLogger.d("Banner closed. placement=${placement.value}")
            }
        }

        AdsLogger.d(
            "Loading banner. placement=${placement.value}, " +
                    "runtimeId=$runtimeId, adUnitSource=$adUnitSource"
        )

        adView.loadAd(
            buildAdRequest(config)
        )
    }

    private fun attachExistingBanner(
        placement: AdPlacement,
        container: ViewGroup,
        runtime: BannerRuntime
    ) {
        AdsLogger.d(
            "Reusing existing banner. placement=${placement.value}, " +
                    "runtimeId=${runtime.runtimeId}, loaded=${runtime.isLoaded}, loading=${runtime.isLoading}"
        )

        runtime.attachedContainerReference = WeakReference(container)
        runtime.lastAccessedAtMillis = currentTimeMillis()
        runtime.detachedAtMillis = null

        attachAdViewToContainer(
            runtime = runtime,
            container = container
        )

        runtime.adView.resume()

        stateStore.update(placement) {
            it.copy(
                isLoading = runtime.isLoading,
                isLoaded = runtime.isLoaded,
                errorMessage = null
            )
        }
    }

    private fun attachAdViewToContainer(
        runtime: BannerRuntime,
        container: ViewGroup
    ) {
        val adView = runtime.adView
        val currentParent = adView.parent as? ViewGroup

        if (currentParent === container) {
            runtime.attachedContainerReference = WeakReference(container)
            runtime.lastAccessedAtMillis = currentTimeMillis()
            runtime.detachedAtMillis = null
            return
        }

        currentParent?.removeView(adView)
        container.removeAllViews()

        container.addView(
            adView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        runtime.attachedContainerReference = WeakReference(container)
        runtime.lastAccessedAtMillis = currentTimeMillis()
        runtime.detachedAtMillis = null
    }

    private fun handleLoadBlocked(
        placement: AdPlacement,
        container: ViewGroup,
        decision: AdsPolicyDecision.Blocked
    ) {
        AdsLogger.w(
            "Banner load blocked. " +
                    "placement=${placement.value}, " +
                    "reason=${decision.reason}, message=${decision.message}"
        )

        analyticsTracker.track(
            AdsAnalyticsEvent.LoadBlocked(
                format = AdFormat.BANNER,
                placement = placement,
                reason = decision.reason,
                message = decision.message
            )
        )

        stateStore.update(placement) {
            it.copy(
                isLoading = false,
                isLoaded = false,
                errorMessage = decision.message
            )
        }

        /**
         * Only clear container, do not destroy existing runtime for harmless states.
         * Runtime cleaner handles global/premium/consent cleanup.
         */
        container.removeAllViews()
    }

    private fun handleMissingConfig(
        placement: AdPlacement
    ) {
        val message = "Resolved banner config became null."

        stateStore.update(placement) {
            it.copy(
                isLoading = false,
                isLoaded = false,
                errorMessage = message
            )
        }

        analyticsTracker.track(
            AdsAnalyticsEvent.LoadBlocked(
                format = AdFormat.BANNER,
                placement = placement,
                reason = AdBlockReason.AD_UNIT_ID_MISSING,
                message = message
            )
        )
    }

    private fun blockInvalidActivity(
        placement: AdPlacement,
        activity: Activity
    ) {
        val reason = if (activity.isFinishing) {
            AdBlockReason.ACTIVITY_FINISHING
        } else {
            AdBlockReason.ACTIVITY_DESTROYED
        }

        val message = "Banner load blocked because Activity is invalid."

        stateStore.update(placement) {
            it.copy(
                isLoading = false,
                isLoaded = false,
                errorMessage = message
            )
        }

        analyticsTracker.track(
            AdsAnalyticsEvent.LoadBlocked(
                format = AdFormat.BANNER,
                placement = placement,
                reason = reason,
                message = message
            )
        )
    }

    private fun scheduleBannerRetryIfNeeded(
        activity: Activity,
        container: ViewGroup,
        placement: AdPlacement,
        adWidthDp: Int,
        nextRetryAttempt: Int,
        errorMessage: String
    ) {
        val bannerConfig = configStore.current.bannerAdConfig
        val placementKey = placement.value

        if (!bannerConfig.retryOnFailure) {
            AdsLogger.d(
                "Banner retry disabled by config. placement=$placementKey"
            )
            retryAttempts.remove(placementKey)
            return
        }

        if (nextRetryAttempt > bannerConfig.maxLoadRetryCount) {
            AdsLogger.d(
                "Banner retry limit reached. " +
                        "placement=$placementKey, attempts=$nextRetryAttempt, " +
                        "max=${bannerConfig.maxLoadRetryCount}, lastError=$errorMessage"
            )
            retryAttempts.remove(placementKey)
            return
        }

        val delayMillis = calculateRetryDelayMillis(
            attempt = nextRetryAttempt,
            initialDelayMillis = bannerConfig.initialRetryDelayMillis,
            maxDelayMillis = bannerConfig.maxRetryDelayMillis
        )

        cancelBannerRetry(placementKey)

        val activityReference = WeakReference(activity)
        val containerReference = WeakReference(container)

        val retryRunnable = Runnable {
            retryRunnables.remove(placementKey)

            val latestActivity = activityReference.get()
            val latestContainer = containerReference.get()

            if (
                latestActivity == null ||
                latestActivity.isFinishing ||
                latestActivity.isDestroyed
            ) {
                AdsLogger.d(
                    "Banner retry cancelled because Activity is invalid. placement=$placementKey"
                )
                retryAttempts.remove(placementKey)
                return@Runnable
            }

            if (latestContainer == null || latestContainer.parent == null) {
                AdsLogger.d(
                    "Banner retry cancelled because container is detached. placement=$placementKey"
                )
                retryAttempts.remove(placementKey)
                return@Runnable
            }

            if (configStore.current.resolveBannerConfig(placement) == null) {
                AdsLogger.d(
                    "Banner retry cancelled because config no longer allows placement=$placementKey"
                )
                retryAttempts.remove(placementKey)
                return@Runnable
            }

            AdsLogger.d(
                "Retrying banner load. " +
                        "placement=$placementKey, attempt=$nextRetryAttempt, delay=${delayMillis}ms"
            )

            retryAttempts[placementKey] = nextRetryAttempt
            internalRetryLoadKeys.add(placementKey)

            loadAndAttach(
                activity = latestActivity,
                container = latestContainer,
                placement = placement,
                adWidthDp = adWidthDp
            )
        }

        retryAttempts[placementKey] = nextRetryAttempt
        retryRunnables[placementKey] = retryRunnable

        AdsLogger.d(
            "Scheduled banner retry. " +
                    "placement=$placementKey, attempt=$nextRetryAttempt, delay=${delayMillis}ms"
        )

        mainHandler.postDelayed(
            retryRunnable,
            delayMillis
        )
    }

    private fun cancelBannerRetry(
        placementKey: String
    ) {
        val runnable = retryRunnables.remove(placementKey) ?: return
        mainHandler.removeCallbacks(runnable)

        AdsLogger.d("Cancelled banner retry. placement=$placementKey")
    }

    private fun calculateRetryDelayMillis(
        attempt: Int,
        initialDelayMillis: Long,
        maxDelayMillis: Long
    ): Long {
        var delayMillis = initialDelayMillis

        repeat((attempt - 1).coerceAtLeast(0)) {
            delayMillis = (delayMillis * 2L).coerceAtMost(maxDelayMillis)
        }

        return delayMillis.coerceAtMost(maxDelayMillis)
    }

    private fun trimDetachedBannerRuntimes(
        exceptPlacementKey: String? = null
    ) {
        val bannerConfig = configStore.current.bannerAdConfig

        destroyExpiredDetachedRuntimes(
            maxDetachedRuntimeAgeMillis = bannerConfig.maxDetachedRuntimeAgeMillis,
            exceptPlacementKey = exceptPlacementKey
        )

        enforceMaxCachedRuntimeLimit(
            maxCachedRuntimes = bannerConfig.maxCachedRuntimes,
            exceptPlacementKey = exceptPlacementKey
        )
    }

    private fun destroyExpiredDetachedRuntimes(
        maxDetachedRuntimeAgeMillis: Long,
        exceptPlacementKey: String?
    ) {
        val now = currentTimeMillis()

        val expiredPlacementKeys = runtimes
            .filter { (placementKey, runtime) ->
                placementKey != exceptPlacementKey &&
                        runtime.isDetached() &&
                        runtime.detachedAtMillis != null &&
                        now - runtime.detachedAtMillis!! >= maxDetachedRuntimeAgeMillis
            }
            .keys
            .toList()

        expiredPlacementKeys.forEach { placementKey ->
            AdsLogger.d(
                "Destroying expired detached banner runtime. placement=$placementKey"
            )

            removeAndDestroyRuntimeByKey(
                placementKey = placementKey
            )
        }
    }

    private fun enforceMaxCachedRuntimeLimit(
        maxCachedRuntimes: Int,
        exceptPlacementKey: String?
    ) {
        if (runtimes.size <= maxCachedRuntimes) return

        val overflowCount = runtimes.size - maxCachedRuntimes

        val removablePlacementKeys = runtimes
            .filter { (placementKey, runtime) ->
                placementKey != exceptPlacementKey &&
                        runtime.isDetached()
            }
            .toList()
            .sortedBy { (_, runtime) ->
                runtime.lastAccessedAtMillis
            }
            .take(overflowCount)
            .map { (placementKey, _) ->
                placementKey
            }

        removablePlacementKeys.forEach { placementKey ->
            AdsLogger.d(
                "Destroying LRU detached banner runtime. " +
                        "placement=$placementKey, maxCachedRuntimes=$maxCachedRuntimes"
            )

            removeAndDestroyRuntimeByKey(
                placementKey = placementKey
            )
        }
    }

    private fun removeAndDestroyRuntimeByKey(
        placementKey: String
    ) {
        val runtime = runtimes.remove(placementKey) ?: return
        destroyRuntime(runtime)
    }

    private fun removeAndDestroyRuntime(
        placement: AdPlacement
    ) {
        val runtime = runtimes.remove(placement.value) ?: return
        destroyRuntime(runtime)
    }

    private fun destroyRuntime(
        runtime: BannerRuntime
    ) {
        runCatching {
            (runtime.adView.parent as? ViewGroup)?.removeView(runtime.adView)
            runtime.adView.destroy()
        }.onFailure { throwable ->
            AdsLogger.e(
                "Failed to destroy banner runtime=${runtime.runtimeId}",
                throwable
            )
        }
    }

    private fun isCurrentRuntime(
        placement: AdPlacement,
        runtimeId: String
    ): Boolean {
        return runtimes[placement.value]?.runtimeId == runtimeId
    }

    private fun resolveAdSize(
        activity: Activity,
        config: ResolvedBannerAdConfig,
        adWidthDp: Int
    ): AdSize {
        return if (config.isAdaptive) {
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                activity,
                adWidthDp
            )
        } else {
            AdSize.BANNER
        }
    }

    private fun buildAdRequest(
        config: ResolvedBannerAdConfig
    ): AdRequest {
        val builder = AdRequest.Builder()

        if (config.isCollapsible) {
            val collapsibleGravity = when (config.collapseGravity) {
                BannerAdConfig.CollapseGravity.BOTTOM -> "bottom"
                else -> "top"
            }

            val extras = Bundle().apply {
                putString("collapsible", collapsibleGravity)
            }

            builder.addNetworkExtrasBundle(
                AdMobAdapter::class.java,
                extras
            )
        }

        return builder.build()
    }

    private fun ResolvedBannerAdConfig.toAdUnitSource(): AdUnitSource {
        return if (isUsingPlacementAdUnitId) {
            AdUnitSource.PLACEMENT
        } else {
            AdUnitSource.GLOBAL
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

    private data class BannerRuntime(
        val runtimeId: String,
        val adView: AdView,
        val adUnitId: String,
        val adUnitSource: AdUnitSource,
        val adWidthDp: Int,
        val isAdaptive: Boolean,
        val isCollapsible: Boolean,
        val collapseGravity: Int,
        var isLoading: Boolean,
        var isLoaded: Boolean,
        var retryAttempt: Int,
        var loadedAtMillis: Long? = null,
        var lastAccessedAtMillis: Long,
        var detachedAtMillis: Long? = null,
        var attachedContainerReference: WeakReference<ViewGroup>? = null
    ) {

        fun matches(
            config: ResolvedBannerAdConfig,
            adWidthDp: Int
        ): Boolean {
            return this.adUnitId == config.adUnitId &&
                    this.adWidthDp == adWidthDp &&
                    this.isAdaptive == config.isAdaptive &&
                    this.isCollapsible == config.isCollapsible &&
                    this.collapseGravity == config.collapseGravity
        }
        fun isDetached(): Boolean {
            return attachedContainerReference?.get() == null ||
                    adView.parent == null
        }
    }
}