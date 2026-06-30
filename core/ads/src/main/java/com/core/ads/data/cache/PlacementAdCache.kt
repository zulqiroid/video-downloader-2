package com.core.ads.data.cache

import com.core.ads.domain.placement.AdPlacement

/**
 * Stores loaded ads by placement.
 *
 * Important:
 * - Cache key is placement, not ad unit ID.
 * - Even if multiple placements fallback to the same global ad unit ID,
 *   they still need separate state and analytics.
 */
class PlacementAdCache<T>(
    private val onDestroyAd: (T) -> Unit = {}
) {

    private val cache = mutableMapOf<String, CachedAd<T>>()

    fun get(
        placement: AdPlacement
    ): CachedAd<T>? {
        return cache[placement.value]
    }

    fun put(
        placement: AdPlacement,
        cachedAd: CachedAd<T>
    ) {
        remove(placement)
        cache[placement.value] = cachedAd
    }

    fun hasFreshAd(
        placement: AdPlacement,
        maxAgeMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        return getFreshAd(
            placement = placement,
            maxAgeMillis = maxAgeMillis,
            nowMillis = nowMillis
        ) != null
    }

    fun getFreshAd(
        placement: AdPlacement,
        maxAgeMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): CachedAd<T>? {
        val cached = cache[placement.value] ?: return null

        return if (cached.isFresh(nowMillis, maxAgeMillis)) {
            cached
        } else {
            remove(placement)
            null
        }
    }

    fun remove(
        placement: AdPlacement
    ) {
        val cached = cache.remove(placement.value)
        cached?.ad?.let { ad ->
            runCatching { onDestroyAd(ad) }
        }
    }

    fun clear() {
        val values = cache.values.toList()
        cache.clear()

        values.forEach { cached ->
            runCatching { onDestroyAd(cached.ad) }
        }
    }

    fun contains(
        placement: AdPlacement
    ): Boolean {
        return cache.containsKey(placement.value)
    }

    fun take(
        placement: AdPlacement
    ): CachedAd<T>? {
        return cache.remove(placement.value)
    }

    fun takeFreshAd(
        placement: AdPlacement,
        maxAgeMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): CachedAd<T>? {
        val cached = cache[placement.value] ?: return null

        return if (cached.isFresh(nowMillis, maxAgeMillis)) {
            cache.remove(placement.value)
        } else {
            remove(placement)
            null
        }
    }

    fun keys(): Set<String> {
        return cache.keys
    }
}