package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.defaults

import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppApiRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppFeatureRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppGlobalRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppPremiumRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.PremiumPlanKey
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.VideoDownloaderRemoteConfig

object DefaultVideoDownloaderRemoteConfigFactory {

    fun create(): VideoDownloaderRemoteConfig {
        return VideoDownloaderRemoteConfig(
            global = AppGlobalRemoteConfig(
                maintenanceEnabled = false,
                maintenanceTitle = "Maintenance",
                maintenanceMessage = "We are improving the app. Please try again later.",

                forceUpdateEnabled = false,
                minSupportedVersionCode = 1,
                forceUpdateTitle = "Update Required",
                forceUpdateMessage = "Please update the app to continue.",
                playStoreUrl = null,

                supportEmail = "www.deepvision.studio@gmail.com",
                privacyPolicyUrl = "https://deepvisionstudio.blogspot.com/2026/05/privacy-policy-of-deepvision-studio.html",
                termsUrl = null
            ),

            features = AppFeatureRemoteConfig(
                videoDownloaderEnabled = true,
                facebookDownloaderEnabled = true,
                instagramDownloaderEnabled = true,
                tiktokDownloaderEnabled = true,
                threadsDownloaderEnabled = true,
                likeeDownloaderEnabled = true,
                snackDownloaderEnabled = true,

                videoToMp3Enabled = true,
                videoSplitterEnabled = true,
                screenCastingEnabled = true,
                vaultEnabled = true,
                reelsEnabled = true,
                mediaPlayerEnabled = true
            ),

            premium = AppPremiumRemoteConfig(
                enabled = false,
                showPremiumScreen = false,
                showPremiumLabel = false,
                defaultSelectedPlan = PremiumPlanKey.YEARLY,

                weeklyProductId = null,
                monthlyProductId = null,
                yearlyProductId = null,
                lifetimeProductId = null,

                showLifetimePlan = true,
                showWeeklyPlan = true,
                showMonthlyPlan = true,
                showYearlyPlan = true,

                headline = "Go Premium",
                subtitle = "Remove ads and enjoy a cleaner experience."
            ),
            api = AppApiRemoteConfig(
                downloaderEnabled = false,
                downloaderBaseUrl = null,
                downloaderSecretKey = null,
                downloaderSecretHeader = "X-Secret-Key"
            )
        )
    }
}