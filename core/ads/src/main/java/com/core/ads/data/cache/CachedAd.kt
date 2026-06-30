package com.core.ads.data.cache

import com.core.ads.domain.analytics.AdUnitSource

/**
 * Runtime cache wrapper for a loaded ad object.
 *
 * T is intentionally generic:
 * - AppOpenAd
 * - InterstitialAd
 * - RewardedAd
 * - NativeAd
 * - AdView if needed
 *
 * Domain layer must not know about Google SDK classes.
 */
data class CachedAd<T>(
    val ad: T,
    val adUnitId: String,
    val adUnitSource: AdUnitSource,
    val loadedAtMillis: Long = System.currentTimeMillis()
) {

    fun isFresh(
        nowMillis: Long = System.currentTimeMillis(),
        maxAgeMillis: Long
    ): Boolean {
        return nowMillis - loadedAtMillis < maxAgeMillis
    }
}