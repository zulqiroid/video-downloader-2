package com.core.ads.domain.analytics

/**
 * Describes what triggered the ad request.
 *
 * This helps you understand user behavior and automated preload behavior.
 */
enum class AdRequestSource {
    APP_START,
    APP_FOREGROUND,
    SCREEN_ENTER,
    USER_ACTION,
    AUTO_PRELOAD,
    MANUAL_PRELOAD,
    RETRY,
    UNKNOWN
}