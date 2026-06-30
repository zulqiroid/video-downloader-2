package com.core.ads.data.cache

import com.core.ads.domain.placement.AdPlacement

/**
 * Tracks last show time for:
 * - one specific placement
 * - the whole format globally
 *
 * Example:
 * Interstitial can enforce:
 * - global cooldown between any two interstitials
 * - placement cooldown for the same placement
 */
class AdShowTimestampStore {

    private var lastGlobalShownAtMillis: Long = 0L

    private val lastShownByPlacement = mutableMapOf<String, Long>()

    fun markShown(
        placement: AdPlacement,
        shownAtMillis: Long = System.currentTimeMillis()
    ) {
        lastGlobalShownAtMillis = shownAtMillis
        lastShownByPlacement[placement.value] = shownAtMillis
    }

    fun getPlacementRemainingCooldownMillis(
        placement: AdPlacement,
        minIntervalMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val lastShownAt = lastShownByPlacement[placement.value] ?: return 0L
        return calculateRemaining(
            lastShownAtMillis = lastShownAt,
            minIntervalMillis = minIntervalMillis,
            nowMillis = nowMillis
        )
    }

    fun getGlobalRemainingCooldownMillis(
        minIntervalMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        if (lastGlobalShownAtMillis == 0L) return 0L

        return calculateRemaining(
            lastShownAtMillis = lastGlobalShownAtMillis,
            minIntervalMillis = minIntervalMillis,
            nowMillis = nowMillis
        )
    }

    fun getEffectiveRemainingCooldownMillis(
        placement: AdPlacement,
        placementMinIntervalMillis: Long,
        globalMinIntervalMillis: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val placementRemaining = getPlacementRemainingCooldownMillis(
            placement = placement,
            minIntervalMillis = placementMinIntervalMillis,
            nowMillis = nowMillis
        )

        val globalRemaining = getGlobalRemainingCooldownMillis(
            minIntervalMillis = globalMinIntervalMillis,
            nowMillis = nowMillis
        )

        return maxOf(placementRemaining, globalRemaining)
    }

    fun clear(
        placement: AdPlacement
    ) {
        lastShownByPlacement.remove(placement.value)
    }

    fun clearAll() {
        lastGlobalShownAtMillis = 0L
        lastShownByPlacement.clear()
    }

    private fun calculateRemaining(
        lastShownAtMillis: Long,
        minIntervalMillis: Long,
        nowMillis: Long
    ): Long {
        if (minIntervalMillis <= 0L) return 0L

        val elapsed = nowMillis - lastShownAtMillis
        val remaining = minIntervalMillis - elapsed

        return if (remaining > 0L) remaining else 0L
    }
}