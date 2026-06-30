package com.core.ads.domain.interstitial

/**
 * InterstitialAdState Interstitial ad ki current UI/business state represent karta hai.
 * Immutable hai, MVI pattern ke mutabik.
 */
data class InterstitialAdState(
    val isLoading: Boolean = false,
    val isAvailable: Boolean = false,
    val isShowing: Boolean = false,
    val lastLoadedAtMillis: Long? = null,
    val lastShownAtMillis: Long? = null,
    val lastErrorMessage: String? = null
)