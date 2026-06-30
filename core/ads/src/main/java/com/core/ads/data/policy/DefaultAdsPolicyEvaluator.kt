package com.core.ads.data.policy

import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.analytics.AdBlockReason
import com.core.ads.domain.analytics.AdUnitSource
import com.core.ads.domain.display.AdsDisplayGuard
import com.core.ads.domain.init.AdsSdkStateProvider
import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.policy.AdsConsentPolicy
import com.core.ads.domain.policy.AdsOperation
import com.core.ads.domain.policy.AdsPolicyContext
import com.core.ads.domain.policy.AdsPolicyDecision
import com.core.ads.domain.policy.AdsPolicyEvaluator
import com.core.ads.domain.policy.AdsUserPolicy

class DefaultAdsPolicyEvaluator(
    private val configStore: AdsConfigStore,
    private val adsUserPolicy: AdsUserPolicy,
    private val adsConsentPolicy: AdsConsentPolicy,
    private val adsSdkStateProvider: AdsSdkStateProvider,
    private val adsDisplayGuard: AdsDisplayGuard
) : AdsPolicyEvaluator{

    override fun evaluate(context: AdsPolicyContext): AdsPolicyDecision {
        val config = configStore.current

        if (!config.adsEnabled) {
            return blocked(
                reason = AdBlockReason.ADS_DISABLED,
                message = "Ads are globally disabled."
            )
        }

        if (!config.canRequestAds) {
            return blocked(
                reason = AdBlockReason.REQUESTS_DISABLED,
                message = "Ad requests are disabled by config."
            )
        }

        /**
         * Premium/remove-ads user check.
         *
         * This must block BOTH load and show.
         * We do not even request ads for premium users.
         */
        if (!adsUserPolicy.canShowAds()) {
            return blocked(
                reason = AdBlockReason.USER_POLICY_BLOCKED,
                message = "User policy blocked ads. User may be premium or remove-ads user."
            )
        }

        if (!adsConsentPolicy.canRequestAds()) {
            return blocked(
                reason = AdBlockReason.CONSENT_NOT_ALLOWED,
                message = "Consent policy does not allow ad requests."
            )
        }

        if (!adsSdkStateProvider.current.isInitialized) {
            return blocked(
                reason = AdBlockReason.SDK_NOT_INITIALIZED,
                message = "Google Mobile Ads SDK is not initialized. " +
                        "status=${adsSdkStateProvider.current.status}, " +
                        "message=${adsSdkStateProvider.current.message}"
            )
        }

        if (!context.isAppInForeground && context.operation == AdsOperation.SHOW) {
            return blocked(
                reason = AdBlockReason.APP_IN_BACKGROUND,
                message = "Cannot show ad while app is in background."
            )
        }

        if (
            context.operation == AdsOperation.SHOW &&
            !adsDisplayGuard.canShow(
                format = context.format,
                placement = context.placement
            )
        ) {
            return blocked(
                reason = AdBlockReason.DISPLAY_GUARD_BLOCKED,
                message = "Another full-screen ad is already showing."
            )
        }

        if (context.isShowing) {
            return blocked(
                reason = AdBlockReason.ALREADY_SHOWING,
                message = "An ad is already showing for this placement or format."
            )
        }

        if (context.operation == AdsOperation.LOAD) {
            if (context.isLoading) {
                return blocked(
                    reason = AdBlockReason.ALREADY_LOADING,
                    message = "Ad is already loading."
                )
            }

            if (context.hasFreshCache) {
                return blocked(
                    reason = AdBlockReason.CACHE_AVAILABLE,
                    message = "A fresh cached ad is already available."
                )
            }
        }

        if (context.operation == AdsOperation.SHOW) {
            val activity = context.activity

            if (activity == null) {
                return blocked(
                    reason = AdBlockReason.ACTIVITY_INVALID,
                    message = "Activity is required to show this ad."
                )
            }

            if (activity.isFinishing) {
                return blocked(
                    reason = AdBlockReason.ACTIVITY_FINISHING,
                    message = "Activity is finishing."
                )
            }

            if (activity.isDestroyed) {
                return blocked(
                    reason = AdBlockReason.ACTIVITY_DESTROYED,
                    message = "Activity is destroyed."
                )
            }

            if (context.cooldownRemainingMillis > 0L) {
                return blocked(
                    reason = AdBlockReason.COOLDOWN_ACTIVE,
                    message = "Cooldown active. Remaining=${context.cooldownRemainingMillis}ms."
                )
            }

            if (!context.hasFreshCache) {
                return blocked(
                    reason = AdBlockReason.CACHE_NOT_AVAILABLE,
                    message = "No fresh cached ad available."
                )
            }
        }

        return resolveAdUnit(context)
    }

    private fun resolveAdUnit(
        context: AdsPolicyContext
    ): AdsPolicyDecision {
        val config = configStore.current

        return when (context.format) {
            AdFormat.APP_OPEN -> {
                val appOpenConfig = config.appOpenAdConfig

                if (!appOpenConfig.enabled) {
                    blocked(
                        reason = AdBlockReason.FORMAT_DISABLED,
                        message = "AppOpen ad format is disabled."
                    )
                } else {
                    val adUnitId =
                        appOpenConfig.adUnitId?.takeIf { it.isNotBlank() }
                            ?: appOpenConfig.splashAdUnitId?.takeIf { it.isNotBlank() }

                    if (adUnitId == null) {
                        blocked(
                            reason = AdBlockReason.AD_UNIT_ID_MISSING,
                            message = "AppOpen ad unit ID is missing."
                        )
                    } else {
                        AdsPolicyDecision.Allowed(
                            adUnitId = adUnitId,
                            adUnitSource = AdUnitSource.GLOBAL
                        )
                    }
                }
            }

            AdFormat.INTERSTITIAL -> {
                val resolved = config.resolveInterstitialConfig(context.placement)

                if (resolved == null) {
                    blocked(
                        reason = detectInterstitialBlockReason(context.placement.value),
                        message = "Interstitial config is disabled or ad unit ID is missing for placement=${context.placement.value}."
                    )
                } else {
                    AdsPolicyDecision.Allowed(
                        adUnitId = resolved.adUnitId,
                        adUnitSource = if (resolved.isUsingPlacementAdUnitId) {
                            AdUnitSource.PLACEMENT
                        } else {
                            AdUnitSource.GLOBAL
                        }
                    )
                }
            }

            AdFormat.REWARDED -> {
                val resolved = config.resolveRewardedConfig(context.placement)

                if (resolved == null) {
                    blocked(
                        reason = detectRewardedBlockReason(context.placement.value),
                        message = "Rewarded config is disabled or ad unit ID is missing for placement=${context.placement.value}."
                    )
                } else {
                    AdsPolicyDecision.Allowed(
                        adUnitId = resolved.adUnitId,
                        adUnitSource = if (resolved.isUsingPlacementAdUnitId) {
                            AdUnitSource.PLACEMENT
                        } else {
                            AdUnitSource.GLOBAL
                        }
                    )
                }
            }

            AdFormat.BANNER -> {
                val resolved = config.resolveBannerConfig(context.placement)

                if (resolved == null) {
                    blocked(
                        reason = detectBannerBlockReason(context.placement.value),
                        message = "Banner config is disabled or ad unit ID is missing for placement=${context.placement.value}."
                    )
                } else {
                    AdsPolicyDecision.Allowed(
                        adUnitId = resolved.adUnitId,
                        adUnitSource = if (resolved.isUsingPlacementAdUnitId) {
                            AdUnitSource.PLACEMENT
                        } else {
                            AdUnitSource.GLOBAL
                        }
                    )
                }
            }

            AdFormat.NATIVE -> {
                val resolved = config.resolveNativeConfig(context.placement)

                if (resolved == null) {
                    blocked(
                        reason = detectNativeBlockReason(context.placement.value),
                        message = "Native config is disabled or ad unit ID is missing for placement=${context.placement.value}."
                    )
                } else {
                    AdsPolicyDecision.Allowed(
                        adUnitId = resolved.adUnitId,
                        adUnitSource = if (resolved.isUsingPlacementAdUnitId) {
                            AdUnitSource.PLACEMENT
                        } else {
                            AdUnitSource.GLOBAL
                        }
                    )
                }
            }
        }
    }

    private fun detectInterstitialBlockReason(placement: String): AdBlockReason {
        val cfg = configStore.current.interstitialAdConfig
        if (!cfg.enabled) return AdBlockReason.FORMAT_DISABLED
        if (cfg.placements[placement]?.enabled == false) return AdBlockReason.PLACEMENT_DISABLED
        return AdBlockReason.AD_UNIT_ID_MISSING
    }

    private fun detectRewardedBlockReason(placement: String): AdBlockReason {
        val cfg = configStore.current.rewardedAdConfig
        if (!cfg.enabled) return AdBlockReason.FORMAT_DISABLED
        if (cfg.placements[placement]?.enabled == false) return AdBlockReason.PLACEMENT_DISABLED
        return AdBlockReason.AD_UNIT_ID_MISSING
    }

    private fun detectBannerBlockReason(placement: String): AdBlockReason {
        val cfg = configStore.current.bannerAdConfig
        if (!cfg.enabled) return AdBlockReason.FORMAT_DISABLED
        if (cfg.placements[placement]?.enabled == false) return AdBlockReason.PLACEMENT_DISABLED
        return AdBlockReason.AD_UNIT_ID_MISSING
    }

    private fun detectNativeBlockReason(placement: String): AdBlockReason {
        val cfg = configStore.current.nativeAdConfig
        if (!cfg.enabled) return AdBlockReason.FORMAT_DISABLED
        if (cfg.placements[placement]?.enabled == false) return AdBlockReason.PLACEMENT_DISABLED
        return AdBlockReason.AD_UNIT_ID_MISSING
    }

    private fun blocked(
        reason: AdBlockReason,
        message: String
    ): AdsPolicyDecision.Blocked {
        return AdsPolicyDecision.Blocked(
            reason = reason,
            message = message
        )
    }
}