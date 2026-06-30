package com.core.ads.data.consent

import android.app.Application
import com.core.ads.domain.policy.AdsConsentPolicy
import com.core.ads.domain.policy.AdsConsentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UmpConsentPolicy(
    private val application: Application
) : AdsConsentPolicy {

    private val mutableState = MutableStateFlow(AdsConsentState())

    override val state: StateFlow<AdsConsentState> = mutableState.asStateFlow()

    fun update(
        canRequestAds: Boolean,
        canRequestPersonalizedAds: Boolean = canRequestAds
    ) {
        mutableState.value = AdsConsentState(
            canRequestAds = canRequestAds,
            canRequestPersonalizedAds = canRequestPersonalizedAds
        )
    }
}