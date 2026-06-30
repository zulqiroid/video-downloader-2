package com.core.ads.domain.appopen

/**
 * AppOpenAdShowResult batata hai ke show request ka final result kya tha.
 *
 * Iska benefit:
 * App ko sirf ye nahi pata chalega ke callback complete ho gaya.
 * App ko reason bhi mil sakta hai ke ad show hui, skip hui ya fail hui.
 */
sealed interface AppOpenAdShowResult {

    /**
     * Ad successfully show hui.
     */
    data object Shown : AppOpenAdShowResult

    /**
     * Ad available nahi thi, isliye show nahi hui.
     */
    data object NotAvailable : AppOpenAdShowResult

    /**
     * Ad intentionally skip hui.
     *
     * Example:
     * - ads disabled
     * - user premium
     * - consent allowed nahi
     * - cooldown active
     */
    data class Skipped(
        val reason: String
    ) : AppOpenAdShowResult

    /**
     * Ad show karne ki try hui, lekin fail ho gayi.
     */
    data class Failed(
        val reason: String
    ) : AppOpenAdShowResult
}