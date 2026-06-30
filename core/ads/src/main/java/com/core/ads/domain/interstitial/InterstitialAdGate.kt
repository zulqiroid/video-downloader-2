package com.core.ads.domain.interstitial

import android.app.Activity
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.screen.AdFeatureKey
import com.core.ads.domain.screen.AdScreenKey

interface InterstitialAdGate {

    fun preloadPlacement(
        placement: AdPlacement
    )

    fun preloadScreen(
        screenKey: AdScreenKey
    )

    fun preloadFeature(
        featureKey: AdFeatureKey
    )

    fun showForPlacement(
        activity: Activity?,
        placement: AdPlacement,
        triggerCount: Int = 1,
        onComplete: (InterstitialAdShowResult) -> Unit = {}
    )

    fun showForScreen(
        activity: Activity?,
        screenKey: AdScreenKey,
        onComplete: (InterstitialAdShowResult) -> Unit = {}
    )

    fun showForFeature(
        activity: Activity?,
        featureKey: AdFeatureKey,
        onComplete: (InterstitialAdShowResult) -> Unit = {}
    )

    fun showForTabSwitch(
        activity: Activity?,
        fromScreenKey: AdScreenKey?,
        toScreenKey: AdScreenKey,
        onComplete: (InterstitialAdShowResult) -> Unit = {}
    )

    fun showForBackNavigation(
        activity: Activity?,
        screenKey: AdScreenKey? = null,
        onComplete: (InterstitialAdShowResult) -> Unit = {}
    )

    fun clearCounters()
}