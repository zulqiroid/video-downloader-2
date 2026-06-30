package com.core.ads.domain.policy

/**
 * AdsPolicyResult ek clean result model hai jo batata hai
 * ke ad operation allowed hai ya blocked.
 *
 * Iska benefit:
 * Manager blindly false return nahi karega.
 * Hume reason bhi milega ke ad kyun block hui.
 *
 * Example reasons:
 * - ads globally disabled
 * - user premium hai
 * - consent allowed nahi
 * - ad unit id missing hai
 */
sealed interface AdsPolicyResult {

    /**
     * Ad operation allowed hai.
     */
    data object Allowed : AdsPolicyResult

    /**
     * Ad operation blocked hai.
     *
     * reason debug logs aur analytics ke liye useful hoga.
     */
    data class Blocked(
        val reason: String
    ) : AdsPolicyResult
}