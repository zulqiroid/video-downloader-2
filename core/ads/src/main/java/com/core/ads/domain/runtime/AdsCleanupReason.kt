package com.core.ads.domain.runtime

enum class AdsCleanupReason {
    ADS_DISABLED,
    REQUESTS_DISABLED,
    USER_POLICY_BLOCKED,
    USER_BECAME_PREMIUM,
    REMOVE_ADS_PURCHASED,
    CONSENT_REVOKED,
    CONFIG_CHANGED,
    SDK_BLOCKED,
    SDK_FAILED,
    MANUAL,
    UNKNOWN
}