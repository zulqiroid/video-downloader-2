package com.core.ads.data.remote

import com.core.ads.domain.NativeAdSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdsRemoteConfigDto(
    @SerialName("ads_enabled")
    val adsEnabled: Boolean? = null,

    @SerialName("is_debug")
    val isDebug: Boolean? = null,

    @SerialName("can_request_ads")
    val canRequestAds: Boolean? = null,

    @SerialName("app_open")
    val appOpen: AppOpenRemoteConfigDto? = null,

    @SerialName("interstitial")
    val interstitial: InterstitialRemoteConfigDto? = null,

    @SerialName("rewarded")
    val rewarded: RewardedRemoteConfigDto? = null,

    @SerialName("banner")
    val banner: BannerRemoteConfigDto? = null,

    @SerialName("native")
    val native: NativeRemoteConfigDto? = null,

    @SerialName("screen_ads")
    val screenAds: Map<String, ScreenAdRemoteConfigDto>? = null
)

@Serializable
data class AppOpenRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    @SerialName("show_on_cold_start")
    val showOnColdStart: Boolean? = null,

    @SerialName("show_on_app_foreground")
    val showOnAppForeground: Boolean? = null,

    @SerialName("max_ad_age_ms")
    val maxAdAgeMillis: Long? = null,

    @SerialName("foreground_show_delay_ms")
    val foregroundShowDelayMillis: Long? = null,

    @SerialName("min_background_duration_before_show_ms")
    val minBackgroundDurationBeforeShowMillis: Long? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null
)

@Serializable
data class InterstitialRemoteConfigDto(
    val enabled: Boolean? = null,

    /**
     * Parent/global fallback interstitial ad unit id.
     */
    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    @SerialName("global_min_interval_between_shows_ms")
    val globalMinIntervalBetweenShowsMillis: Long? = null,

    @SerialName("max_ad_age_ms")
    val maxAdAgeMillis: Long? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null,

    /**
     * Key = placement name.
     *
     * Example:
     * "home_interstitial"
     */
    val placements: Map<String, InterstitialPlacementRemoteConfigDto>? = null
)

@Serializable
data class InterstitialPlacementRemoteConfigDto(
    val enabled: Boolean? = null,

    /**
     * Optional placement-specific override.
     * If missing, parent/global ID will be used.
     */
    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    @SerialName("max_ad_age_ms")
    val maxAdAgeMillis: Long? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null
)

@Serializable
data class RewardedRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    @SerialName("global_min_interval_between_shows_ms")
    val globalMinIntervalBetweenShowsMillis: Long? = null,

    @SerialName("max_ad_age_ms")
    val maxAdAgeMillis: Long? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null,

    val placements: Map<String, RewardedPlacementRemoteConfigDto>? = null
)

@Serializable
data class RewardedPlacementRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    @SerialName("max_ad_age_ms")
    val maxAdAgeMillis: Long? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null
)

@Serializable
data class BannerRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    @SerialName("is_adaptive")
    val isAdaptive: Boolean? = null,

    @SerialName("is_collapsible")
    val isCollapsible: Boolean? = null,

    /**
     * Supported values:
     * 1 = TOP
     * 2 = BOTTOM
     */
    @SerialName("collapse_gravity")
    val collapseGravity: Int? = null,

    val placements: Map<String, BannerPlacementRemoteConfigDto>? = null
)

@Serializable
data class BannerPlacementRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    @SerialName("is_adaptive")
    val isAdaptive: Boolean? = null,

    @SerialName("is_collapsible")
    val isCollapsible: Boolean? = null,

    @SerialName("collapse_gravity")
    val collapseGravity: Int? = null
)

@Serializable
data class NativeRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    val size: NativeAdSize? = null,

    @SerialName("max_ad_age_ms")
    val maxAdAgeMillis: Long? = null,

    val placements: Map<String, NativePlacementRemoteConfigDto>? = null
)

@Serializable
data class NativePlacementRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("ad_unit_id")
    val adUnitId: String? = null,

    val size: NativeAdSize? = null,

    @SerialName("max_ad_age_ms")
    val maxAdAgeMillis: Long? = null
)


@Serializable
data class ScreenAdRemoteConfigDto(
    val banner: ScreenBannerRemoteConfigDto? = null,
    val native: ScreenNativeRemoteConfigDto? = null,
    val interstitial: ScreenInterstitialRemoteConfigDto? = null
)

@Serializable
data class ScreenBannerRemoteConfigDto(
    val enabled: Boolean? = null,

    /**
     * Placement key from Remote Config.
     *
     * Example:
     * main_home_banner
     */
    val placement: String? = null,

    /**
     * T = top
     * B = bottom
     */
    val position: String? = null
)

@Serializable
data class ScreenNativeRemoteConfigDto(
    val enabled: Boolean? = null,

    /**
     * Placement key from Remote Config.
     */
    val placement: String? = null,

    /**
     * S = small
     * M = medium
     * L = large
     */
    val type: String? = null,

    /**
     * T = top
     * B = bottom
     */
    val position: String? = null
)

@Serializable
data class ScreenInterstitialRemoteConfigDto(
    val enabled: Boolean? = null,

    /**
     * Placement key from Remote Config.
     */
    val placement: String? = null,

    @SerialName("show_on_screen_enter")
    val showOnScreenEnter: Boolean? = null,

    @SerialName("show_on_tab_selected")
    val showOnTabSelected: Boolean? = null,

    @SerialName("show_on_back_pressed")
    val showOnBackPressed: Boolean? = null
)