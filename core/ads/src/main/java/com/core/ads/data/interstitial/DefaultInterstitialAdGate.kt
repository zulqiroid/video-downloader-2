package com.core.ads.data.interstitial

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.core.ads.domain.interstitial.InterstitialAdGate
import com.core.ads.domain.interstitial.InterstitialAdShowResult
import com.core.ads.domain.interstitial.InterstitialPlacementKeys
import com.core.ads.domain.manager.AdsManager
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.screen.AdFeatureKey
import com.core.ads.domain.screen.AdScreenKey
import com.core.ads.utils.AdsLogger

class DefaultInterstitialAdGate(
    private val adsManager: AdsManager
) : InterstitialAdGate {

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Key = logical trigger key.
     *
     * Example:
     * - tab_switch
     * - app_language_continue
     * - video_to_mp3_entry
     */
    private val triggerCounters = mutableMapOf<String, Int>()

    /**
     * Full-screen ad show pipeline should be one-at-a-time.
     *
     * Controller also has display guard, but this prevents duplicate app-level
     * show requests caused by double clicks / duplicate nav events.
     */
    private var isShowRequestInFlight: Boolean = false

    override fun preloadPlacement(
        placement: AdPlacement
    ) {
        runOnMainThread {
            if (!adsManager.currentConfig.canUseInterstitialAd(placement)) {
                AdsLogger.d(
                    "Interstitial preload skipped. placement=${placement.value} is not usable."
                )
                return@runOnMainThread
            }

            adsManager.preloadInterstitial(placement)
        }
    }

    override fun preloadScreen(
        screenKey: AdScreenKey
    ) {
        preloadPlacement(
            placement = InterstitialPlacementKeys.fromScreen(screenKey)
        )
    }

    override fun preloadFeature(
        featureKey: AdFeatureKey
    ) {
        preloadPlacement(
            placement = InterstitialPlacementKeys.fromFeature(featureKey)
        )
    }

    override fun showForPlacement(
        activity: Activity?,
        placement: AdPlacement,
        triggerCount: Int,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        runOnMainThread {
            showForPlacementInternal(
                activity = activity,
                placement = placement,
                triggerCount = triggerCount,
                triggerKey = placement.value,
                onComplete = onComplete
            )
        }
    }

    override fun showForScreen(
        activity: Activity?,
        screenKey: AdScreenKey,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        runOnMainThread {
            val interstitialConfig = adsManager
                .currentConfig
                .interstitialAdConfig

            val screenConfig = interstitialConfig.screens[screenKey.value]

            if (screenConfig?.enabled != true) {
                completeSkipped(
                    reason = "Interstitial screen trigger disabled. screen=${screenKey.value}",
                    onComplete = onComplete
                )
                return@runOnMainThread
            }

            showForPlacementInternal(
                activity = activity,
                placement = InterstitialPlacementKeys.fromScreen(screenKey),
                triggerCount = screenConfig.triggerCount,
                triggerKey = screenKey.value,
                onComplete = onComplete
            )
        }
    }

    override fun showForFeature(
        activity: Activity?,
        featureKey: AdFeatureKey,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        runOnMainThread {
            val interstitialConfig = adsManager
                .currentConfig
                .interstitialAdConfig

            val featureConfig = interstitialConfig.getFeatureConfig(featureKey.value)

            if (!featureConfig.enabled) {
                completeSkipped(
                    reason = "Interstitial feature trigger disabled. feature=${featureKey.value}",
                    onComplete = onComplete
                )
                return@runOnMainThread
            }

            showForPlacementInternal(
                activity = activity,
                placement = InterstitialPlacementKeys.fromFeature(featureKey),
                triggerCount = featureConfig.triggerCount,
                triggerKey = featureKey.value,
                onComplete = onComplete
            )
        }
    }

    override fun showForTabSwitch(
        activity: Activity?,
        fromScreenKey: AdScreenKey?,
        toScreenKey: AdScreenKey,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        runOnMainThread {
            val triggers = adsManager
                .currentConfig
                .interstitialAdConfig
                .triggers

            if (!triggers.showOnTabSwitch) {
                completeSkipped(
                    reason = "Interstitial tab switch trigger disabled.",
                    onComplete = onComplete
                )
                return@runOnMainThread
            }

            if (fromScreenKey?.value == toScreenKey.value) {
                completeSkipped(
                    reason = "Interstitial tab switch skipped because selected tab is same.",
                    onComplete = onComplete
                )
                return@runOnMainThread
            }

            showForPlacementInternal(
                activity = activity,
                placement = InterstitialPlacementKeys.TAB_SWITCH,
                triggerCount = triggers.tabSwitchTriggerCount,
                triggerKey = InterstitialPlacementKeys.TAB_SWITCH.value,
                onComplete = onComplete
            )
        }
    }

    override fun showForBackNavigation(
        activity: Activity?,
        screenKey: AdScreenKey?,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        runOnMainThread {
            val triggers = adsManager
                .currentConfig
                .interstitialAdConfig
                .triggers

            if (!triggers.showOnBackNavigation) {
                completeSkipped(
                    reason = "Interstitial back navigation trigger disabled.",
                    onComplete = onComplete
                )
                return@runOnMainThread
            }

            showForPlacementInternal(
                activity = activity,
                placement = InterstitialPlacementKeys.fromBackNavigation(screenKey),
                triggerCount = triggers.backNavigationTriggerCount,
                triggerKey = screenKey?.value?.let { "${it}_back_navigation" }
                    ?: InterstitialPlacementKeys.BACK_NAVIGATION.value,
                onComplete = onComplete
            )
        }
    }

    override fun clearCounters() {
        runOnMainThread {
            triggerCounters.clear()
            isShowRequestInFlight = false
        }
    }

    private fun showForPlacementInternal(
        activity: Activity?,
        placement: AdPlacement,
        triggerCount: Int,
        triggerKey: String,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        if (activity == null) {
            completeSkipped(
                reason = "Interstitial skipped because Activity is null. placement=${placement.value}",
                onComplete = onComplete
            )
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            completeSkipped(
                reason = "Interstitial skipped because Activity is finishing/destroyed. placement=${placement.value}",
                onComplete = onComplete
            )
            return
        }

        if (!adsManager.currentConfig.canUseInterstitialAd(placement)) {
            completeSkipped(
                reason = "Interstitial placement is not usable. placement=${placement.value}",
                onComplete = onComplete
            )
            return
        }

        val safeTriggerCount = triggerCount.coerceAtLeast(1)

        if (!hasTriggerReached(triggerKey, safeTriggerCount)) {
            AdsLogger.d(
                "Interstitial trigger not reached. " +
                        "key=$triggerKey, triggerCount=$safeTriggerCount"
            )

            /**
             * Warm up the ad while user continues normal flow.
             */
            adsManager.preloadInterstitial(placement)

            completeSkipped(
                reason = "Interstitial trigger not reached. key=$triggerKey",
                onComplete = onComplete
            )
            return
        }

        if (isShowRequestInFlight) {
            completeSkipped(
                reason = "Another interstitial show request is already in flight.",
                onComplete = onComplete
            )
            return
        }

        isShowRequestInFlight = true

        AdsLogger.d(
            "Interstitial gate passed. placement=${placement.value}, triggerKey=$triggerKey"
        )

        adsManager.showInterstitialIfAvailable(
            activity = activity,
            placement = placement,
            onComplete = { result ->
                isShowRequestInFlight = false

                if (result is InterstitialAdShowResult.NotAvailable) {
                    adsManager.preloadInterstitial(placement)
                }

                onComplete(result)
            }
        )
    }

    private fun hasTriggerReached(
        triggerKey: String,
        triggerCount: Int
    ): Boolean {
        if (triggerCount <= 1) {
            triggerCounters[triggerKey] = 0
            return true
        }

        val nextCount = (triggerCounters[triggerKey] ?: 0) + 1

        return if (nextCount >= triggerCount) {
            triggerCounters[triggerKey] = 0
            true
        } else {
            triggerCounters[triggerKey] = nextCount
            false
        }
    }

    private fun completeSkipped(
        reason: String,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        AdsLogger.d(reason)

        onComplete(
            InterstitialAdShowResult.Skipped(
                reason = reason
            )
        )
    }

    private fun runOnMainThread(
        block: () -> Unit
    ) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }
}