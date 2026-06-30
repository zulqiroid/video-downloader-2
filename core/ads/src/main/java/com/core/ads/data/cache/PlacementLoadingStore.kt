package com.core.ads.data.cache

import com.core.ads.domain.placement.AdPlacement

/**
 * Tracks in-flight loading by placement.
 *
 * This prevents duplicate load requests for the same placement while still
 * allowing another placement to load independently.
 */
class PlacementLoadingStore {

    private val loadingPlacements = mutableSetOf<String>()

    fun isLoading(
        placement: AdPlacement
    ): Boolean {
        return loadingPlacements.contains(placement.value)
    }

    fun markLoading(
        placement: AdPlacement
    ) {
        loadingPlacements.add(placement.value)
    }

    fun markNotLoading(
        placement: AdPlacement
    ) {
        loadingPlacements.remove(placement.value)
    }

    fun clear() {
        loadingPlacements.clear()
    }
}