package com.core.ads.domain.rewarded

sealed interface RewardedAdShowResult {
    data object Shown : RewardedAdShowResult
    data object NotAvailable : RewardedAdShowResult
    data class Skipped(val reason: String) : RewardedAdShowResult
    data class Failed(val reason: String) : RewardedAdShowResult
}