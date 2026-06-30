package com.core.ads.domain.analytics

import com.core.ads.domain.analytics.AdsAnalyticsEvent
import com.core.ads.domain.analytics.AdsAnalyticsTracker

/**
 * Default tracker when app module does not provide analytics.
 *
 * This prevents crashes and keeps ads module reusable.
 */
class NoOpAdsAnalyticsTracker : AdsAnalyticsTracker {

    override fun track(event: AdsAnalyticsEvent) {
        // No-op by default.
    }
}