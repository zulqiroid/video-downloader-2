package com.core.ads.domain.interstitial

import android.app.Activity
import com.core.ads.domain.placement.AdPlacement
import kotlinx.coroutines.flow.StateFlow

/**
 * InterstitialAdController is placement-based.
 *
 * Why placement-based?
 * - Different screens may have different ad unit IDs.
 * - If a screen does not provide its own ID, it can fallback to parent/global ID.
 * - Analytics needs to know where the ad was requested/shown.
 *
 * Example:
 * preload(AdPlacement.HOME_INTERSTITIAL)
 * showIfAvailable(activity, AdPlacement.RESULT_INTERSTITIAL)
 */
interface InterstitialAdController {

    /**
     * State of all interstitial placements.
     *
     * Key = placement.value
     */
    val states: StateFlow<Map<String, InterstitialAdState>>

    /**
     * Convenience method to read one placement state immediately.
     */
    fun getState(
        placement: AdPlacement = AdPlacement.DEFAULT_INTERSTITIAL
    ): InterstitialAdState {
        return states.value[placement.value] ?: InterstitialAdState()
    }

    /**
     * Preload interstitial for a specific placement.
     *
     * If placement has its own adUnitId, controller will use that.
     * If placement does not have its own adUnitId, controller will fallback
     * to the parent/global interstitial adUnitId.
     */
    fun preload(
        placement: AdPlacement = AdPlacement.DEFAULT_INTERSTITIAL
    )

    /**
     * Show interstitial for a specific placement if available.
     *
     * onComplete must always be called:
     * - shown and dismissed
     * - not available
     * - skipped
     * - failed
     */
    fun showIfAvailable(
        activity: Activity,
        placement: AdPlacement = AdPlacement.DEFAULT_INTERSTITIAL,
        onComplete: (InterstitialAdShowResult) -> Unit = {}
    )

    /**
     * Clear one placement cache/state.
     */
    fun clear(
        placement: AdPlacement = AdPlacement.DEFAULT_INTERSTITIAL
    )

    /**
     * Clear all placement caches/states.
     *
     * Use cases:
     * - user becomes premium
     * - ads disabled from Remote Config
     * - consent revoked
     * - debug reset
     */
    fun clearAll()
}