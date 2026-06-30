package com.core.ads.domain.analytics

import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.rewarded.RewardItem

/**
 * Every important ad lifecycle event should be represented here.
 *
 * This makes analytics:
 * - consistent
 * - testable
 * - Firebase-ready
 * - backend-ready
 * - easy to calculate show rate, load rate, fail rate, revenue, etc.
 */
sealed interface AdsAnalyticsEvent {

    val format: AdFormat
    val placement: AdPlacement
    val timestampMillis: Long

    /**
     * Fired when app asks the module to preload/load an ad.
     *
     * This is the denominator for load rate.
     */
    data class LoadRequested(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val source: AdRequestSource,
        val adUnitId: String?,
        val adUnitSource: AdUnitSource?,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when load request is blocked before calling Google SDK.
     *
     * Example:
     * - ads disabled
     * - consent not allowed
     * - premium user
     * - missing ad unit ID
     */
    data class LoadBlocked(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val reason: AdBlockReason,
        val message: String? = null,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when Google SDK successfully loads an ad.
     *
     * This is the numerator for load success rate.
     */
    data class Loaded(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String,
        val adUnitSource: AdUnitSource,
        val loadDurationMillis: Long? = null,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when Google SDK fails to load an ad.
     *
     * This helps calculate fail rate and no-fill/match-related behavior.
     */
    data class LoadFailed(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String?,
        val adUnitSource: AdUnitSource?,
        val error: AdErrorInfo,
        val loadDurationMillis: Long? = null,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when app requests to show an ad.
     *
     * This is the denominator for show-request analysis.
     */
    data class ShowRequested(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String?,
        val adUnitSource: AdUnitSource?,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when show request is blocked before showing.
     */
    data class ShowBlocked(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val reason: AdBlockReason,
        val message: String? = null,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when the ad is available and module is about to show it.
     */
    data class ShowEligible(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String,
        val adUnitSource: AdUnitSource,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired from FullScreenContentCallback.onAdShowedFullScreenContent()
     * or equivalent display callback.
     */
    data class ShowStarted(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when SDK reports impression.
     *
     * This is the strongest client-side signal that an ad was actually displayed.
     */
    data class Impression(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when user clicks the ad.
     */
    data class Clicked(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when full-screen ad is dismissed.
     */
    data class Dismissed(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when SDK fails to show a full-screen ad.
     */
    data class ShowFailed(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String?,
        val error: AdErrorInfo,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when a rewarded ad grants reward.
     */
    data class RewardEarned(
        override val placement: AdPlacement,
        val adUnitId: String,
        val reward: RewardItem,
        override val format: AdFormat = AdFormat.REWARDED,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent

    /**
     * Fired when Google paid event callback gives revenue.
     */
    data class PaidEvent(
        override val format: AdFormat,
        override val placement: AdPlacement,
        val adUnitId: String,
        val revenue: AdRevenueInfo,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : AdsAnalyticsEvent
}