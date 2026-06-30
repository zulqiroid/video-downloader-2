package com.core.ads.domain.policy

import kotlinx.coroutines.flow.StateFlow

/**
 * AdsConsentPolicy handles privacy/consent decision.
 */
interface AdsConsentPolicy {

    val state: StateFlow<AdsConsentState>

    fun canRequestAds(): Boolean {
        return state.value.canRequestAds
    }

    fun canRequestPersonalizedAds(): Boolean {
        return state.value.canRequestPersonalizedAds
    }
}