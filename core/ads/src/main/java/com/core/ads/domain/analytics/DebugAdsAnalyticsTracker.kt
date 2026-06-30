package com.core.ads.domain.analytics

import com.core.ads.domain.analytics.AdsAnalyticsEvent
import com.core.ads.domain.analytics.AdsAnalyticsTracker
import com.core.ads.utils.AdsLogger

/**
 * Useful during development and QA.
 *
 * Later you can combine this with Firebase tracker.
 */
class DebugAdsAnalyticsTracker : AdsAnalyticsTracker {

    override fun track(event: AdsAnalyticsEvent) {
        AdsLogger.d("AdAnalyticsEvent: $event")
    }
}