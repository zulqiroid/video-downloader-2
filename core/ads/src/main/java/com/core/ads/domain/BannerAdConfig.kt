package com.core.ads.domain

import com.core.ads.domain.placement.AdPlacement

data class BannerAdConfig(
    val enabled: Boolean = false,

    val adUnitId: String? = null,

    val isAdaptive: Boolean = true,

    val isCollapsible: Boolean = false,

    val collapseGravity: Int = CollapseGravity.BOTTOM,

    /**
     * Show placeholder while banner is loading.
     */
    val showPlaceholder: Boolean = true,

    /**
     * Placeholder height.
     * Recommended: 50–70dp for banner.
     */
    val placeholderHeightDp: Int = 60,

    /**
     * If false:
     * placeholder disappears when ad fails/gets blocked.
     *
     * If true:
     * placeholder remains even after failure.
     */
    val keepPlaceholderOnFailure: Boolean = false,

    /**
     * Maximum banner AdView runtimes allowed in memory.
     *
     * Only detached old runtimes are trimmed.
     * Currently attached visible banners are never destroyed by this limit.
     */
    val maxCachedRuntimes: Int = 5,

    /**
     * Detached banner runtime max age.
     *
     * If a banner is detached and not reused within this window,
     * it becomes eligible for cleanup.
     */
    val maxDetachedRuntimeAgeMillis: Long = 5 * 60 * 1000L,

    /**
     * Retry banner load after failure.
     *
     * Useful for:
     * - temporary network issue
     * - SDK warmup delay
     * - first request no-fill
     */
    val retryOnFailure: Boolean = true,

    /**
     * Maximum retry attempts after a failed banner load.
     *
     * 0 = no retry
     */
    val maxLoadRetryCount: Int = 2,

    val initialRetryDelayMillis: Long = 3_000L,

    val maxRetryDelayMillis: Long = 30_000L,

    /**
     * Debug-only banner label.
     *
     * This should only be shown when:
     * - ads_global_config.is_debug = true
     * - debug_label_enabled = true
     *
     * Production Remote Config mein isko false rakhna.
     */
    val debugLabelEnabled: Boolean = false,

    val screens: Map<String, BannerScreenConfig> = emptyMap(),

    val placements: Map<String, BannerPlacementConfig> = emptyMap()
) {
    fun resolve(placement: AdPlacement): ResolvedBannerAdConfig? {
        if (!enabled) return null

        val placementConfig = placements[placement.value]

        if (placementConfig?.enabled == false) return null

        val resolvedAdUnitId = placementConfig
            ?.adUnitId
            ?.takeIf { it.isNotBlank() }
            ?: adUnitId?.takeIf { it.isNotBlank() }
            ?: return null

        return ResolvedBannerAdConfig(
            placement = placement,
            adUnitId = resolvedAdUnitId,
            isAdaptive = placementConfig?.isAdaptive ?: isAdaptive,
            isCollapsible = placementConfig?.isCollapsible ?: isCollapsible,
            collapseGravity = placementConfig?.collapseGravity ?: collapseGravity,
            isUsingPlacementAdUnitId = !placementConfig?.adUnitId.isNullOrBlank()
        )
    }

    fun isPlacementUsable(placement: AdPlacement): Boolean {
        return resolve(placement) != null
    }

    fun getScreenConfig(screenKey: String): BannerScreenConfig {
        return screens[screenKey] ?: BannerScreenConfig()
    }

    object CollapseGravity {
        const val TOP = 1
        const val BOTTOM = 2
    }
}

data class BannerScreenConfig(
    val top: Boolean = false,
    val bottom: Boolean = false,

    /**
     * Screen-level banner unit.
     * Used by both top and bottom when slot-specific ID is missing.
     */
    val adUnitId: String? = null,

    /**
     * Top banner slot specific unit.
     */
    val topAdUnitId: String? = null,

    /**
     * Bottom banner slot specific unit.
     */
    val bottomAdUnitId: String? = null,

    /**
     * Optional screen-level adaptive override.
     */
    val isAdaptive: Boolean? = null,

    /**
     * Optional screen-level collapsible override.
     */
    val isCollapsible: Boolean? = null,

    /**
     * Optional screen-level collapse gravity.
     */
    val collapseGravity: Int? = null
)

data class BannerPlacementConfig(
    val enabled: Boolean? = null,
    val adUnitId: String? = null,

    val isAdaptive: Boolean? = null,
    val isCollapsible: Boolean? = null,
    val collapseGravity: Int? = null
)

data class ResolvedBannerAdConfig(
    val placement: AdPlacement,
    val adUnitId: String,
    val isAdaptive: Boolean,
    val isCollapsible: Boolean,
    val collapseGravity: Int,
    val isUsingPlacementAdUnitId: Boolean
)