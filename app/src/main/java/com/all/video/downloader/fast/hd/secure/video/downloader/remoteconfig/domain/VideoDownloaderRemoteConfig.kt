package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain

data class VideoDownloaderRemoteConfig(
    val global: AppGlobalRemoteConfig = AppGlobalRemoteConfig(),
    val features: AppFeatureRemoteConfig = AppFeatureRemoteConfig(),
    val premium: AppPremiumRemoteConfig = AppPremiumRemoteConfig(),
    val api: AppApiRemoteConfig = AppApiRemoteConfig()
)

data class AppGlobalRemoteConfig(
    val maintenanceEnabled: Boolean = false,
    val maintenanceTitle: String = "Maintenance",
    val maintenanceMessage: String = "We are improving the app. Please try again later.",

    val forceUpdateEnabled: Boolean = false,
    val minSupportedVersionCode: Int = 1,
    val forceUpdateTitle: String = "Update Required",
    val forceUpdateMessage: String = "Please update the app to continue.",
    val playStoreUrl: String? = null,

    val supportEmail: String? = null,
    val privacyPolicyUrl: String? = null,
    val termsUrl: String? = null
)

data class AppFeatureRemoteConfig(
    val videoDownloaderEnabled: Boolean = true,
    val facebookDownloaderEnabled: Boolean = true,
    val instagramDownloaderEnabled: Boolean = true,
    val tiktokDownloaderEnabled: Boolean = true,
    val threadsDownloaderEnabled: Boolean = true,
    val likeeDownloaderEnabled: Boolean = true,
    val snackDownloaderEnabled: Boolean = true,

    val videoToMp3Enabled: Boolean = true,
    val videoSplitterEnabled: Boolean = true,
    val screenCastingEnabled: Boolean = true,
    val vaultEnabled: Boolean = true,
    val reelsEnabled: Boolean = true,
    val mediaPlayerEnabled: Boolean = true
)

data class AppPremiumRemoteConfig(
    val enabled: Boolean = true,
    val showPremiumScreen: Boolean = true,
    val showPremiumLabel: Boolean = true,
    val defaultSelectedPlan: PremiumPlanKey = PremiumPlanKey.YEARLY,

    val weeklyProductId: String? = null,
    val monthlyProductId: String? = null,
    val yearlyProductId: String? = null,
    val lifetimeProductId: String? = null,

    val showLifetimePlan: Boolean = true,
    val showWeeklyPlan: Boolean = true,
    val showMonthlyPlan: Boolean = true,
    val showYearlyPlan: Boolean = true,

    val headline: String = "Go Premium",
    val subtitle: String = "Remove ads and enjoy a cleaner experience."
)

data class AppApiRemoteConfig(
    val downloaderEnabled: Boolean = true,
    val downloaderBaseUrl: String? = null,
    val downloaderSecretKey: String? = null,
    val downloaderSecretHeader: String = "X-Secret-Key"
)

enum class PremiumPlanKey {
    WEEKLY,
    MONTHLY,
    YEARLY,
    LIFETIME
}