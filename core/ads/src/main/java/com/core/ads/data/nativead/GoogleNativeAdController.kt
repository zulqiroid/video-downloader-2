package com.core.ads.data.nativead

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.core.ads.data.cache.CachedAd
import com.core.ads.data.cache.PlacementAdCache
import com.core.ads.data.cache.PlacementLoadingStore
import com.core.ads.data.cache.PlacementStateStore
import com.core.ads.domain.NativeAdSize
import com.core.ads.domain.ResolvedNativeAdConfig
import com.core.ads.domain.analytics.AdBlockReason
import com.core.ads.domain.analytics.AdErrorInfo
import com.core.ads.domain.analytics.AdRequestSource
import com.core.ads.domain.analytics.AdRevenueInfo
import com.core.ads.domain.analytics.AdUnitSource
import com.core.ads.domain.analytics.AdsAnalyticsEvent
import com.core.ads.domain.analytics.AdsAnalyticsTracker
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.nativead.NativeAdBinding
import com.core.ads.domain.nativead.NativeAdController
import com.core.ads.domain.nativead.NativeAdState
import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.policy.AdsOperation
import com.core.ads.domain.policy.AdsPolicyContext
import com.core.ads.domain.policy.AdsPolicyDecision
import com.core.ads.domain.policy.AdsPolicyEvaluator
import com.core.ads.utils.AdsLogger
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

class GoogleNativeAdController(
    context: Context,
    private val configStore: AdsConfigStore,
    private val policyEvaluator: AdsPolicyEvaluator,
    private val analyticsTracker: AdsAnalyticsTracker
) : NativeAdController {

    private val appContext: Context = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private val cache = PlacementAdCache<NativeAd>(
        onDestroyAd = { nativeAd -> nativeAd.destroy() }
    )

    private val stateStore = PlacementStateStore { NativeAdState() }
    private val loadingStore = PlacementLoadingStore()

    /**
     * One latest pending request per placement.
     *
     * NativeAd should not be delivered to multiple NativeAdViews at the same time.
     * If Compose recomposes during loading, we replace old callback with latest callback.
     */
    private val pendingRequests = mutableMapOf<String, NativePendingRequest>()

    override val states: StateFlow<Map<String, NativeAdState>> =
        stateStore.states

    override fun loadAd(
        activity: Activity,
        placement: AdPlacement,
        onLoaded: (NativeAdBinding) -> Unit,
        onError: (String) -> Unit
    ) {
        runOnMainThread {
            AdsLogger.d("Native load requested. placement=${placement.value}")

            if (activity.isFinishing || activity.isDestroyed) {
                val message = "Native load blocked because Activity is invalid."

                stateStore.update(placement) {
                    it.copy(
                        isLoading = false,
                        isLoaded = false,
                        errorMessage = message
                    )
                }

                analyticsTracker.track(
                    AdsAnalyticsEvent.LoadBlocked(
                        format = AdFormat.NATIVE,
                        placement = placement,
                        reason = if (activity.isFinishing) {
                            AdBlockReason.ACTIVITY_FINISHING
                        } else {
                            AdBlockReason.ACTIVITY_DESTROYED
                        },
                        message = message
                    )
                )

                onError(message)
                return@runOnMainThread
            }

            val resolvedConfig = configStore.current.resolveNativeConfig(placement)

            analyticsTracker.track(
                AdsAnalyticsEvent.LoadRequested(
                    format = AdFormat.NATIVE,
                    placement = placement,
                    source = AdRequestSource.SCREEN_ENTER,
                    adUnitId = resolvedConfig?.adUnitId,
                    adUnitSource = resolvedConfig?.toAdUnitSource()
                )
            )

            /**
             * For Native loadAd(), we do policy check first, but we do not let
             * "fresh cache exists" block the request. If policy allows, we may
             * return cached ad immediately below.
             */
            val decision = policyEvaluator.evaluate(
                AdsPolicyContext(
                    operation = AdsOperation.LOAD,
                    format = AdFormat.NATIVE,
                    placement = placement,
                    isLoading = false,
                    isShowing = false,
                    hasFreshCache = false
                )
            )

            when (decision) {
                is AdsPolicyDecision.Blocked -> {
                    AdsLogger.w(
                        "Native load blocked. " +
                                "placement=${placement.value}, " +
                                "reason=${decision.reason}, message=${decision.message}"
                    )

                    analyticsTracker.track(
                        AdsAnalyticsEvent.LoadBlocked(
                            format = AdFormat.NATIVE,
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

                    cache.remove(placement)
                    pendingRequests.remove(placement.value)
                    onError(decision.message)

                    return@runOnMainThread
                }

                is AdsPolicyDecision.Allowed -> {
                    val safeResolvedConfig = resolvedConfig ?: run {
                        val message = "Resolved native config became null."

                        stateStore.update(placement) {
                            it.copy(
                                isLoading = false,
                                isLoaded = false,
                                errorMessage = message
                            )
                        }

                        analyticsTracker.track(
                            AdsAnalyticsEvent.LoadBlocked(
                                format = AdFormat.NATIVE,
                                placement = placement,
                                reason = AdBlockReason.AD_UNIT_ID_MISSING,
                                message = message
                            )
                        )

                        onError(message)
                        return@runOnMainThread
                    }

                    val freshCachedAd = cache.takeFreshAd(
                        placement = placement,
                        maxAgeMillis = safeResolvedConfig.maxAdAgeMillis
                    )

                    if (freshCachedAd != null) {
                        AdsLogger.d(
                            "Returning cached native ad. placement=${placement.value}"
                        )

                        analyticsTracker.track(
                            AdsAnalyticsEvent.LoadBlocked(
                                format = AdFormat.NATIVE,
                                placement = placement,
                                reason = AdBlockReason.CACHE_AVAILABLE,
                                message = "Returning fresh cached native ad without new SDK request."
                            )
                        )

                        val binding = createBinding(
                            activity = activity,
                            nativeAd = freshCachedAd.ad,
                            config = safeResolvedConfig
                        )

                        stateStore.update(placement) {
                            it.copy(
                                isLoading = false,
                                isLoaded = true,
                                errorMessage = null
                            )
                        }

                        onLoaded(binding)
                        return@runOnMainThread
                    }

                    if (loadingStore.isLoading(placement)) {
                        AdsLogger.d(
                            "Native already loading. Replacing pending callback. " +
                                    "placement=${placement.value}"
                        )

                        pendingRequests[placement.value] = NativePendingRequest(
                            activityReference = WeakReference(activity),
                            onLoaded = onLoaded,
                            onError = onError
                        )

                        return@runOnMainThread
                    }

                    pendingRequests[placement.value] = NativePendingRequest(
                        activityReference = WeakReference(activity),
                        onLoaded = onLoaded,
                        onError = onError
                    )

                    startLoading(
                        placement = placement,
                        config = safeResolvedConfig,
                        adUnitId = decision.adUnitId,
                        adUnitSource = decision.adUnitSource
                    )
                }
            }
        }
    }

    override fun cancel(
        placement: AdPlacement
    ) {
        runOnMainThread {
            AdsLogger.d("Cancelling native pending request. placement=${placement.value}")
            pendingRequests.remove(placement.value)
        }
    }

    override fun clear(
        placement: AdPlacement
    ) {
        runOnMainThread {
            AdsLogger.d("Clearing native placement=${placement.value}")

            cache.remove(placement)
            loadingStore.markNotLoading(placement)
            pendingRequests.remove(placement.value)
            stateStore.reset(placement)
        }
    }

    override fun clearAll() {
        runOnMainThread {
            AdsLogger.d("Clearing all native ads")

            cache.clear()
            loadingStore.clear()
            pendingRequests.clear()
            stateStore.clear()
        }
    }

    private fun startLoading(
        placement: AdPlacement,
        config: ResolvedNativeAdConfig,
        adUnitId: String,
        adUnitSource: AdUnitSource
    ) {
        val loadStartedAtMillis = currentTimeMillis()

        loadingStore.markLoading(placement)

        stateStore.update(placement) {
            it.copy(
                isLoading = true,
                isLoaded = false,
                errorMessage = null
            )
        }

        AdsLogger.d(
            "Loading native ad. placement=${placement.value}, " +
                    "size=${config.size}, adUnitSource=$adUnitSource"
        )

        val adLoader = AdLoader.Builder(appContext, adUnitId)
            .forNativeAd { nativeAd ->
                runOnMainThread {
                    loadingStore.markNotLoading(placement)

                    nativeAd.setOnPaidEventListener { adValue: AdValue ->
                        analyticsTracker.track(
                            AdsAnalyticsEvent.PaidEvent(
                                format = AdFormat.NATIVE,
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

                    val cachedAd = CachedAd(
                        ad = nativeAd,
                        adUnitId = adUnitId,
                        adUnitSource = adUnitSource,
                        loadedAtMillis = currentTimeMillis()
                    )

                    analyticsTracker.track(
                        AdsAnalyticsEvent.Loaded(
                            format = AdFormat.NATIVE,
                            placement = placement,
                            adUnitId = adUnitId,
                            adUnitSource = adUnitSource,
                            loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                        )
                    )

                    val pendingRequest = pendingRequests.remove(placement.value)

                    if (pendingRequest == null) {
                        /**
                         * This can happen if a preload-style request is added later
                         * or if UI disappeared before load completed.
                         */
                        cache.put(
                            placement = placement,
                            cachedAd = cachedAd
                        )

                        stateStore.update(placement) {
                            it.copy(
                                isLoading = false,
                                isLoaded = true,
                                errorMessage = null
                            )
                        }

                        AdsLogger.i(
                            "Native loaded and cached. placement=${placement.value}"
                        )

                        return@runOnMainThread
                    }

                    val activity = pendingRequest.activityReference.get()

                    if (activity == null || activity.isFinishing || activity.isDestroyed) {
                        cache.put(
                            placement = placement,
                            cachedAd = cachedAd
                        )

                        stateStore.update(placement) {
                            it.copy(
                                isLoading = false,
                                isLoaded = true,
                                errorMessage = null
                            )
                        }

                        AdsLogger.w(
                            "Native loaded but Activity unavailable. Cached for later. " +
                                    "placement=${placement.value}"
                        )

                        return@runOnMainThread
                    }

                    /**
                     * Ownership transfers to NativeAdBinding.
                     * Controller does NOT keep this NativeAd in cache after delivery.
                     */
                    val binding = createBinding(
                        activity = activity,
                        nativeAd = nativeAd,
                        config = config
                    )

                    stateStore.update(placement) {
                        it.copy(
                            isLoading = false,
                            isLoaded = true,
                            errorMessage = null
                        )
                    }

                    AdsLogger.i(
                        "Native loaded and delivered. placement=${placement.value}"
                    )

                    pendingRequest.onLoaded(binding)
                }
            }
            .withAdListener(object : AdListener() {

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    runOnMainThread {
                        loadingStore.markNotLoading(placement)
                        cache.remove(placement)

                        val pendingRequest = pendingRequests.remove(placement.value)

                        stateStore.update(placement) {
                            it.copy(
                                isLoading = false,
                                isLoaded = false,
                                errorMessage = loadAdError.message
                            )
                        }

                        analyticsTracker.track(
                            AdsAnalyticsEvent.LoadFailed(
                                format = AdFormat.NATIVE,
                                placement = placement,
                                adUnitId = adUnitId,
                                adUnitSource = adUnitSource,
                                error = loadAdError.toAdErrorInfo(),
                                loadDurationMillis = currentTimeMillis() - loadStartedAtMillis
                            )
                        )

                        AdsLogger.e(
                            "Native failed to load. " +
                                    "placement=${placement.value}, error=${loadAdError.message}"
                        )

                        pendingRequest?.onError?.invoke(loadAdError.message)
                    }
                }

                override fun onAdImpression() {
                    analyticsTracker.track(
                        AdsAnalyticsEvent.Impression(
                            format = AdFormat.NATIVE,
                            placement = placement,
                            adUnitId = adUnitId
                        )
                    )
                }

                override fun onAdClicked() {
                    analyticsTracker.track(
                        AdsAnalyticsEvent.Clicked(
                            format = AdFormat.NATIVE,
                            placement = placement,
                            adUnitId = adUnitId
                        )
                    )
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setRequestMultipleImages(false)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun createBinding(
        activity: Activity,
        nativeAd: NativeAd,
        config: ResolvedNativeAdConfig
    ): NativeAdBinding {
        return GoogleNativeAdBinding(
            activity = activity,
            nativeAd = nativeAd,
            config = config
        )
    }

    private fun ResolvedNativeAdConfig.toAdUnitSource(): AdUnitSource {
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

    private data class NativePendingRequest(
        val activityReference: WeakReference<Activity>,
        val onLoaded: (NativeAdBinding) -> Unit,
        val onError: (String) -> Unit
    )
}