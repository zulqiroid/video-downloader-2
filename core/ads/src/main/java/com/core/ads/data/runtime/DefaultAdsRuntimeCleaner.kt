package com.core.ads.data.runtime

import com.core.ads.domain.appopen.AppOpenAdController
import com.core.ads.domain.banner.BannerAdController
import com.core.ads.domain.display.AdsDisplayGuard
import com.core.ads.domain.interstitial.InterstitialAdController
import com.core.ads.domain.nativead.NativeAdController
import com.core.ads.domain.rewarded.RewardedAdController
import com.core.ads.domain.runtime.AdsCleanupReason
import com.core.ads.domain.runtime.AdsRuntimeCleaner
import com.core.ads.utils.AdsLogger

class DefaultAdsRuntimeCleaner(
    private val appOpenAdController: AppOpenAdController,
    private val interstitialAdController: InterstitialAdController,
    private val rewardedAdController: RewardedAdController,
    private val bannerAdController: BannerAdController,
    private val nativeAdController: NativeAdController,
    private val adsDisplayGuard: AdsDisplayGuard
) : AdsRuntimeCleaner{

    override fun clearAll(
        reason: AdsCleanupReason,
        message: String?
    ) {
        AdsLogger.w(
            "Clearing all ads. reason=$reason, message=$message"
        )

        /**
         * AppOpen: single app-level cache.
         */
        runCatching {
            appOpenAdController.clear()
        }.onFailure {
            AdsLogger.e("Failed to clear AppOpen ads", it)
        }

        /**
         * Interstitial: placement-based cache.
         */
        runCatching {
            interstitialAdController.clearAll()
        }.onFailure {
            AdsLogger.e("Failed to clear Interstitial ads", it)
        }

        /**
         * Rewarded: placement-based cache.
         */
        runCatching {
            rewardedAdController.clearAll()
        }.onFailure {
            AdsLogger.e("Failed to clear Rewarded ads", it)
        }

        /**
         * Banner: destroy all AdViews to prevent memory leaks.
         */
        runCatching {
            bannerAdController.destroyAll()
        }.onFailure {
            AdsLogger.e("Failed to destroy Banner ads", it)
        }

        /**
         * Native: destroy all NativeAd objects.
         *
         * Nullable because you may register NativeAdController later.
         */
        runCatching {
            nativeAdController.clearAll()
        }.onFailure {
            AdsLogger.e("Failed to clear Native ads", it)
        }

        /**
         * Force clear global display state only when ads are blocked globally.
         *
         * For CONFIG_CHANGED, we do not strictly need to clear display guard
         * because an ad may currently be showing and should finish naturally.
         */
        val shouldForceClearDisplayGuard = reason in setOf(
            AdsCleanupReason.ADS_DISABLED,
            AdsCleanupReason.REQUESTS_DISABLED,
            AdsCleanupReason.USER_POLICY_BLOCKED,
            AdsCleanupReason.USER_BECAME_PREMIUM,
            AdsCleanupReason.REMOVE_ADS_PURCHASED,
            AdsCleanupReason.CONSENT_REVOKED,
            AdsCleanupReason.SDK_BLOCKED,
            AdsCleanupReason.SDK_FAILED,
            AdsCleanupReason.MANUAL
        )

        if (shouldForceClearDisplayGuard) {
            runCatching {
                adsDisplayGuard.clear()
            }.onFailure {
                AdsLogger.e("Failed to clear display guard", it)
            }
        }
    }
}