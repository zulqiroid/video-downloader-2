package com.core.ads.data.cache

import com.core.ads.domain.placement.AdPlacement

/**
 * Tracks whether an ad is currently showing.
 *
 * It supports:
 * - per-placement showing check
 * - global showing check across the same format
 */
class PlacementShowingStore {

    private val showingPlacements = mutableSetOf<String>()

    fun isShowing(
        placement: AdPlacement
    ): Boolean {
        return showingPlacements.contains(placement.value)
    }

    fun isAnyShowing(): Boolean {
        return showingPlacements.isNotEmpty()
    }

    fun markShowing(
        placement: AdPlacement
    ) {
        showingPlacements.add(placement.value)
    }

    fun markNotShowing(
        placement: AdPlacement
    ) {
        showingPlacements.remove(placement.value)
    }

    fun clear() {
        showingPlacements.clear()
    }
}