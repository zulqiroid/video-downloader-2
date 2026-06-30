package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDownloaderRemoteConfigDto(
    @SerialName("app_global_config")
    val global: AppGlobalRemoteConfigDto? = null,

    @SerialName("app_feature_config")
    val features: AppFeatureRemoteConfigDto? = null,

    @SerialName("app_premium_config")
    val premium: AppPremiumRemoteConfigDto? = null,

    @SerialName("app_api_config")
    val api: AppApiRemoteConfigDto? = null

)

@Serializable
data class AppGlobalRemoteConfigDto(
    @SerialName("maintenance_enabled")
    val maintenanceEnabled: Boolean? = null,

    @SerialName("maintenance_title")
    val maintenanceTitle: String? = null,

    @SerialName("maintenance_message")
    val maintenanceMessage: String? = null,

    @SerialName("force_update_enabled")
    val forceUpdateEnabled: Boolean? = null,

    @SerialName("min_supported_version_code")
    val minSupportedVersionCode: Int? = null,

    @SerialName("force_update_title")
    val forceUpdateTitle: String? = null,

    @SerialName("force_update_message")
    val forceUpdateMessage: String? = null,

    @SerialName("play_store_url")
    val playStoreUrl: String? = null,

    @SerialName("support_email")
    val supportEmail: String? = null,

    @SerialName("privacy_policy_url")
    val privacyPolicyUrl: String? = null,

    @SerialName("terms_url")
    val termsUrl: String? = null
)

@Serializable
data class AppFeatureRemoteConfigDto(
    @SerialName("video_downloader_enabled")
    val videoDownloaderEnabled: Boolean? = null,

    @SerialName("facebook_downloader_enabled")
    val facebookDownloaderEnabled: Boolean? = null,

    @SerialName("instagram_downloader_enabled")
    val instagramDownloaderEnabled: Boolean? = null,

    @SerialName("tiktok_downloader_enabled")
    val tiktokDownloaderEnabled: Boolean? = null,

    @SerialName("threads_downloader_enabled")
    val threadsDownloaderEnabled: Boolean? = null,

    @SerialName("likee_downloader_enabled")
    val likeeDownloaderEnabled: Boolean? = null,

    @SerialName("snack_downloader_enabled")
    val snackDownloaderEnabled: Boolean? = null,

    @SerialName("video_to_mp3_enabled")
    val videoToMp3Enabled: Boolean? = null,

    @SerialName("video_splitter_enabled")
    val videoSplitterEnabled: Boolean? = null,

    @SerialName("screen_casting_enabled")
    val screenCastingEnabled: Boolean? = null,

    @SerialName("vault_enabled")
    val vaultEnabled: Boolean? = null,

    @SerialName("reels_enabled")
    val reelsEnabled: Boolean? = null,

    @SerialName("media_player_enabled")
    val mediaPlayerEnabled: Boolean? = null
)

@Serializable
data class AppPremiumRemoteConfigDto(
    val enabled: Boolean? = null,

    @SerialName("show_premium_screen")
    val showPremiumScreen: Boolean? = null,

    @SerialName("show_premium_label")
    val showPremiumLabel: Boolean? = null,

    @SerialName("default_selected_plan")
    val defaultSelectedPlan: String? = null,

    @SerialName("weekly_product_id")
    val weeklyProductId: String? = null,

    @SerialName("monthly_product_id")
    val monthlyProductId: String? = null,

    @SerialName("yearly_product_id")
    val yearlyProductId: String? = null,

    @SerialName("lifetime_product_id")
    val lifetimeProductId: String? = null,

    @SerialName("show_lifetime_plan")
    val showLifetimePlan: Boolean? = null,

    @SerialName("show_weekly_plan")
    val showWeeklyPlan: Boolean? = null,

    @SerialName("show_monthly_plan")
    val showMonthlyPlan: Boolean? = null,

    @SerialName("show_yearly_plan")
    val showYearlyPlan: Boolean? = null,

    val headline: String? = null,
    val subtitle: String? = null
)

@Serializable
data class AppApiRemoteConfigDto(
    @SerialName("downloader_enabled")
    val downloaderEnabled: Boolean? = null,

    @SerialName("downloader_base_url")
    val downloaderBaseUrl: String? = null,

    @SerialName("downloader_secret_key")
    val downloaderSecretKey: String? = null,

    @SerialName("downloader_secret_header")
    val downloaderSecretHeader: String? = null
)