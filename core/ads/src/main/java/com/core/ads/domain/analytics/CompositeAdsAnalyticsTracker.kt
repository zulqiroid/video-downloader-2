package com.core.ads.domain.analytics

import com.core.ads.domain.analytics.AdsAnalyticsEvent
import com.core.ads.domain.analytics.AdsAnalyticsTracker

/**
 * Sends the same ad analytics event to multiple trackers.
 *
 * Example:
 * - Firebase
 * - Logcat
 * - Your backend
 */
class CompositeAdsAnalyticsTracker(
    private val trackers: List<AdsAnalyticsTracker>
) : AdsAnalyticsTracker {

    override fun track(event: AdsAnalyticsEvent) {
        trackers.forEach { tracker ->
            runCatching {
                tracker.track(event)
            }
        }
    }
}