package com.core.ads.domain

import com.core.ads.domain.placement.AdPlacement

data class NativeAdConfig(
    val enabled: Boolean = false,

    /**
     * Global/fallback native ID.
     */
    val adUnitId: String? = null,

    val size: NativeAdSize = NativeAdSize.MEDIUM,

    val maxAdAgeMillis: Long = DEFAULT_MAX_AD_AGE_MILLIS,

    val retryConfig: CommonAdRetryConfig = CommonAdRetryConfig(),

    val styleConfig: NativeAdStyleConfig = NativeAdStyleConfig(),

    /**
     * Native placements from Remote Config.
     *
     * Example:
     * placements["home"]
     * placements["app_language_list"]
     */
    val placements: Map<String, NativePlacementConfig> = emptyMap()
) {

    fun resolve(placement: AdPlacement): ResolvedNativeAdConfig? {
        if (!enabled) return null

        /**
         * Native ads must be explicitly declared in Remote Config.
         *
         * If placement is missing, do not fallback to global native_ad_id.
         *
         * Example:
         * placements["create"] missing
         * → native ad must not show on create screen
         */
        val placementConfig = placements[placement.value] ?: return null

        /**
         * Only enabled=true means usable.
         *
         * enabled=false or enabled=null both mean disabled.
         */
        if (placementConfig.enabled != true) return null

        val resolvedAdUnitId = placementConfig
            .adUnitId
            ?.takeIf { it.isNotBlank() }
            ?: adUnitId?.takeIf { it.isNotBlank() }
            ?: return null

        return ResolvedNativeAdConfig(
            placement = placement,
            adUnitId = resolvedAdUnitId,
            size = placementConfig.size ?: size,
            position = placementConfig.position ?: AdContentPosition.BOTTOM,
            showPlaceholder = placementConfig.showPlaceholder ?: false,
            maxAdAgeMillis = placementConfig.maxAdAgeMillis ?: maxAdAgeMillis,
            isUsingPlacementAdUnitId = !placementConfig.adUnitId.isNullOrBlank(),
            styleConfig = styleConfig
        )
    }

    fun isPlacementUsable(placement: AdPlacement): Boolean {
        return resolve(placement) != null
    }

    companion object {
        const val DEFAULT_MAX_AD_AGE_MILLIS: Long = 60 * 60 * 1000L
    }
}

data class NativePlacementConfig(
    val enabled: Boolean? = null,
    val adUnitId: String? = null,
    val size: NativeAdSize? = null,
    val position: AdContentPosition? = null,
    val showPlaceholder: Boolean? = null,
    val maxAdAgeMillis: Long? = null
)

data class ResolvedNativeAdConfig(
    val placement: AdPlacement,
    val adUnitId: String,
    val size: NativeAdSize,
    val position: AdContentPosition,
    val showPlaceholder: Boolean,
    val maxAdAgeMillis: Long,
    val isUsingPlacementAdUnitId: Boolean,

    /**
     * Remote-config-driven styling.
     */
    val styleConfig: NativeAdStyleConfig
)

enum class NativeAdSize {
    SMALL,
    MEDIUM,
    LARGE
}

data class NativeAdStyleConfig(
    val containerBackgroundColor: String = "#FFFFFF",
    val containerBorderColor: String = "#E5E7EB",
    val containerBorderWidthDp: Int = 0,

    val mediaBackgroundColor: String = "#FFFFFF",

    val headlineTextColor: String = "#0F172A",
    val bodyTextColor: String = "#475569",

    val ctaBackgroundColor: String = "#2563EB",
    val ctaTextColor: String = "#FFFFFF",

    val adAttributionTextColor: String = "#0F172A",
    val adAttributionBackgroundColor: String = "#FFFFFF",
    val adAttributionBorderColor: String = "#E5E7EB",

    val starRatingColor: String = "#F59E0B",

    val cornerRadiusDp: Int = 16,
    val ctaCornerRadiusDp: Int = 16,
    val adBadgeCornerRadiusDp: Int = 5,
    val mediaCornerRadiusDp: Int = 16
)