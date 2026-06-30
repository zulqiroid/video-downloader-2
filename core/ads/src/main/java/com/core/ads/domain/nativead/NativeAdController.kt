package com.core.ads.domain.nativead

import android.app.Activity
import com.core.ads.domain.placement.AdPlacement
import kotlinx.coroutines.flow.StateFlow

interface NativeAdController {

    val states: StateFlow<Map<String, NativeAdState>>

    fun getState(
        placement: AdPlacement = AdPlacement.DEFAULT_NATIVE
    ): NativeAdState {
        return states.value[placement.value] ?: NativeAdState()
    }

    fun loadAd(
        activity: Activity,
        placement: AdPlacement = AdPlacement.DEFAULT_NATIVE,
        onLoaded: (NativeAdBinding) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Cancels only pending UI callback for this placement.
     *
     * It does not cancel Google SDK request because SDK load cannot always be cancelled.
     * If ad loads after cancel, controller can cache it safely.
     */
    fun cancel(
        placement: AdPlacement = AdPlacement.DEFAULT_NATIVE
    )

    fun clear(
        placement: AdPlacement = AdPlacement.DEFAULT_NATIVE
    )

    fun clearAll()
}