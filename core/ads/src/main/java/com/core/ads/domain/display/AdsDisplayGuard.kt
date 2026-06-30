package com.core.ads.domain.display

import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement
import kotlinx.coroutines.flow.StateFlow

/**
 * Global full-screen ad display guard.
 *
 * Purpose:
 * - Prevent AppOpen, Interstitial, and Rewarded ads from showing at the same time.
 * - Prevent accidental double show caused by fast clicks, lifecycle events, or callbacks.
 *
 * Banner and Native ads do not need this because they are not full-screen.
 */
interface AdsDisplayGuard {

    val state: StateFlow<AdsDisplayState>

    /**
     * Returns true if a full-screen ad can start showing.
     */
    fun canShow(
        format: AdFormat,
        placement: AdPlacement
    ): Boolean

    /**
     * Mark a full-screen ad as showing.
     *
     * Returns false if another full-screen ad is already showing.
     */
    fun markShowing(
        format: AdFormat,
        placement: AdPlacement
    ): Boolean

    /**
     * Mark the current full-screen ad as finished.
     */
    fun markFinished(
        format: AdFormat,
        placement: AdPlacement
    )

    /**
     * Force clear display state.
     *
     * Use cases:
     * - premium user activated
     * - ads disabled
     * - consent revoked
     * - fatal callback failure
     * - debug reset
     */
    fun clear()
}