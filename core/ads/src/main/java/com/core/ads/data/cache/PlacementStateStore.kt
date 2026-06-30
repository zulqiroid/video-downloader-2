package com.core.ads.data.cache

import com.core.ads.domain.placement.AdPlacement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Generic state holder for placement-based controllers.
 *
 * Example:
 * PlacementStateStore { InterstitialAdState() }
 * PlacementStateStore { RewardedAdState() }
 * PlacementStateStore { BannerAdState() }
 * PlacementStateStore { NativeAdState() }
 */
class PlacementStateStore<S>(
    private val defaultStateFactory: () -> S
) {

    private val mutableStates = MutableStateFlow<Map<String, S>>(emptyMap())

    val states: StateFlow<Map<String, S>> = mutableStates.asStateFlow()

    fun get(
        placement: AdPlacement
    ): S {
        return mutableStates.value[placement.value] ?: defaultStateFactory()
    }

    fun update(
        placement: AdPlacement,
        reducer: (S) -> S
    ) {
        mutableStates.update { current ->
            val oldState = current[placement.value] ?: defaultStateFactory()
            current + (placement.value to reducer(oldState))
        }
    }

    fun set(
        placement: AdPlacement,
        state: S
    ) {
        mutableStates.update { current ->
            current + (placement.value to state)
        }
    }

    fun remove(
        placement: AdPlacement
    ) {
        mutableStates.update { current ->
            current - placement.value
        }
    }

    fun reset(
        placement: AdPlacement
    ) {
        set(placement, defaultStateFactory())
    }

    fun clear() {
        mutableStates.value = emptyMap()
    }
}