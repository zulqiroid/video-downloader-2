package com.core.ads.domain.rewarded

import android.app.Activity
import com.core.ads.domain.placement.AdPlacement
import kotlinx.coroutines.flow.StateFlow

/**
 * RewardedAdController is placement-based.
 *
 * Example placements:
 * - reward_unlock_feature
 * - reward_extra_generation
 * - reward_save_image
 */
interface RewardedAdController {

    /**
     * State of all rewarded placements.
     *
     * Key = placement.value
     */
    val states: StateFlow<Map<String, RewardedAdState>>

    fun getState(
        placement: AdPlacement = AdPlacement.DEFAULT_REWARDED
    ): RewardedAdState {
        return states.value[placement.value] ?: RewardedAdState()
    }

    /**
     * Preload rewarded ad for one placement.
     */
    fun preload(
        placement: AdPlacement = AdPlacement.DEFAULT_REWARDED
    )

    /**
     * Show rewarded ad for one placement.
     *
     * Important:
     * onRewardEarned should only be called when Google SDK confirms reward.
     */
    fun showIfAvailable(
        activity: Activity,
        placement: AdPlacement = AdPlacement.DEFAULT_REWARDED,
        onComplete: (RewardedAdShowResult) -> Unit = {},
        onRewardEarned: (RewardItem) -> Unit = {}
    )

    /**
     * Clear one rewarded placement.
     */
    fun clear(
        placement: AdPlacement = AdPlacement.DEFAULT_REWARDED
    )

    /**
     * Clear all rewarded ads.
     */
    fun clearAll()
}