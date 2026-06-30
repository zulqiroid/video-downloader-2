package com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium

data class PremiumProduct(
    val type: PremiumPlanType,
    val productId: String,
    val title: String,
    val price: String,
    val periodLabel: String,
    val isSubscription: Boolean
)