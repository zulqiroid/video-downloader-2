package com.core.ads.domain.banner

import android.app.Activity
import android.view.ViewGroup
import com.core.ads.domain.placement.AdPlacement
import kotlinx.coroutines.flow.StateFlow

interface BannerAdController {

    val states: StateFlow<Map<String, BannerAdState>>

    fun getState(
        placement: AdPlacement = AdPlacement.DEFAULT_BANNER
    ): BannerAdState {
        return states.value[placement.value] ?: BannerAdState()
    }

    fun loadAndAttach(
        activity: Activity,
        container: ViewGroup,
        placement: AdPlacement = AdPlacement.DEFAULT_BANNER,
        adWidthDp: Int
    )

    /**
     * Detach only if this exact container is currently attached.
     *
     * This prevents old Compose onDispose from detaching a newly attached banner.
     */
    fun detach(
        placement: AdPlacement = AdPlacement.DEFAULT_BANNER,
        container: ViewGroup? = null
    )

    fun resume(
        placement: AdPlacement = AdPlacement.DEFAULT_BANNER
    )

    fun pause(
        placement: AdPlacement = AdPlacement.DEFAULT_BANNER
    )

    fun destroy(
        placement: AdPlacement = AdPlacement.DEFAULT_BANNER
    )

    fun destroyAll()
}