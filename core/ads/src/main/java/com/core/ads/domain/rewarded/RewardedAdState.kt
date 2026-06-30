package com.core.ads.domain.rewarded

data class RewardedAdState(
    val isLoading: Boolean = false,
    val isAvailable: Boolean = false,
    val isShowing: Boolean = false,
    val lastLoadedAtMillis: Long? = null,
    val lastShownAtMillis: Long? = null,
    val lastErrorMessage: String? = null
)