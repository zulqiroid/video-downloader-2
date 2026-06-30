package com.core.ads.domain.manager

import android.app.Activity
import com.core.ads.domain.AdsCoreConfig
import com.core.ads.domain.appopen.AppOpenAdShowResult
import com.core.ads.domain.appopen.AppOpenAdState
import com.core.ads.domain.banner.BannerAdState
import com.core.ads.domain.init.AdsInitializationState
import com.core.ads.domain.init.AdsSdkState
import com.core.ads.domain.interstitial.InterstitialAdShowResult
import com.core.ads.domain.interstitial.InterstitialAdState
import com.core.ads.domain.loading.AdLoadingUiState
import com.core.ads.domain.nativead.NativeAdState
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.rewarded.RewardItem
import com.core.ads.domain.rewarded.RewardedAdShowResult
import com.core.ads.domain.rewarded.RewardedAdState
import com.core.ads.domain.runtime.AdsCleanupReason
import kotlinx.coroutines.flow.StateFlow

/**
 * App-facing facade for the ads module.
 *
 * App module should prefer using this instead of injecting every controller directly.
 */
interface AdsManager {

    val configState: StateFlow<AdsCoreConfig>

    val currentConfig: AdsCoreConfig

    val adLoadingUiState: StateFlow<AdLoadingUiState>

    val initializationState: StateFlow<AdsInitializationState>

    val sdkState: StateFlow<AdsSdkState>

    val appOpenState: StateFlow<AppOpenAdState>

    val interstitialStates: StateFlow<Map<String, InterstitialAdState>>

    val rewardedStates: StateFlow<Map<String, RewardedAdState>>

    val bannerStates: StateFlow<Map<String, BannerAdState>>

    val nativeStates: StateFlow<Map<String, NativeAdState>>

    /**
     * Starts consent + SDK initialization + runtime monitor.
     *
     * Safe to call multiple times.
     */
    fun initialize()

    /**
     * Updates ads config from Remote Config/backend/local config.
     *
     * Runtime monitor will react and clear caches if needed.
     */
    fun updateConfig(config: AdsCoreConfig)

    /**
     * AppOpen.
     */
    fun preloadAppOpen()

    fun preloadSplashAppOpen()

    fun showAppOpenIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit = {}
    )

    fun showSplashAppOpenIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit = {}
    )

    /**
     * Interstitial.
     */
    fun preloadInterstitial(
        placement: AdPlacement = AdPlacement.DEFAULT_INTERSTITIAL
    )

    fun showInterstitialIfAvailable(
        activity: Activity,
        placement: AdPlacement = AdPlacement.DEFAULT_INTERSTITIAL,
        onComplete: (InterstitialAdShowResult) -> Unit = {}
    )

    /**
     * Rewarded.
     */
    fun preloadRewarded(
        placement: AdPlacement = AdPlacement.DEFAULT_REWARDED
    )

    fun showRewardedIfAvailable(
        activity: Activity,
        placement: AdPlacement = AdPlacement.DEFAULT_REWARDED,
        onComplete: (RewardedAdShowResult) -> Unit = {},
        onRewardEarned: (RewardItem) -> Unit = {}
    )

    /**
     * Clear all ads manually.
     *
     * Useful for:
     * - user becomes premium
     * - remove ads purchased
     * - logout
     * - consent revoke
     * - debug reset
     */
    fun clearAllAds(
        reason: AdsCleanupReason = AdsCleanupReason.MANUAL,
        message: String? = null
    )

    fun clearAll()

    /**
     * Convenience method for premium/remove-ads activation.
     */
    fun clearAllAdsForPremiumUser(
        message: String = "Premium/remove-ads user should not keep cached ads."
    )
}