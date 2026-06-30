package com.core.ads.domain

import com.core.ads.domain.loading.AdLoadingUiState

data class AdLoadingDialogConfig(
    val enabled: Boolean = false,

    val showForAppOpen: Boolean = false,
    val showForInterstitial: Boolean = true,
    val showForRewarded: Boolean = true,

    /**
     * Backward-compatible default duration.
     * If ad-type specific duration is missing, this duration will be used.
     */
    val durationMs: Long = 800L,

    val appOpenDurationMs: Long? = null,
    val interstitialDurationMs: Long? = null,
    val rewardedDurationMs: Long? = null,

    val title: String = "Loading Ad",
    val message: String = "Please wait while we prepare your ad",

    val appOpenTitle: String? = null,
    val appOpenMessage: String? = null,

    val interstitialTitle: String? = null,
    val interstitialMessage: String? = null,

    val rewardedTitle: String? = null,
    val rewardedMessage: String? = null
) {

    val safeDurationMs: Long
        get() = durationMs.safeLoadingDuration()

    val safeAppOpenDurationMs: Long
        get() = (appOpenDurationMs ?: durationMs).safeLoadingDuration()

    val safeInterstitialDurationMs: Long
        get() = (interstitialDurationMs ?: durationMs).safeLoadingDuration()

    val safeRewardedDurationMs: Long
        get() = (rewardedDurationMs ?: durationMs).safeLoadingDuration()

    fun appOpenUiStateOrNull(): AdLoadingUiState? {
        if (!enabled || !showForAppOpen || safeAppOpenDurationMs <= 0L) return null

        return AdLoadingUiState(
            isVisible = true,
            title = appOpenTitle.cleanOrDefault(title),
            message = appOpenMessage.cleanOrDefault(message),
            durationMs = safeAppOpenDurationMs
        )
    }

    fun interstitialUiStateOrNull(): AdLoadingUiState? {
        if (!enabled || !showForInterstitial || safeInterstitialDurationMs <= 0L) return null

        return AdLoadingUiState(
            isVisible = true,
            title = interstitialTitle.cleanOrDefault(title),
            message = interstitialMessage.cleanOrDefault(message),
            durationMs = safeInterstitialDurationMs
        )
    }

    fun rewardedUiStateOrNull(): AdLoadingUiState? {
        if (!enabled || !showForRewarded || safeRewardedDurationMs <= 0L) return null

        return AdLoadingUiState(
            isVisible = true,
            title = rewardedTitle.cleanOrDefault(title),
            message = rewardedMessage.cleanOrDefault(message),
            durationMs = safeRewardedDurationMs
        )
    }

    private fun Long.safeLoadingDuration(): Long {
        return coerceIn(
            minimumValue = 0L,
            maximumValue = MAX_LOADING_DURATION_MS
        )
    }

    private fun String?.cleanOrDefault(defaultValue: String): String {
        return this
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: defaultValue
    }

    private companion object {
        private const val MAX_LOADING_DURATION_MS = 3_000L
    }
}