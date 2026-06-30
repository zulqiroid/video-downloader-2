package com.core.ads.domain.analytics

/**
 * Structured block reasons.
 *
 * Never rely only on random strings for analytics.
 * Strings are okay for debug messages, but reports need stable reason codes.
 */
enum class AdBlockReason {
    ADS_DISABLED,
    REQUESTS_DISABLED,
    FORMAT_DISABLED,
    PLACEMENT_DISABLED,
    AD_UNIT_ID_MISSING,
    USER_POLICY_BLOCKED,
    CONSENT_NOT_ALLOWED,
    SDK_NOT_INITIALIZED,
    ACTIVITY_INVALID,
    ACTIVITY_FINISHING,
    ACTIVITY_DESTROYED,
    ALREADY_LOADING,
    ALREADY_SHOWING,
    CACHE_AVAILABLE,
    CACHE_NOT_AVAILABLE,
    DISPLAY_GUARD_BLOCKED,
    CACHE_EXPIRED,
    COOLDOWN_ACTIVE,
    FREQUENCY_CAP_ACTIVE,
    NETWORK_UNAVAILABLE,
    APP_IN_BACKGROUND,
    INTERNAL_ERROR,
    UNKNOWN
}