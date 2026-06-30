package com.core.ads.domain.rewarded

/**
 * RewardItem represent karta hai ke rewarded ad se user ko kya reward mila.
 */
data class RewardItem(
    val type: String,
    val amount: Int
)