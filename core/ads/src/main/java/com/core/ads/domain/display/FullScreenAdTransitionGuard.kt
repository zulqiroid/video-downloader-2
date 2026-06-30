package com.core.ads.domain.display

interface FullScreenAdTransitionGuard {

    fun suppressAppOpenFor(
        durationMillis: Long,
        reason: String
    )

    fun isAppOpenSuppressed(): Boolean

    fun getRemainingSuppressionMillis(): Long

    fun clear()
}