package com.core.ads.domain.loading

data class AdLoadingUiState(
    val isVisible: Boolean = false,
    val title: String = "Loading Ad",
    val message: String = "Please wait while we prepare your ad",
    val durationMs: Long = 800L
)