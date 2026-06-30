package com.core.ads.domain.policy

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AdsUserPolicy handles user-specific ads decision.
 *
 * Important:
 * This must handle:
 * - premium user
 * - remove-ads purchase
 * - enterprise users
 * - special user segments
 */
interface AdsUserPolicy {

    val state: StateFlow<AdsUserAdState>

    fun canShowAds(): Boolean {
        return state.value.canShowAds
    }
}

/**
 * Default implementation for development/testing.
 *
 * Real app module should provide its own implementation backed by:
 * - DataStore
 * - RevenueCat
 * - Google Play Billing
 * - Firebase
 * - backend entitlement API
 */
class DefaultAdsUserPolicy : AdsUserPolicy {

    private val mutableState = MutableStateFlow(AdsUserAdState())

    override val state: StateFlow<AdsUserAdState> = mutableState.asStateFlow()

    fun update(state: AdsUserAdState) {
        mutableState.value = state
    }
}