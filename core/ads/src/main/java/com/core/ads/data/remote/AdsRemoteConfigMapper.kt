package com.core.ads.data.remote

import com.core.ads.data.remote.v2.AdsAppOpenRemoteConfigDto
import com.core.ads.data.remote.v2.AdsBannerRemoteConfigDto
import com.core.ads.data.remote.v2.AdsGlobalRemoteConfigDto
import com.core.ads.data.remote.v2.AdsInterstitialRemoteConfigDto
import com.core.ads.data.remote.v2.AdsLoadingDialogRemoteConfigDto
import com.core.ads.data.remote.v2.AdsNativeRemoteConfigDto
import com.core.ads.data.remote.v2.AdsRemoteConfigV2Dto
import com.core.ads.data.remote.v2.AdsRewardedRemoteConfigDto
import com.core.ads.data.remote.v2.BannerScreenRemoteConfigDto
import com.core.ads.data.remote.v2.InterstitialFeatureRemoteConfigDto
import com.core.ads.data.remote.v2.InterstitialScreenRemoteConfigDto
import com.core.ads.data.remote.v2.NativePlacementRemoteConfigDto
import com.core.ads.data.remote.v2.RewardedPlacementRemoteConfigDto
import com.core.ads.domain.AdContentPosition
import com.core.ads.domain.AdLoadingDialogConfig
import com.core.ads.domain.AdsCoreConfig
import com.core.ads.domain.BannerAdConfig
import com.core.ads.domain.BannerPlacementConfig
import com.core.ads.domain.BannerScreenConfig
import com.core.ads.domain.CommonAdRetryConfig
import com.core.ads.domain.InterstitialAdConfig
import com.core.ads.domain.InterstitialFeatureConfig
import com.core.ads.domain.InterstitialPlacementConfig
import com.core.ads.domain.InterstitialScreenConfig
import com.core.ads.domain.InterstitialTriggerConfig
import com.core.ads.domain.NativeAdConfig
import com.core.ads.domain.NativeAdSize
import com.core.ads.domain.NativeAdStyleConfig
import com.core.ads.domain.NativePlacementConfig
import com.core.ads.domain.RewardedAdConfig
import com.core.ads.domain.RewardedPlacementConfig
import com.core.ads.domain.appopen.AppOpenAdConfig
import kotlinx.serialization.json.Json

class AdsRemoteConfigMapper(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = true
    }
) {

    fun mapFromJson(
        jsonString: String?,
        fallbackConfig: AdsCoreConfig = AdsCoreConfig(adsEnabled = false)
    ): AdsRemoteConfigMapperResult {
        if (jsonString.isNullOrBlank()) {
            return AdsRemoteConfigMapperResult.Failure(
                fallbackConfig = fallbackConfig,
                errorMessage = "Remote ads config JSON is blank."
            )
        }

        return runCatching {
            val dto = json.decodeFromString<AdsRemoteConfigV2Dto>(jsonString)

            val warnings = mutableListOf<String>()

            val config = dto.toDomain(
                fallbackConfig = fallbackConfig,
                warnings = warnings
            )

            AdsRemoteConfigMapperResult.Success(
                config = config,
                warnings = warnings
            )
        }.getOrElse { throwable ->
            AdsRemoteConfigMapperResult.Failure(
                fallbackConfig = fallbackConfig,
                errorMessage = throwable.message ?: "Failed to parse ads Remote Config JSON."
            )
        }
    }

    private fun AdsRemoteConfigV2Dto.toDomain(
        fallbackConfig: AdsCoreConfig,
        warnings: MutableList<String>
    ): AdsCoreConfig {
        val globalConfig = global.toDomain(
            fallback = fallbackConfig
        )

        return AdsCoreConfig(
            adsEnabled = globalConfig.adsEnabled,
            canRequestAds = globalConfig.canRequestAds,
            isDebug = globalConfig.isDebug,

            adLoadingDialogConfig = loadingDialog.toDomain(
                fallback = fallbackConfig.adLoadingDialogConfig,
                warnings = warnings
            ),

            appOpenAdConfig = appOpen.toDomain(
                fallback = fallbackConfig.appOpenAdConfig,
                warnings = warnings
            ),

            bannerAdConfig = banner.toDomain(
                fallback = fallbackConfig.bannerAdConfig,
                warnings = warnings
            ),

            interstitialAdConfig = interstitial.toDomain(
                fallback = fallbackConfig.interstitialAdConfig,
                warnings = warnings
            ),

            rewardedAdConfig = rewarded.toDomain(
                fallback = fallbackConfig.rewardedAdConfig,
                warnings = warnings
            ),

            nativeAdConfig = native.toDomain(
                fallback = fallbackConfig.nativeAdConfig,
                warnings = warnings
            )
        )
    }

    private fun AdsGlobalRemoteConfigDto?.toDomain(
        fallback: AdsCoreConfig
    ): ResolvedGlobalConfig {
        if (this == null) {
            return ResolvedGlobalConfig(
                adsEnabled = fallback.adsEnabled,
                canRequestAds = fallback.canRequestAds,
                isDebug = fallback.isDebug
            )
        }

        return ResolvedGlobalConfig(
            adsEnabled = adsEnabled ?: fallback.adsEnabled,
            canRequestAds = canRequestAds ?: fallback.canRequestAds,
            isDebug = isDebug ?: fallback.isDebug
        )
    }

    private fun AdsAppOpenRemoteConfigDto?.toDomain(
        fallback: AppOpenAdConfig,
        warnings: MutableList<String>
    ): AppOpenAdConfig {
        if (this == null) return fallback

        return AppOpenAdConfig(
            enabled = enabled ?: fallback.enabled,

            adUnitId = appOpenAdId.cleanOrNull()
                ?: fallback.adUnitId,

            splashAdUnitId = splashOpenAdUnitId.cleanOrNull()
                ?: fallback.splashAdUnitId,

            showOnSplash = showOnSplash
                ?: fallback.showOnSplash,

            showOnAppForeground = showOnResume
                ?: fallback.showOnAppForeground,

            /**
             * Keep both fields aligned.
             */
            showOnColdStart = showAutomaticallyOnColdStart
                ?: fallback.showOnColdStart,

            showAutomaticallyOnColdStart = showAutomaticallyOnColdStart
                ?: fallback.showAutomaticallyOnColdStart,

            foregroundShowDelayMillis = fallback.foregroundShowDelayMillis,

            minBackgroundDurationBeforeShowMillis =
                minBackgroundDurationBeforeShowMillis.validZeroOrPositiveOrDefault(
                    defaultValue = fallback.minBackgroundDurationBeforeShowMillis,
                    fieldName = "ads_app_open_config.min_background_duration_before_show_ms",
                    warnings = warnings
                ),

            maxAdAgeMillis = maxAdCacheDurationMillis.validPositiveOrDefault(
                defaultValue = fallback.maxAdAgeMillis,
                fieldName = "ads_app_open_config.max_ad_cache_duration_ms",
                warnings = warnings
            ),

            minIntervalBetweenShowsMillis =
                minIntervalBetweenShowsMillis.validZeroOrPositiveOrDefault(
                    defaultValue = fallback.minIntervalBetweenShowsMillis,
                    fieldName = "ads_app_open_config.min_interval_between_shows_ms",
                    warnings = warnings
                ),

            retryConfig = CommonAdRetryConfig(
                maxLoadRetryCount = maxLoadRetryCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.retryConfig.maxLoadRetryCount,
                    fieldName = "ads_app_open_config.max_load_retry_count",
                    warnings = warnings
                ),
                initialRetryDelayMillis =
                    initialRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.initialRetryDelayMillis,
                        fieldName = "ads_app_open_config.initial_retry_delay_ms",
                        warnings = warnings
                    ),
                maxRetryDelayMillis =
                    maxRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.maxRetryDelayMillis,
                        fieldName = "ads_app_open_config.max_retry_delay_ms",
                        warnings = warnings
                    )
            )
        )
    }

    private fun AdsBannerRemoteConfigDto?.toDomain(
        fallback: BannerAdConfig,
        warnings: MutableList<String>
    ): BannerAdConfig {
        if (this == null) return fallback

        val collapseGravity = collapsiblePosition.toCollapseGravity(
            fallback = fallback.collapseGravity,
            fieldName = "ads_banner_config.collapsible_position",
            warnings = warnings
        )

        val resolvedIsCollapsible = collapsibleEnabled ?: fallback.isCollapsible

        val screenConfigs = screens.toDomainBannerScreens(
            fallback = fallback.screens,
            parentIsAdaptive = fallback.isAdaptive,
            parentIsCollapsible = resolvedIsCollapsible,
            parentCollapseGravity = collapseGravity,
            warnings = warnings
        )

        val placementOverrides = if (screens == null) {
            fallback.placements
        } else {
            screenConfigs.toBannerPlacementOverrides()
        }

        return BannerAdConfig(
            enabled = enabled ?: fallback.enabled,
            adUnitId = bannerAdId.cleanOrNull() ?: fallback.adUnitId,
            isAdaptive = fallback.isAdaptive,
            isCollapsible = resolvedIsCollapsible,
            collapseGravity = collapseGravity,

            showPlaceholder = showPlaceholder ?: fallback.showPlaceholder,

            placeholderHeightDp = placeholderHeightDp.validOneOrPositiveOrDefault(
                defaultValue = fallback.placeholderHeightDp,
                fieldName = "ads_banner_config.placeholder_height_dp",
                warnings = warnings
            ),

            keepPlaceholderOnFailure =
                keepPlaceholderOnFailure ?: fallback.keepPlaceholderOnFailure,

            maxCachedRuntimes = maxCachedRuntimes.validOneOrPositiveOrDefault(
                defaultValue = fallback.maxCachedRuntimes,
                fieldName = "ads_banner_config.max_cached_runtimes",
                warnings = warnings
            ),

            maxDetachedRuntimeAgeMillis = maxDetachedRuntimeAgeMillis.validPositiveOrDefault(
                defaultValue = fallback.maxDetachedRuntimeAgeMillis,
                fieldName = "ads_banner_config.max_detached_runtime_age_ms",
                warnings = warnings
            ),

            retryOnFailure =
                retryOnFailure ?: fallback.retryOnFailure,

            maxLoadRetryCount = maxLoadRetryCount.validZeroOrPositiveOrDefault(
                defaultValue = fallback.maxLoadRetryCount,
                fieldName = "ads_banner_config.max_load_retry_count",
                warnings = warnings
            ),

            initialRetryDelayMillis = initialRetryDelayMillis.validPositiveOrDefault(
                defaultValue = fallback.initialRetryDelayMillis,
                fieldName = "ads_banner_config.initial_retry_delay_ms",
                warnings = warnings
            ),

            maxRetryDelayMillis = maxRetryDelayMillis.validPositiveOrDefault(
                defaultValue = fallback.maxRetryDelayMillis,
                fieldName = "ads_banner_config.max_retry_delay_ms",
                warnings = warnings
            ),

            debugLabelEnabled =
                debugLabelEnabled ?: fallback.debugLabelEnabled,

            screens = screenConfigs,

            /**
             * Enterprise placement strategy:
             *
             * For every screen key, we generate:
             * - screenKey
             * - screenKey_top
             * - screenKey_bottom
             *
             * Example:
             * home -> home, home_top, home_bottom
             *
             * This allows:
             * - same global banner ID
             * - same screen-level ID
             * - separate top/bottom IDs
             */
            placements = placementOverrides
        )
    }

    private fun Map<String, BannerScreenRemoteConfigDto>?.toDomainBannerScreens(
        fallback: Map<String, BannerScreenConfig>,
        parentIsAdaptive: Boolean,
        parentIsCollapsible: Boolean,
        parentCollapseGravity: Int,
        warnings: MutableList<String>
    ): Map<String, BannerScreenConfig> {
        if (this == null) return fallback

        return mapValues { (screenKey, dto) ->
            val collapseGravity = dto.collapsiblePosition.toCollapseGravity(
                fallback = parentCollapseGravity,
                fieldName = "ads_banner_config.screens.$screenKey.collapsible_position",
                warnings = warnings
            )

            BannerScreenConfig(
                top = dto.top ?: false,
                bottom = dto.bottom ?: false,
                adUnitId = dto.unitId.cleanOrNull(),
                topAdUnitId = dto.topUnitId.cleanOrNull(),
                bottomAdUnitId = dto.bottomUnitId.cleanOrNull(),
                isAdaptive = dto.isAdaptive ?: parentIsAdaptive,
                isCollapsible = dto.collapsibleEnabled ?: parentIsCollapsible,
                collapseGravity = collapseGravity
            )
        }
    }

    private fun Map<String, BannerScreenConfig>.toBannerPlacementOverrides(): Map<String, BannerPlacementConfig> {
        val screenConfigs = this

        return buildMap {
            screenConfigs.forEach { (screenKey, screenConfig) ->
                val screenAdUnitId = screenConfig.adUnitId.cleanOrNull()
                val topAdUnitId = screenConfig.topAdUnitId.cleanOrNull() ?: screenAdUnitId
                val bottomAdUnitId = screenConfig.bottomAdUnitId.cleanOrNull() ?: screenAdUnitId

                put(
                    screenKey,
                    BannerPlacementConfig(
                        enabled = screenConfig.top || screenConfig.bottom,
                        adUnitId = screenAdUnitId,
                        isAdaptive = screenConfig.isAdaptive,
                        isCollapsible = screenConfig.isCollapsible,
                        collapseGravity = screenConfig.collapseGravity
                    )
                )

                put(
                    "${screenKey}_top",
                    BannerPlacementConfig(
                        enabled = screenConfig.top,
                        adUnitId = topAdUnitId,
                        isAdaptive = screenConfig.isAdaptive,
                        isCollapsible = screenConfig.isCollapsible,
                        collapseGravity = screenConfig.collapseGravity
                    )
                )

                put(
                    "${screenKey}_bottom",
                    BannerPlacementConfig(
                        enabled = screenConfig.bottom,
                        adUnitId = bottomAdUnitId,
                        isAdaptive = screenConfig.isAdaptive,
                        isCollapsible = screenConfig.isCollapsible,
                        collapseGravity = screenConfig.collapseGravity
                    )
                )
            }
        }
    }


    private fun AdsInterstitialRemoteConfigDto?.toDomain(
        fallback: InterstitialAdConfig,
        warnings: MutableList<String>
    ): InterstitialAdConfig {
        if (this == null) return fallback

        val resolvedMaxAdAgeMillis = maxAdCacheDurationMillis.validPositiveOrDefault(
            defaultValue = fallback.maxAdAgeMillis,
            fieldName = "ads_interstitial_config.max_ad_cache_duration_ms",
            warnings = warnings
        )

        val resolvedMinIntervalBetweenShowsMillis =
            minIntervalBetweenShowsMillis.validZeroOrPositiveOrDefault(
                defaultValue = fallback.minIntervalBetweenShowsMillis,
                fieldName = "ads_interstitial_config.min_interval_between_shows_ms",
                warnings = warnings
            )

        val screenConfigs = screens.toDomainInterstitialScreens(
            fallback = fallback.screens,
            fallbackMaxAdAgeMillis = resolvedMaxAdAgeMillis,
            fallbackMinIntervalBetweenShowsMillis = resolvedMinIntervalBetweenShowsMillis,
            warnings = warnings
        )

        val featureConfigs = features.toDomainInterstitialFeatures(
            fallback = fallback.features,
            warnings = warnings
        )

        val placementOverrides = buildMap<String, InterstitialPlacementConfig> {
            screenConfigs.forEach { (screenKey, screenConfig) ->
                put(
                    screenKey,
                    InterstitialPlacementConfig(
                        enabled = screenConfig.enabled,
                        adUnitId = screenConfig.adUnitId,
                        maxAdAgeMillis = screenConfig.maxAdAgeMillis,
                        minIntervalBetweenShowsMillis = screenConfig.minIntervalBetweenShowsMillis
                    )
                )
            }

            featureConfigs.forEach { (featureKey, featureConfig) ->
                put(
                    featureKey,
                    InterstitialPlacementConfig(
                        enabled = featureConfig.enabled,
                        adUnitId = featureConfig.adUnitId,
                        maxAdAgeMillis = null,
                        minIntervalBetweenShowsMillis = null
                    )
                )
            }

            splashUnitId.cleanOrNull()?.let { id ->
                if (!containsKey("splash")) {
                    put(
                        "splash",
                        InterstitialPlacementConfig(
                            enabled = true,
                            adUnitId = id
                        )
                    )
                }
            }

            languageUnitId.cleanOrNull()?.let { id ->
                if (!containsKey("language")) {
                    put(
                        "language",
                        InterstitialPlacementConfig(
                            enabled = true,
                            adUnitId = id
                        )
                    )
                }

                if (!containsKey("app_language")) {
                    put(
                        "app_language",
                        InterstitialPlacementConfig(
                            enabled = true,
                            adUnitId = id
                        )
                    )
                }
            }

            introUnitId.cleanOrNull()?.let { id ->
                if (!containsKey("intro")) {
                    put(
                        "intro",
                        InterstitialPlacementConfig(
                            enabled = true,
                            adUnitId = id
                        )
                    )
                }

                if (!containsKey("onboarding")) {
                    put(
                        "onboarding",
                        InterstitialPlacementConfig(
                            enabled = true,
                            adUnitId = id
                        )
                    )
                }
            }

            premiumUnitId.cleanOrNull()?.let { id ->
                if (!containsKey("premium")) {
                    put(
                        "premium",
                        InterstitialPlacementConfig(
                            enabled = true,
                            adUnitId = id
                        )
                    )
                }
            }
        }

        return InterstitialAdConfig(
            enabled = enabled ?: fallback.enabled,

            adUnitId = interstitialAdId.cleanOrNull()
                ?: fallback.adUnitId,

            splashUnitId = splashUnitId.cleanOrNull()
                ?: fallback.splashUnitId,

            languageUnitId = languageUnitId.cleanOrNull()
                ?: fallback.languageUnitId,

            introUnitId = introUnitId.cleanOrNull()
                ?: fallback.introUnitId,

            premiumUnitId = premiumUnitId.cleanOrNull()
                ?: fallback.premiumUnitId,

            globalMinIntervalBetweenShowsMillis =
                minIntervalBetweenShowsMillis.validZeroOrPositiveOrDefault(
                    defaultValue = fallback.globalMinIntervalBetweenShowsMillis,
                    fieldName = "ads_interstitial_config.min_interval_between_shows_ms",
                    warnings = warnings
                ),

            maxAdAgeMillis = resolvedMaxAdAgeMillis,

            minIntervalBetweenShowsMillis = resolvedMinIntervalBetweenShowsMillis,

            retryConfig = CommonAdRetryConfig(
                maxLoadRetryCount = maxLoadRetryCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.retryConfig.maxLoadRetryCount,
                    fieldName = "ads_interstitial_config.max_load_retry_count",
                    warnings = warnings
                ),
                initialRetryDelayMillis =
                    initialRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.initialRetryDelayMillis,
                        fieldName = "ads_interstitial_config.initial_retry_delay_ms",
                        warnings = warnings
                    ),
                maxRetryDelayMillis =
                    maxRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.maxRetryDelayMillis,
                        fieldName = "ads_interstitial_config.max_retry_delay_ms",
                        warnings = warnings
                    )
            ),

            triggers = InterstitialTriggerConfig(
                showOnTabSwitch = showOnTabSwitch ?: fallback.triggers.showOnTabSwitch,
                tabSwitchTriggerCount = tabSwitchTriggerCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.triggers.tabSwitchTriggerCount,
                    fieldName = "ads_interstitial_config.tab_switch_trigger_count",
                    warnings = warnings
                ),

                showOnPremium = showOnPremium ?: fallback.triggers.showOnPremium,
                premiumTriggerCount = premiumTriggerCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.triggers.premiumTriggerCount,
                    fieldName = "ads_interstitial_config.premium_trigger_count",
                    warnings = warnings
                ),

                showOnOnboarding = showOnOnboarding ?: fallback.triggers.showOnOnboarding,
                onboardingTriggerCount = onboardingTriggerCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.triggers.onboardingTriggerCount,
                    fieldName = "ads_interstitial_config.onboarding_trigger_count",
                    warnings = warnings
                ),

                showOnLanguage = showOnLanguage ?: fallback.triggers.showOnLanguage,
                languageTriggerCount = languageTriggerCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.triggers.languageTriggerCount,
                    fieldName = "ads_interstitial_config.language_trigger_count",
                    warnings = warnings
                ),

                showOnImageResult = showOnImageResult ?: fallback.triggers.showOnImageResult,
                imageResultTriggerCount = imageResultTriggerCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.triggers.imageResultTriggerCount,
                    fieldName = "ads_interstitial_config.image_result_trigger_count",
                    warnings = warnings
                ),

                showOnBackNavigation =
                    showOnBackNavigation ?: fallback.triggers.showOnBackNavigation,
                backNavigationTriggerCount =
                    backNavigationTriggerCount.validOneOrPositiveOrDefault(
                        defaultValue = fallback.triggers.backNavigationTriggerCount,
                        fieldName = "ads_interstitial_config.back_navigation_trigger_count",
                        warnings = warnings
                    )
            ),

            screens = screenConfigs,

            features = featureConfigs,

            placements = placementOverrides
        )
    }

    private fun Map<String, InterstitialScreenRemoteConfigDto>?.toDomainInterstitialScreens(
        fallback: Map<String, InterstitialScreenConfig>,
        fallbackMaxAdAgeMillis: Long,
        fallbackMinIntervalBetweenShowsMillis: Long,
        warnings: MutableList<String>
    ): Map<String, InterstitialScreenConfig> {
        if (this == null) return fallback

        return mapValues { (screenKey, dto) ->
            InterstitialScreenConfig(
                enabled = dto.enabled ?: false,
                triggerCount = dto.triggerCount.validOneOrPositiveOrDefault(
                    defaultValue = 1,
                    fieldName = "ads_interstitial_config.screens.$screenKey.trigger_count",
                    warnings = warnings
                ),
                adUnitId = dto.unitId.cleanOrNull(),
                maxAdAgeMillis = dto.maxAdCacheDurationMillis?.validPositiveOrDefault(
                    defaultValue = fallbackMaxAdAgeMillis,
                    fieldName = "ads_interstitial_config.screens.$screenKey.max_ad_cache_duration_ms",
                    warnings = warnings
                ),
                minIntervalBetweenShowsMillis =
                    dto.minIntervalBetweenShowsMillis?.validZeroOrPositiveOrDefault(
                        defaultValue = fallbackMinIntervalBetweenShowsMillis,
                        fieldName = "ads_interstitial_config.screens.$screenKey.min_interval_between_shows_ms",
                        warnings = warnings
                    )
            )
        }
    }



    private fun AdsRewardedRemoteConfigDto?.toDomain(
        fallback: RewardedAdConfig,
        warnings: MutableList<String>
    ): RewardedAdConfig {
        if (this == null) return fallback

        return RewardedAdConfig(
            enabled = enabled ?: fallback.enabled,

            adUnitId = rewardedAdId.cleanOrNull()
                ?: fallback.adUnitId,

            globalMinIntervalBetweenShowsMillis =
                minIntervalBetweenShowsMillis.validZeroOrPositiveOrDefault(
                    defaultValue = fallback.globalMinIntervalBetweenShowsMillis,
                    fieldName = "ads_rewarded_config.min_interval_between_shows_ms",
                    warnings = warnings
                ),

            maxAdAgeMillis = maxAdCacheDurationMillis.validPositiveOrDefault(
                defaultValue = fallback.maxAdAgeMillis,
                fieldName = "ads_rewarded_config.max_ad_cache_duration_ms",
                warnings = warnings
            ),

            minIntervalBetweenShowsMillis =
                minIntervalBetweenShowsMillis.validZeroOrPositiveOrDefault(
                    defaultValue = fallback.minIntervalBetweenShowsMillis,
                    fieldName = "ads_rewarded_config.min_interval_between_shows_ms",
                    warnings = warnings
                ),

            retryConfig = CommonAdRetryConfig(
                maxLoadRetryCount = maxLoadRetryCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.retryConfig.maxLoadRetryCount,
                    fieldName = "ads_rewarded_config.max_load_retry_count",
                    warnings = warnings
                ),
                initialRetryDelayMillis =
                    initialRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.initialRetryDelayMillis,
                        fieldName = "ads_rewarded_config.initial_retry_delay_ms",
                        warnings = warnings
                    ),
                maxRetryDelayMillis =
                    maxRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.maxRetryDelayMillis,
                        fieldName = "ads_rewarded_config.max_retry_delay_ms",
                        warnings = warnings
                    )
            ),

            placements = placements.toDomainRewardedPlacements(
                fallback = fallback.placements
            )
        )
    }

    private fun Map<String, RewardedPlacementRemoteConfigDto>?.toDomainRewardedPlacements(
        fallback: Map<String, RewardedPlacementConfig>
    ): Map<String, RewardedPlacementConfig> {
        if (this == null) return fallback

        return mapValues { (_, dto) ->
            RewardedPlacementConfig(
                enabled = dto.enabled,
                adUnitId = dto.unitId.cleanOrNull(),
                rewardType = dto.rewardType.cleanOrNull(),
                rewardAmount = dto.rewardAmount?.takeIf { it > 0 }
            )
        }
    }

    private fun AdsNativeRemoteConfigDto?.toDomain(
        fallback: NativeAdConfig,
        warnings: MutableList<String>
    ): NativeAdConfig {
        if (this == null) return fallback

        return NativeAdConfig(
            enabled = enabled ?: fallback.enabled,

            adUnitId = nativeAdId.cleanOrNull()
                ?: fallback.adUnitId,

            size = fallback.size,

            maxAdAgeMillis = maxAdCacheDurationMillis.validPositiveOrDefault(
                defaultValue = fallback.maxAdAgeMillis,
                fieldName = "ads_native_config.max_ad_cache_duration_ms",
                warnings = warnings
            ),

            retryConfig = CommonAdRetryConfig(
                maxLoadRetryCount = maxLoadRetryCount.validOneOrPositiveOrDefault(
                    defaultValue = fallback.retryConfig.maxLoadRetryCount,
                    fieldName = "ads_native_config.max_load_retry_count",
                    warnings = warnings
                ),
                initialRetryDelayMillis =
                    initialRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.initialRetryDelayMillis,
                        fieldName = "ads_native_config.initial_retry_delay_ms",
                        warnings = warnings
                    ),
                maxRetryDelayMillis =
                    maxRetryDelayMillis.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.retryConfig.maxRetryDelayMillis,
                        fieldName = "ads_native_config.max_retry_delay_ms",
                        warnings = warnings
                    )
            ),

            styleConfig = NativeAdStyleConfig(
                containerBackgroundColor =
                    containerBackgroundColor.cleanOrNull()
                        ?: fallback.styleConfig.containerBackgroundColor,

                containerBorderColor =
                    containerBorderColor.cleanOrNull()
                        ?: fallback.styleConfig.containerBorderColor,

                containerBorderWidthDp =
                    containerBorderWidthDp.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.styleConfig.containerBorderWidthDp,
                        fieldName = "ads_native_config.container_border_width_dp",
                        warnings = warnings
                    ),

                mediaBackgroundColor =
                    mediaBackgroundColor.cleanOrNull()
                        ?: fallback.styleConfig.mediaBackgroundColor,

                headlineTextColor =
                    headlineTextColor.cleanOrNull()
                        ?: fallback.styleConfig.headlineTextColor,

                bodyTextColor =
                    bodyTextColor.cleanOrNull()
                        ?: fallback.styleConfig.bodyTextColor,

                ctaBackgroundColor =
                    ctaBackgroundColor.cleanOrNull()
                        ?: fallback.styleConfig.ctaBackgroundColor,

                ctaTextColor =
                    ctaTextColor.cleanOrNull()
                        ?: fallback.styleConfig.ctaTextColor,

                adAttributionTextColor =
                    adAttributionTextColor.cleanOrNull()
                        ?: fallback.styleConfig.adAttributionTextColor,

                adAttributionBackgroundColor =
                    adAttributionBackgroundColor.cleanOrNull()
                        ?: fallback.styleConfig.adAttributionBackgroundColor,

                adAttributionBorderColor =
                    adAttributionBorderColor.cleanOrNull()
                        ?: fallback.styleConfig.adAttributionBorderColor,

                starRatingColor =
                    starRatingColor.cleanOrNull()
                        ?: fallback.styleConfig.starRatingColor,

                cornerRadiusDp =
                    cornerRadiusDp.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.styleConfig.cornerRadiusDp,
                        fieldName = "ads_native_config.corner_radius_dp",
                        warnings = warnings
                    ),

                ctaCornerRadiusDp =
                    ctaCornerRadiusDp.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.styleConfig.ctaCornerRadiusDp,
                        fieldName = "ads_native_config.cta_corner_radius_dp",
                        warnings = warnings
                    ),

                adBadgeCornerRadiusDp =
                    adBadgeCornerRadiusDp.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.styleConfig.adBadgeCornerRadiusDp,
                        fieldName = "ads_native_config.ad_badge_corner_radius_dp",
                        warnings = warnings
                    ),

                mediaCornerRadiusDp =
                    mediaCornerRadiusDp.validZeroOrPositiveOrDefault(
                        defaultValue = fallback.styleConfig.mediaCornerRadiusDp,
                        fieldName = "ads_native_config.media_corner_radius_dp",
                        warnings = warnings
                    )
            ),

            placements = placements.toDomainNativePlacements(
                fallback = fallback.placements,
                parentAdUnitId = nativeAdId.cleanOrNull() ?: fallback.adUnitId,
                parentMaxAdAgeMillis = maxAdCacheDurationMillis.validPositiveOrDefault(
                    defaultValue = fallback.maxAdAgeMillis,
                    fieldName = "ads_native_config.max_ad_cache_duration_ms",
                    warnings = warnings
                ),
                warnings = warnings
            )
        )
    }


    private fun AdsLoadingDialogRemoteConfigDto?.toDomain(
        fallback: AdLoadingDialogConfig,
        warnings: MutableList<String>
    ): AdLoadingDialogConfig {
        if (this == null) return fallback

        return AdLoadingDialogConfig(
            enabled = enabled ?: fallback.enabled,

            showForAppOpen = showForAppOpen
                ?: fallback.showForAppOpen,

            showForInterstitial = showForInterstitial
                ?: fallback.showForInterstitial,

            showForRewarded = showForRewarded
                ?: fallback.showForRewarded,

            durationMs = durationMs.validZeroOrPositiveOrDefault(
                defaultValue = fallback.durationMs,
                fieldName = "ads_loading_dialog_config.duration_ms",
                warnings = warnings
            ),

            appOpenDurationMs = appOpenDurationMs.validNullableZeroOrPositiveOrDefault(
                defaultValue = fallback.appOpenDurationMs,
                fieldName = "ads_loading_dialog_config.app_open_duration_ms",
                warnings = warnings
            ),

            interstitialDurationMs = interstitialDurationMs.validNullableZeroOrPositiveOrDefault(
                defaultValue = fallback.interstitialDurationMs,
                fieldName = "ads_loading_dialog_config.interstitial_duration_ms",
                warnings = warnings
            ),

            rewardedDurationMs = rewardedDurationMs.validNullableZeroOrPositiveOrDefault(
                defaultValue = fallback.rewardedDurationMs,
                fieldName = "ads_loading_dialog_config.rewarded_duration_ms",
                warnings = warnings
            ),

            title = title.cleanOrNull()
                ?: fallback.title,

            message = message.cleanOrNull()
                ?: fallback.message,

            appOpenTitle = appOpenTitle.cleanOrNull()
                ?: fallback.appOpenTitle,

            appOpenMessage = appOpenMessage.cleanOrNull()
                ?: fallback.appOpenMessage,

            interstitialTitle = interstitialTitle.cleanOrNull()
                ?: fallback.interstitialTitle,

            interstitialMessage = interstitialMessage.cleanOrNull()
                ?: fallback.interstitialMessage,

            rewardedTitle = rewardedTitle.cleanOrNull()
                ?: fallback.rewardedTitle,

            rewardedMessage = rewardedMessage.cleanOrNull()
                ?: fallback.rewardedMessage
        )
    }

    private fun Map<String, NativePlacementRemoteConfigDto>?.toDomainNativePlacements(
        fallback: Map<String, NativePlacementConfig>,
        parentAdUnitId: String?,
        parentMaxAdAgeMillis: Long,
        warnings: MutableList<String>
    ): Map<String, NativePlacementConfig> {
        if (this == null) return fallback

        return mapValues { (placementKey, dto) ->
            val parsedPosition = dto.position.toAdContentPosition(
                fallback = AdContentPosition.BOTTOM,
                fieldName = "ads_native_config.placements.$placementKey.position",
                warnings = warnings
            )

            val parsedSize = dto.style.toNativeAdSize(
                fallback = if (parsedPosition == AdContentPosition.FULL_PAGE) {
                    NativeAdSize.LARGE
                } else {
                    NativeAdSize.MEDIUM
                },
                fieldName = "ads_native_config.placements.$placementKey.style",
                warnings = warnings
            )

            val safeSize = when {
                parsedPosition == AdContentPosition.FULL_PAGE -> {
                    NativeAdSize.LARGE
                }

                parsedSize == NativeAdSize.LARGE -> {
                    warnings.add(
                        "ads_native_config.placements.$placementKey.style is LARGE, " +
                                "but inline native supports only Small/Medium. Using MEDIUM."
                    )
                    NativeAdSize.MEDIUM
                }

                else -> parsedSize
            }

            NativePlacementConfig(
                enabled = dto.enabled,
                adUnitId = dto.unitId.cleanOrNull() ?: parentAdUnitId,
                size = safeSize,
                position = parsedPosition,
                showPlaceholder = dto.showPlaceholder,
                maxAdAgeMillis = parentMaxAdAgeMillis
            )
        }
    }

    private fun String?.cleanOrNull(): String? {
        return this?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun String?.toCollapseGravity(
        fallback: Int,
        fieldName: String,
        warnings: MutableList<String>
    ): Int {
        return when (this?.trim()?.lowercase()) {
            "top", "t" -> BannerAdConfig.CollapseGravity.TOP
            "bottom", "b" -> BannerAdConfig.CollapseGravity.BOTTOM
            null, "" -> fallback
            else -> {
                warnings.add("$fieldName has invalid value=$this. Using fallback=$fallback.")
                fallback
            }
        }
    }

    private fun String?.toNativeAdSize(
        fallback: NativeAdSize,
        fieldName: String,
        warnings: MutableList<String>
    ): NativeAdSize {
        return when (this?.trim()?.lowercase()) {
            "small", "s" -> NativeAdSize.SMALL
            "medium", "m" -> NativeAdSize.MEDIUM
            "large", "l", "full", "fullpage", "full_page", "full-page" -> NativeAdSize.LARGE
            null, "" -> fallback
            else -> {
                warnings.add("$fieldName has invalid value=$this. Using fallback=$fallback.")
                fallback
            }
        }
    }

    private fun String?.toAdContentPosition(
        fallback: AdContentPosition,
        fieldName: String,
        warnings: MutableList<String>
    ): AdContentPosition {
        return when (this?.trim()?.lowercase()) {
            "top", "t" -> AdContentPosition.TOP
            "bottom", "b" -> AdContentPosition.BOTTOM
            "fullpage", "full_page", "full-page", "full", "f" -> AdContentPosition.FULL_PAGE
            null, "" -> fallback
            else -> {
                warnings.add("$fieldName has invalid value=$this. Using fallback=$fallback.")
                fallback
            }
        }
    }

    private fun Long?.validPositiveOrDefault(
        defaultValue: Long,
        fieldName: String,
        warnings: MutableList<String>
    ): Long {
        if (this == null) return defaultValue

        return if (this > 0L) {
            this
        } else {
            warnings.add("$fieldName must be > 0. Using fallback=$defaultValue.")
            defaultValue
        }
    }

    private fun Long?.validZeroOrPositiveOrDefault(
        defaultValue: Long,
        fieldName: String,
        warnings: MutableList<String>
    ): Long {
        if (this == null) return defaultValue

        return if (this >= 0L) {
            this
        } else {
            warnings.add("$fieldName must be >= 0. Using fallback=$defaultValue.")
            defaultValue
        }
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

    private fun Int?.validZeroOrPositiveOrDefault(
        defaultValue: Int,
        fieldName: String,
        warnings: MutableList<String>
    ): Int {
        if (this == null) return defaultValue

        return if (this >= 0) {
            this
        } else {
            warnings.add("$fieldName must be >= 0. Using fallback=$defaultValue.")
            defaultValue
        }
    }

    private data class ResolvedGlobalConfig(
        val adsEnabled: Boolean,
        val canRequestAds: Boolean,
        val isDebug: Boolean
    )

    private fun Map<String, InterstitialFeatureRemoteConfigDto>?.toDomainInterstitialFeatures(
        fallback: Map<String, InterstitialFeatureConfig>,
        warnings: MutableList<String>
    ): Map<String, InterstitialFeatureConfig> {
        if (this == null) return fallback

        return mapValues { (featureKey, dto) ->
            InterstitialFeatureConfig(
                enabled = dto.enabled ?: false,
                triggerCount = dto.triggerCount.validOneOrPositiveOrDefault(
                    defaultValue = 1,
                    fieldName = "ads_interstitial_config.features.$featureKey.trigger_count",
                    warnings = warnings
                ),
                adUnitId = dto.unitId.cleanOrNull()
            )
        }
    }

    private fun Long?.validNullableZeroOrPositiveOrDefault(
        defaultValue: Long?,
        fieldName: String,
        warnings: MutableList<String>
    ): Long? {
        if (this == null) return defaultValue

        return if (this >= 0L) {
            this
        } else {
            warnings.add("$fieldName must be >= 0. Using fallback=$defaultValue.")
            defaultValue
        }
    }
}