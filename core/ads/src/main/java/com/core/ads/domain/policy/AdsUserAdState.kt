package com.core.ads.domain.policy

/**
 * Represents user's ad eligibility.
 *
 * This is important for premium/remove-ads users.
 */
data class AdsUserAdState(
    val canShowAds: Boolean = true,

    /**
     * true when user has premium subscription.
     */
    val isPremiumUser: Boolean = false,

    /**
     * true when user has one-time remove-ads purchase.
     */
    val hasRemoveAdsPurchase: Boolean = false,

    /**
     * Optional debug reason.
     */
    val reason: String? = null
)