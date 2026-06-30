package com.core.ads.domain.banner

data class BannerAdState(
    /**
     * true jab ad load ho rahi ho.
     */
    val isLoading: Boolean = false,

    /**
     * true jab ad successfully load ho chuki ho aur display ke liye ready ho.
     */
    val isLoaded: Boolean = false,

    /**
     * Error message agar load fail ho jaye.
     */
    val errorMessage: String? = null
)