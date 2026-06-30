package com.core.ads.domain.screen

import com.core.ads.domain.NativeAdSize
import com.core.ads.domain.placement.AdPlacement

data class ScreenAdsConfig(
    val screens: Map<String, ScreenAdConfig> = emptyMap()
) {

    fun getScreenConfig(screenKey: AdScreenKey): ScreenAdConfig {
        return screens[screenKey.value] ?: ScreenAdConfig()
    }

    fun resolveBanner(screenKey: AdScreenKey): ScreenBannerAdConfig? {
        val config = getScreenConfig(screenKey).banner
        return config.takeIf { it.enabled && it.placement != null }
    }

    fun resolveNative(screenKey: AdScreenKey): ScreenNativeAdConfig? {
        val config = getScreenConfig(screenKey).native
        return config.takeIf { it.enabled && it.placement != null }
    }

    fun resolveInterstitial(screenKey: AdScreenKey): ScreenInterstitialAdConfig? {
        val config = getScreenConfig(screenKey).interstitial
        return config.takeIf { it.enabled && it.placement != null }
    }
}

data class ScreenAdConfig(
    val banner: ScreenBannerAdConfig = ScreenBannerAdConfig(),
    val native: ScreenNativeAdConfig = ScreenNativeAdConfig(),
    val interstitial: ScreenInterstitialAdConfig = ScreenInterstitialAdConfig()
)

data class ScreenBannerAdConfig(
    val enabled: Boolean = false,
    val placement: AdPlacement? = null,
    val position: AdSlotPosition = AdSlotPosition.BOTTOM
)

data class ScreenNativeAdConfig(
    val enabled: Boolean = false,
    val placement: AdPlacement? = null,
    val size: NativeAdSize = NativeAdSize.MEDIUM,
    val position: AdSlotPosition = AdSlotPosition.BOTTOM
)

data class ScreenInterstitialAdConfig(
    val enabled: Boolean = false,
    val placement: AdPlacement? = null,

    /**
     * Show when user enters a screen/tab.
     */
    val showOnScreenEnter: Boolean = false,

    /**
     * Show when user selects a bottom tab.
     */
    val showOnTabSelected: Boolean = false,

    /**
     * Optional: show on back/exit action.
     */
    val showOnBackPressed: Boolean = false
)