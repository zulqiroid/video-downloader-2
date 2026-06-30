package com.core.ads.domain.policy

import android.app.Activity
import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement

/**
 * Runtime context needed to evaluate whether an ad can load/show.
 *
 * Controllers will pass these values.
 * Policy evaluator decides allowed/blocked.
 */
data class AdsPolicyContext(
    val operation: AdsOperation,
    val format: AdFormat,
    val placement: AdPlacement,

    /**
     * Required for SHOW operations.
     * Usually not required for LOAD operations.
     */
    val activity: Activity? = null,

    /**
     * Controller state flags.
     */
    val isLoading: Boolean = false,
    val isShowing: Boolean = false,
    val hasFreshCache: Boolean = false,

    /**
     * Cooldown remaining for this placement or global format.
     * If > 0, show should be blocked.
     */
    val cooldownRemainingMillis: Long = 0L,

    /**
     * Useful for AppOpen/interstitial/rewarded.
     */
    val isAppInForeground: Boolean = true,

    /**
     * Later this can be connected to SDK initialization state.
     */
//    val isSdkInitialized: Boolean = true
)