package com.core.ads.domain.analytics

/**
 * Public analytics contract.
 *
 * App module can provide:
 * - FirebaseAdsAnalyticsTracker
 * - BackendAdsAnalyticsTracker
 * - LogcatAdsAnalyticsTracker
 * - Composite tracker
 *
 * Ads module should only depend on this interface.
 */
interface AdsAnalyticsTracker {

    fun track(event: AdsAnalyticsEvent)
}