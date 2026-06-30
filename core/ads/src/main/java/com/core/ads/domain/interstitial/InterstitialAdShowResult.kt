package com.core.ads.domain.interstitial

/**
 * InterstitialAdShowResult show request ke final outcomes ko represent karta hai.
 */
sealed interface InterstitialAdShowResult {

    /** Ad successfully show hui aur user ne dismiss kiya. */
    data object Shown : InterstitialAdShowResult

    /** Cached ad available nahi thi. */
    data object NotAvailable : InterstitialAdShowResult

    /** Ad intentionally skip hui (policy, cooldown). */
    data class Skipped(val reason: String) : InterstitialAdShowResult

    /** Ad show attempt fail ho gayi. */
    data class Failed(val reason: String) : InterstitialAdShowResult
}