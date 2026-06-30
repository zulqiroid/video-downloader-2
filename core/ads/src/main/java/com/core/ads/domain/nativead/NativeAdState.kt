package com.core.ads.domain.nativead

data class NativeAdState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null
)