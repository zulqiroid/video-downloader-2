package com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium

data class PremiumEntitlement(
    val isPremiumUser: Boolean = false,
    val hasLifetimePurchase: Boolean = false,
    val productId: String? = null
)