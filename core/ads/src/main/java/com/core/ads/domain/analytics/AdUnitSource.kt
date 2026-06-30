package com.core.ads.domain.analytics

/**
 * Tells analytics whether the ad unit ID came from:
 * - global/parent config
 * - placement-specific override config
 *
 * This is important for debugging Remote Config and AdMob performance.
 */
enum class AdUnitSource {
    GLOBAL,
    PLACEMENT
}