package com.core.ads.domain.interstitial

import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.screen.AdFeatureKey
import com.core.ads.domain.screen.AdScreenKey

object InterstitialPlacementKeys {

    val TAB_SWITCH = AdPlacement("tab_switch")
    val BACK_NAVIGATION = AdPlacement("back_navigation")

    fun fromScreen(
        screenKey: AdScreenKey
    ): AdPlacement {
        return AdPlacement(screenKey.value)
    }

    fun fromFeature(
        featureKey: AdFeatureKey
    ): AdPlacement {
        return AdPlacement(featureKey.value)
    }

    fun fromBackNavigation(
        screenKey: AdScreenKey? = null
    ): AdPlacement {
        return if (screenKey == null) {
            BACK_NAVIGATION
        } else {
            AdPlacement("${screenKey.value}_back_navigation")
        }
    }
}