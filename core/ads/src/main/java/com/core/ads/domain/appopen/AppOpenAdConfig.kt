package com.core.ads.domain.appopen

import com.core.ads.domain.CommonAdRetryConfig

data class AppOpenAdConfig(
    val enabled: Boolean = false,

    /**
     * Normal app-open ad ID used for resume/background-to-foreground.
     */
    val adUnitId: String? = null,

    /**
     * Optional special AppOpen ID for splash.
     */
    val splashAdUnitId: String? = null,

    /**
     * Whether AppOpen can show from splash manually.
     */
    val showOnSplash: Boolean = true,

    /**
     * Whether AppOpen can show on background → foreground.
     */
    val showOnAppForeground: Boolean = true,

    /**
     * Old-compatible name.
     * For lifecycle cold start auto-show.
     */
    val showOnColdStart: Boolean = false,

    /**
     * New clearer name.
     */
    val showAutomaticallyOnColdStart: Boolean = false,

    val foregroundShowDelayMillis: Long = 500L,
    val minBackgroundDurationBeforeShowMillis: Long = 3_000L,

    val maxAdAgeMillis: Long = 4 * 60 * 60 * 1_000L,
    val minIntervalBetweenShowsMillis: Long = 0L,

    val retryConfig: CommonAdRetryConfig = CommonAdRetryConfig()
) {
    val isUsable: Boolean
        get() = enabled && (
                !adUnitId.isNullOrBlank() ||
                        !splashAdUnitId.isNullOrBlank()
                )
}