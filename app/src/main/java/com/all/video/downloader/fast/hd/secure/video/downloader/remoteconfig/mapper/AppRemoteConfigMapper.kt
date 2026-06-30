package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.mapper

import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.dto.AppApiRemoteConfigDto
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.dto.AppFeatureRemoteConfigDto
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.dto.AppGlobalRemoteConfigDto
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.dto.AppPremiumRemoteConfigDto
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.dto.VideoDownloaderRemoteConfigDto
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.defaults.DefaultVideoDownloaderRemoteConfigFactory
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppApiRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppFeatureRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppGlobalRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppPremiumRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.PremiumPlanKey
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.VideoDownloaderRemoteConfig
import kotlinx.serialization.json.Json

class AppRemoteConfigMapper(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = true
    }
) {

    fun mapFromJson(
        jsonString: String?,
        fallbackConfig: VideoDownloaderRemoteConfig =
            DefaultVideoDownloaderRemoteConfigFactory.create()
    ): AppRemoteConfigMapperResult {
        if (jsonString.isNullOrBlank()) {
            return AppRemoteConfigMapperResult.Failure(
                fallbackConfig = fallbackConfig,
                errorMessage = "Remote app config JSON is blank."
            )
        }

        return runCatching {
            val dto = json.decodeFromString<VideoDownloaderRemoteConfigDto>(jsonString)
            val warnings = mutableListOf<String>()

            val config = VideoDownloaderRemoteConfig(
                global = dto.global.toDomain(
                    fallback = fallbackConfig.global,
                    warnings = warnings
                ),
                features = dto.features.toDomain(
                    fallback = fallbackConfig.features
                ),
                premium = dto.premium.toDomain(
                    fallback = fallbackConfig.premium,
                    warnings = warnings
                ),
                api = dto.api.toDomain(
                    fallback = fallbackConfig.api
                )
            )

            AppRemoteConfigMapperResult.Success(
                config = config,
                warnings = warnings
            )
        }.getOrElse { throwable ->
            AppRemoteConfigMapperResult.Failure(
                fallbackConfig = fallbackConfig,
                errorMessage = throwable.message ?: "Failed to parse app Remote Config JSON."
            )
        }
    }

    private fun AppGlobalRemoteConfigDto?.toDomain(
        fallback: AppGlobalRemoteConfig,
        warnings: MutableList<String>
    ): AppGlobalRemoteConfig {
        if (this == null) return fallback

        return AppGlobalRemoteConfig(
            maintenanceEnabled = maintenanceEnabled ?: fallback.maintenanceEnabled,
            maintenanceTitle = maintenanceTitle.cleanOrNull() ?: fallback.maintenanceTitle,
            maintenanceMessage = maintenanceMessage.cleanOrNull() ?: fallback.maintenanceMessage,

            forceUpdateEnabled = forceUpdateEnabled ?: fallback.forceUpdateEnabled,
            minSupportedVersionCode = minSupportedVersionCode.validOneOrPositiveOrDefault(
                defaultValue = fallback.minSupportedVersionCode,
                fieldName = "app_global_config.min_supported_version_code",
                warnings = warnings
            ),
            forceUpdateTitle = forceUpdateTitle.cleanOrNull() ?: fallback.forceUpdateTitle,
            forceUpdateMessage = forceUpdateMessage.cleanOrNull() ?: fallback.forceUpdateMessage,
            playStoreUrl = playStoreUrl.cleanOrNull() ?: fallback.playStoreUrl,

            supportEmail = supportEmail.cleanOrNull() ?: fallback.supportEmail,
            privacyPolicyUrl = privacyPolicyUrl.cleanOrNull() ?: fallback.privacyPolicyUrl,
            termsUrl = termsUrl.cleanOrNull() ?: fallback.termsUrl
        )
    }

    private fun AppFeatureRemoteConfigDto?.toDomain(
        fallback: AppFeatureRemoteConfig
    ): AppFeatureRemoteConfig {
        if (this == null) return fallback

        return AppFeatureRemoteConfig(
            videoDownloaderEnabled =
                videoDownloaderEnabled ?: fallback.videoDownloaderEnabled,

            facebookDownloaderEnabled =
                facebookDownloaderEnabled ?: fallback.facebookDownloaderEnabled,

            instagramDownloaderEnabled =
                instagramDownloaderEnabled ?: fallback.instagramDownloaderEnabled,

            tiktokDownloaderEnabled =
                tiktokDownloaderEnabled ?: fallback.tiktokDownloaderEnabled,

            threadsDownloaderEnabled =
                threadsDownloaderEnabled ?: fallback.threadsDownloaderEnabled,

            likeeDownloaderEnabled =
                likeeDownloaderEnabled ?: fallback.likeeDownloaderEnabled,

            snackDownloaderEnabled =
                snackDownloaderEnabled ?: fallback.snackDownloaderEnabled,

            videoToMp3Enabled =
                videoToMp3Enabled ?: fallback.videoToMp3Enabled,

            videoSplitterEnabled =
                videoSplitterEnabled ?: fallback.videoSplitterEnabled,

            screenCastingEnabled =
                screenCastingEnabled ?: fallback.screenCastingEnabled,

            vaultEnabled =
                vaultEnabled ?: fallback.vaultEnabled,

            reelsEnabled =
                reelsEnabled ?: fallback.reelsEnabled,

            mediaPlayerEnabled =
                mediaPlayerEnabled ?: fallback.mediaPlayerEnabled
        )
    }

    private fun AppPremiumRemoteConfigDto?.toDomain(
        fallback: AppPremiumRemoteConfig,
        warnings: MutableList<String>
    ): AppPremiumRemoteConfig {
        if (this == null) return fallback

        return AppPremiumRemoteConfig(
            enabled = enabled ?: fallback.enabled,
            showPremiumScreen = showPremiumScreen ?: fallback.showPremiumScreen,
            showPremiumLabel = showPremiumLabel ?: fallback.showPremiumLabel,

            defaultSelectedPlan = defaultSelectedPlan.toPremiumPlanKey(
                fallback = fallback.defaultSelectedPlan,
                fieldName = "app_premium_config.default_selected_plan",
                warnings = warnings
            ),

            weeklyProductId = weeklyProductId.cleanOrNull() ?: fallback.weeklyProductId,
            monthlyProductId = monthlyProductId.cleanOrNull() ?: fallback.monthlyProductId,
            yearlyProductId = yearlyProductId.cleanOrNull() ?: fallback.yearlyProductId,
            lifetimeProductId = lifetimeProductId.cleanOrNull() ?: fallback.lifetimeProductId,

            showLifetimePlan = showLifetimePlan ?: fallback.showLifetimePlan,
            showWeeklyPlan = showWeeklyPlan ?: fallback.showWeeklyPlan,
            showMonthlyPlan = showMonthlyPlan ?: fallback.showMonthlyPlan,
            showYearlyPlan = showYearlyPlan ?: fallback.showYearlyPlan,

            headline = headline.cleanOrNull() ?: fallback.headline,
            subtitle = subtitle.cleanOrNull() ?: fallback.subtitle
        )
    }

    private fun String?.toPremiumPlanKey(
        fallback: PremiumPlanKey,
        fieldName: String,
        warnings: MutableList<String>
    ): PremiumPlanKey {
        return when (this?.trim()?.lowercase()) {
            "weekly", "week" -> PremiumPlanKey.WEEKLY
            "monthly", "month" -> PremiumPlanKey.MONTHLY
            "yearly", "annual", "year" -> PremiumPlanKey.YEARLY
            "lifetime", "life_time", "remove_ads", "remove-ads" -> PremiumPlanKey.LIFETIME
            null, "" -> fallback
            else -> {
                warnings.add("$fieldName has invalid value=$this. Using fallback=$fallback.")
                fallback
            }
        }
    }

    private fun String?.cleanOrNull(): String? {
        return this?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun Int?.validOneOrPositiveOrDefault(
        defaultValue: Int,
        fieldName: String,
        warnings: MutableList<String>
    ): Int {
        if (this == null) return defaultValue

        return if (this >= 1) {
            this
        } else {
            warnings.add("$fieldName must be >= 1. Using fallback=$defaultValue.")
            defaultValue
        }
    }


    private fun AppApiRemoteConfigDto?.toDomain(
        fallback: AppApiRemoteConfig
    ): AppApiRemoteConfig {
        if (this == null) return fallback

        return AppApiRemoteConfig(
            downloaderEnabled = downloaderEnabled ?: fallback.downloaderEnabled,
            downloaderBaseUrl = downloaderBaseUrl.cleanOrNull() ?: fallback.downloaderBaseUrl,
            downloaderSecretKey = downloaderSecretKey.cleanOrNull() ?: fallback.downloaderSecretKey,
            downloaderSecretHeader = downloaderSecretHeader.cleanOrNull()
                ?: fallback.downloaderSecretHeader
        )
    }

}