package com.core.ads.domain.policy

data class AdsConsentState(
    val canRequestAds: Boolean = false,
    val canRequestPersonalizedAds: Boolean = false
)