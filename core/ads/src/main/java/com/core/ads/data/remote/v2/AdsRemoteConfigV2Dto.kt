package com.core.ads.data.remote.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdsRemoteConfigV2Dto(
    @SerialName("ads_global_config")
    val global: AdsGlobalRemoteConfigDto? = null,

    @SerialName("ads_loading_dialog_config")
    val loadingDialog: AdsLoadingDialogRemoteConfigDto? = null,

    @SerialName("ads_app_open_config")
    val appOpen: AdsAppOpenRemoteConfigDto? = null,

    @SerialName("ads_banner_config")
    val banner: AdsBannerRemoteConfigDto? = null,

    @SerialName("ads_interstitial_config")
    val interstitial: AdsInterstitialRemoteConfigDto? = null,

    @SerialName("ads_rewarded_config")
    val rewarded: AdsRewardedRemoteConfigDto? = null,

    @SerialName("ads_native_config")
    val native: AdsNativeRemoteConfigDto? = null
)

@Serializable
data class AdsGlobalRemoteConfigDto(
    @SerialName("ads_enabled")
    val adsEnabled: Boolean? = null,

    @SerialName("can_request_ads")
    val canRequestAds: Boolean? = null,

    @SerialName("is_debug")
    val isDebug: Boolean? = null
)

@Serializable
data class AdsAppOpenRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("show_on_resume")
    val showOnResume: Boolean? = null,

    @SerialName("show_on_splash")
    val showOnSplash: Boolean? = null,

    @SerialName("app_open_ad_id")
    val appOpenAdId: String? = null,

    @SerialName("splash_open_ad_unit_id")
    val splashOpenAdUnitId: String? = null,

    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMillis: Long? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null,

    @SerialName("min_background_duration_before_show_ms")
    val minBackgroundDurationBeforeShowMillis: Long? = null,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int? = null,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMillis: Long? = null,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMillis: Long? = null,

    @SerialName("show_automatically_on_cold_start")
    val showAutomaticallyOnColdStart: Boolean? = null
)

@Serializable
data class AdsBannerRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("banner_ad_id")
    val bannerAdId: String? = null,

    @SerialName("show_placeholder")
    val showPlaceholder: Boolean? = null,

    @SerialName("placeholder_height_dp")
    val placeholderHeightDp: Int? = null,

    @SerialName("keep_placeholder_on_failure")
    val keepPlaceholderOnFailure: Boolean? = null,

    @SerialName("max_cached_runtimes")
    val maxCachedRuntimes: Int? = null,

    @SerialName("max_detached_runtime_age_ms")
    val maxDetachedRuntimeAgeMillis: Long? = null,

    @SerialName("retry_on_failure")
    val retryOnFailure: Boolean? = null,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int? = null,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMillis: Long? = null,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMillis: Long? = null,

    @SerialName("debug_label_enabled")
    val debugLabelEnabled: Boolean? = null,

    val screens: Map<String, BannerScreenRemoteConfigDto>? = null,

    @SerialName("collapsible_enabled")
    val collapsibleEnabled: Boolean? = null,

    @SerialName("collapsible_position")
    val collapsiblePosition: String? = null
)

@Serializable
data class BannerScreenRemoteConfigDto(
    val top: Boolean? = null,
    val bottom: Boolean? = null,

    /**
     * Optional screen-level banner unit.
     * Used as fallback for both top and bottom slots before global banner_ad_id.
     *
     * Priority:
     * top_unit_id / bottom_unit_id -> unit_id -> banner_ad_id
     */
    @SerialName("unit_id")
    val unitId: String? = null,

    /**
     * Optional top-slot specific banner unit.
     */
    @SerialName("top_unit_id")
    val topUnitId: String? = null,

    /**
     * Optional bottom-slot specific banner unit.
     */
    @SerialName("bottom_unit_id")
    val bottomUnitId: String? = null,

    /**
     * Optional per-screen adaptive override.
     */
    @SerialName("is_adaptive")
    val isAdaptive: Boolean? = null,

    /**
     * Optional per-screen collapsible override.
     */
    @SerialName("collapsible_enabled")
    val collapsibleEnabled: Boolean? = null,

    /**
     * Optional per-screen collapsible position override.
     * Supported values: top, bottom
     */
    @SerialName("collapsible_position")
    val collapsiblePosition: String? = null
)

@Serializable
data class AdsInterstitialRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("interstitial_ad_id")
    val interstitialAdId: String? = null,

    @SerialName("splash_unit_id")
    val splashUnitId: String? = null,

    @SerialName("language_unit_id")
    val languageUnitId: String? = null,

    @SerialName("intro_unit_id")
    val introUnitId: String? = null,

    @SerialName("premium_unit_id")
    val premiumUnitId: String? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null,

    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMillis: Long? = null,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int? = null,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMillis: Long? = null,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMillis: Long? = null,

    @SerialName("show_on_tab_switch")
    val showOnTabSwitch: Boolean? = null,

    @SerialName("tab_switch_trigger_count")
    val tabSwitchTriggerCount: Int? = null,

    @SerialName("show_on_premium")
    val showOnPremium: Boolean? = null,

    @SerialName("premium_trigger_count")
    val premiumTriggerCount: Int? = null,

    @SerialName("show_on_onboarding")
    val showOnOnboarding: Boolean? = null,

    @SerialName("onboarding_trigger_count")
    val onboardingTriggerCount: Int? = null,

    @SerialName("show_on_language")
    val showOnLanguage: Boolean? = null,

    @SerialName("language_trigger_count")
    val languageTriggerCount: Int? = null,

    @SerialName("show_on_image_result")
    val showOnImageResult: Boolean? = null,

    @SerialName("image_result_trigger_count")
    val imageResultTriggerCount: Int? = null,

    @SerialName("show_on_back_navigation")
    val showOnBackNavigation: Boolean? = null,

    @SerialName("back_navigation_trigger_count")
    val backNavigationTriggerCount: Int? = null,

    val screens: Map<String, InterstitialScreenRemoteConfigDto>? = null,

    val features: Map<String, InterstitialFeatureRemoteConfigDto>? = null
)

@Serializable
data class InterstitialScreenRemoteConfigDto(
    val enabled: Boolean? = null,

    /**
     * Optional screen-specific interstitial unit.
     * Falls back to interstitial_ad_id when blank/missing.
     */
    @SerialName("unit_id")
    val unitId: String? = null,

    /**
     * How many times this screen trigger should happen before an ad is eligible.
     */
    @SerialName("trigger_count")
    val triggerCount: Int? = null,

    /**
     * Optional screen-specific show interval override.
     */
    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null,

    /**
     * Optional screen-specific cache-age override.
     */
    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMillis: Long? = null
)

@Serializable
data class AdsRewardedRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("rewarded_ad_id")
    val rewardedAdId: String? = null,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMillis: Long? = null,

    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMillis: Long? = null,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int? = null,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMillis: Long? = null,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMillis: Long? = null,

    val placements: Map<String, RewardedPlacementRemoteConfigDto>? = null
)

@Serializable
data class RewardedPlacementRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("unit_id")
    val unitId: String? = null,

    @SerialName("reward_type")
    val rewardType: String? = null,

    @SerialName("reward_amount")
    val rewardAmount: Int? = null
)

@Serializable
data class AdsNativeRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("native_ad_id")
    val nativeAdId: String? = null,

    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMillis: Long? = null,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int? = null,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMillis: Long? = null,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMillis: Long? = null,

    @SerialName("container_background_color")
    val containerBackgroundColor: String? = null,

    @SerialName("container_border_color")
    val containerBorderColor: String? = null,

    @SerialName("container_border_width_dp")
    val containerBorderWidthDp: Int? = null,

    @SerialName("media_background_color")
    val mediaBackgroundColor: String? = null,

    @SerialName("headline_text_color")
    val headlineTextColor: String? = null,

    @SerialName("body_text_color")
    val bodyTextColor: String? = null,

    @SerialName("cta_background_color")
    val ctaBackgroundColor: String? = null,

    @SerialName("cta_text_color")
    val ctaTextColor: String? = null,

    @SerialName("ad_attribution_text_color")
    val adAttributionTextColor: String? = null,

    @SerialName("ad_attribution_background_color")
    val adAttributionBackgroundColor: String? = null,

    @SerialName("ad_attribution_border_color")
    val adAttributionBorderColor: String? = null,

    @SerialName("star_rating_color")
    val starRatingColor: String? = null,

    @SerialName("corner_radius_dp")
    val cornerRadiusDp: Int? = null,

    @SerialName("cta_corner_radius_dp")
    val ctaCornerRadiusDp: Int? = null,

    @SerialName("ad_badge_corner_radius_dp")
    val adBadgeCornerRadiusDp: Int? = null,

    @SerialName("media_corner_radius_dp")
    val mediaCornerRadiusDp: Int? = null,

    val placements: Map<String, NativePlacementRemoteConfigDto>? = null
)

@Serializable
data class NativePlacementRemoteConfigDto(
    @SerialName("unit_id")
    val unitId: String? = null,

    val enabled: Boolean? = null,

    /**
     * Small / Medium / Large
     * Also supports S / M / L later in mapper.
     */
    val style: String? = null,

    /**
     * Top / Bottom / FullPage
     * Also supports T / B later in mapper.
     */
    val position: String? = null,

    @SerialName("show_placeholder")
    val showPlaceholder: Boolean? = null
)

@Serializable
data class InterstitialFeatureRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("trigger_count")
    val triggerCount: Int? = null,

    @SerialName("unit_id")
    val unitId: String? = null
)

@Serializable
data class AdsLoadingDialogRemoteConfigDto(
    @SerialName("enabled")
    val enabled: Boolean? = null,

    @SerialName("show_for_app_open")
    val showForAppOpen: Boolean? = null,

    @SerialName("show_for_interstitial")
    val showForInterstitial: Boolean? = null,

    @SerialName("show_for_rewarded")
    val showForRewarded: Boolean? = null,

    @SerialName("duration_ms")
    val durationMs: Long? = null,

    @SerialName("app_open_duration_ms")
    val appOpenDurationMs: Long? = null,

    @SerialName("interstitial_duration_ms")
    val interstitialDurationMs: Long? = null,

    @SerialName("rewarded_duration_ms")
    val rewardedDurationMs: Long? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("app_open_title")
    val appOpenTitle: String? = null,

    @SerialName("app_open_message")
    val appOpenMessage: String? = null,

    @SerialName("interstitial_title")
    val interstitialTitle: String? = null,

    @SerialName("interstitial_message")
    val interstitialMessage: String? = null,

    @SerialName("rewarded_title")
    val rewardedTitle: String? = null,

    @SerialName("rewarded_message")
    val rewardedMessage: String? = null
)