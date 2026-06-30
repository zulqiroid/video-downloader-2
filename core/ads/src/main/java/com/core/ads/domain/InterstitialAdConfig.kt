package com.core.ads.domain

import com.core.ads.domain.placement.AdPlacement

data class InterstitialAdConfig(
    val enabled: Boolean = false,

    /**
     * Global/fallback interstitial ID.
     */
    val adUnitId: String? = null,

    /**
     * Optional special IDs.
     */
    val splashUnitId: String? = null,
    val languageUnitId: String? = null,
    val introUnitId: String? = null,
    val premiumUnitId: String? = null,

    val globalMinIntervalBetweenShowsMillis: Long =
        DEFAULT_MIN_INTERVAL_BETWEEN_SHOWS_MILLIS,

    val maxAdAgeMillis: Long = DEFAULT_MAX_AD_AGE_MILLIS,

    val minIntervalBetweenShowsMillis: Long =
        DEFAULT_MIN_INTERVAL_BETWEEN_SHOWS_MILLIS,

    val retryConfig: CommonAdRetryConfig = CommonAdRetryConfig(),

    /**
     * Trigger rules.
     */
    val triggers: InterstitialTriggerConfig = InterstitialTriggerConfig(),

    /**
     * Per-screen enable/disable for tab/screen based interstitial.
     */
    val screens: Map<String, InterstitialScreenConfig> = emptyMap(),

    /**
     * Optional placement-specific unit ID overrides.
     */
    val placements: Map<String, InterstitialPlacementConfig> = emptyMap(),

    val features: Map<String, InterstitialFeatureConfig> = emptyMap(),
) {

    fun resolve(placement: AdPlacement): ResolvedInterstitialAdConfig? {
        if (!enabled) return null

        val placementConfig = placements[placement.value]

        if (placementConfig?.enabled == false) return null

        val resolvedAdUnitId = placementConfig
            ?.adUnitId
            ?.takeIf { it.isNotBlank() }
            ?: resolveSpecialUnitId(placement)
            ?: adUnitId?.takeIf { it.isNotBlank() }
            ?: return null

        return ResolvedInterstitialAdConfig(
            placement = placement,
            adUnitId = resolvedAdUnitId,
            maxAdAgeMillis = placementConfig?.maxAdAgeMillis ?: maxAdAgeMillis,
            minIntervalBetweenShowsMillis = placementConfig?.minIntervalBetweenShowsMillis
                ?: minIntervalBetweenShowsMillis,
            globalMinIntervalBetweenShowsMillis = globalMinIntervalBetweenShowsMillis,
            isUsingPlacementAdUnitId = !placementConfig?.adUnitId.isNullOrBlank()
        )
    }

    fun isPlacementUsable(placement: AdPlacement): Boolean {
        return resolve(placement) != null
    }

    fun isScreenEnabled(screenKey: String): Boolean {
        return screens[screenKey]?.enabled ?: false
    }

    private fun resolveSpecialUnitId(placement: AdPlacement): String? {
        return when (placement.value) {
            "splash" -> splashUnitId
            "language", "app_language" -> languageUnitId
            "intro", "onboarding" -> introUnitId
            "premium" -> premiumUnitId
            else -> null
        }?.takeIf { it.isNotBlank() }
    }

    fun getFeatureConfig(featureKey: String): InterstitialFeatureConfig {
        return features[featureKey] ?: InterstitialFeatureConfig()
    }

    fun isFeatureEnabled(featureKey: String): Boolean {
        return getFeatureConfig(featureKey).enabled
    }

    companion object {
        private const val ONE_MINUTE_MILLIS = 60 * 1000L
        private const val ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS

        const val DEFAULT_MAX_AD_AGE_MILLIS: Long = 4 * ONE_HOUR_MILLIS
        const val DEFAULT_MIN_INTERVAL_BETWEEN_SHOWS_MILLIS: Long = ONE_MINUTE_MILLIS
    }
}

data class InterstitialTriggerConfig(
    val showOnTabSwitch: Boolean = false,
    val tabSwitchTriggerCount: Int = 1,

    val showOnPremium: Boolean = false,
    val premiumTriggerCount: Int = 1,

    val showOnOnboarding: Boolean = false,
    val onboardingTriggerCount: Int = 1,

    val showOnLanguage: Boolean = false,
    val languageTriggerCount: Int = 1,

    val showOnImageResult: Boolean = false,
    val imageResultTriggerCount: Int = 1,

    val showOnBackNavigation: Boolean = false,
    val backNavigationTriggerCount: Int = 1
)

data class InterstitialScreenConfig(
    val enabled: Boolean = false,

    /**
     * Screen-specific trigger count.
     */
    val triggerCount: Int = 1,

    /**
     * Screen-specific interstitial unit.
     * Falls back to global interstitialAdConfig.adUnitId.
     */
    val adUnitId: String? = null,

    /**
     * Optional screen-specific ad cache age override.
     */
    val maxAdAgeMillis: Long? = null,

    /**
     * Optional screen-specific interval override.
     */
    val minIntervalBetweenShowsMillis: Long? = null
)
data class InterstitialPlacementConfig(
    val enabled: Boolean? = null,
    val adUnitId: String? = null,
    val maxAdAgeMillis: Long? = null,
    val minIntervalBetweenShowsMillis: Long? = null
)

data class ResolvedInterstitialAdConfig(
    val placement: AdPlacement,
    val adUnitId: String,
    val maxAdAgeMillis: Long,
    val minIntervalBetweenShowsMillis: Long,
    val globalMinIntervalBetweenShowsMillis: Long,
    val isUsingPlacementAdUnitId: Boolean
)

data class InterstitialFeatureConfig(
    val enabled: Boolean = false,
    val triggerCount: Int = 1,
    val adUnitId: String? = null
)