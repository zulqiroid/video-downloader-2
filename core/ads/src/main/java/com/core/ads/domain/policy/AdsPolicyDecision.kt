package com.core.ads.domain.policy

import com.core.ads.domain.analytics.AdBlockReason
import com.core.ads.domain.analytics.AdUnitSource

/**
 * Final policy decision for an ad operation.
 */
sealed interface AdsPolicyDecision {

    data class Allowed(
        val adUnitId: String,
        val adUnitSource: AdUnitSource
    ) : AdsPolicyDecision

    data class Blocked(
        val reason: AdBlockReason,
        val message: String
    ) : AdsPolicyDecision
}