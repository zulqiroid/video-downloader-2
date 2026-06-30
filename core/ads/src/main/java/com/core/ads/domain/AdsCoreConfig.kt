package com.core.ads.domain

import com.core.ads.domain.appopen.AppOpenAdConfig
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.screen.AdScreenKey
import com.core.ads.domain.screen.ScreenAdsConfig

/**
 * Top-level ads config.
 *
 * This should be updated from Remote Config.
 * Controllers should read it from AdsConfigStore, not receive it directly.
 */
data class AdsCoreConfig(
    /**
     * Main kill switch for the whole ads module.
     */
    val adsEnabled: Boolean = true,

    val screenAdsConfig: ScreenAdsConfig = ScreenAdsConfig(),

    /**
     * Debug mode for logs, test devices, and debug config.
     */
    val isDebug: Boolean = false,

    /**
     * Temporary request kill switch.
     *
     * Useful if Remote Config wants to stop new ad requests
     * without fully disabling the whole ads module state.
     */
    val canRequestAds: Boolean = true,

    val appOpenAdConfig: AppOpenAdConfig = AppOpenAdConfig(),

    val interstitialAdConfig: InterstitialAdConfig = InterstitialAdConfig(),

    val rewardedAdConfig: RewardedAdConfig = RewardedAdConfig(),

    val bannerAdConfig: BannerAdConfig = BannerAdConfig(),

    val nativeAdConfig: NativeAdConfig = NativeAdConfig(),

    val adLoadingDialogConfig: AdLoadingDialogConfig = AdLoadingDialogConfig()
) {

    val canUseAppOpenAd: Boolean
        get() = adsEnabled &&
                canRequestAds &&
                appOpenAdConfig.isUsable

    fun resolveInterstitialConfig(
        placement: AdPlacement
    ): ResolvedInterstitialAdConfig? {
        if (!adsEnabled || !canRequestAds) return null
        return interstitialAdConfig.resolve(placement)
    }

    fun resolveRewardedConfig(
        placement: AdPlacement
    ): ResolvedRewardedAdConfig? {
        if (!adsEnabled || !canRequestAds) return null
        return rewardedAdConfig.resolve(placement)
    }

    fun resolveBannerConfig(
        placement: AdPlacement
    ): ResolvedBannerAdConfig? {
        if (!adsEnabled || !canRequestAds) return null
        return bannerAdConfig.resolve(placement)
    }

    fun resolveNativeConfig(
        placement: AdPlacement
    ): ResolvedNativeAdConfig? {
        if (!adsEnabled || !canRequestAds) return null
        return nativeAdConfig.resolve(placement)
    }



    fun canUseInterstitialAd(placement: AdPlacement): Boolean {
        return resolveInterstitialConfig(placement) != null
    }

    fun canUseRewardedAd(placement: AdPlacement): Boolean {
        return resolveRewardedConfig(placement) != null
    }

    fun canUseBannerAd(placement: AdPlacement): Boolean {
        return resolveBannerConfig(placement) != null
    }

    fun canUseNativeAd(placement: AdPlacement): Boolean {
        return resolveNativeConfig(placement) != null
    }

    fun getScreenAdsConfig(screenKey: AdScreenKey) =
        screenAdsConfig.getScreenConfig(screenKey)

    fun resolveScreenBanner(screenKey: AdScreenKey) =
        screenAdsConfig.resolveBanner(screenKey)

    fun resolveScreenNative(screenKey: AdScreenKey) =
        screenAdsConfig.resolveNative(screenKey)

    fun resolveScreenInterstitial(screenKey: AdScreenKey) =
        screenAdsConfig.resolveInterstitial(screenKey)
}