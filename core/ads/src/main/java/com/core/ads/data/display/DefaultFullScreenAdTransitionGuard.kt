package com.core.ads.data.display

import com.core.ads.domain.display.FullScreenAdTransitionGuard
import com.core.ads.utils.AdsLogger

class DefaultFullScreenAdTransitionGuard : FullScreenAdTransitionGuard {

    private var suppressAppOpenUntilMillis: Long = 0L
    private var lastReason: String? = null

    override fun suppressAppOpenFor(
        durationMillis: Long,
        reason: String
    ) {
        val now = System.currentTimeMillis()
        val newUntil = now + durationMillis.coerceAtLeast(0L)

        if (newUntil > suppressAppOpenUntilMillis) {
            suppressAppOpenUntilMillis = newUntil
            lastReason = reason

            AdsLogger.d(
                "AppOpen suppressed for ${durationMillis}ms. reason=$reason"
            )
        }
    }

    override fun isAppOpenSuppressed(): Boolean {
        return getRemainingSuppressionMillis() > 0L
    }

    override fun getRemainingSuppressionMillis(): Long {
        val remaining = suppressAppOpenUntilMillis - System.currentTimeMillis()
        return remaining.coerceAtLeast(0L)
    }

    override fun clear() {
        suppressAppOpenUntilMillis = 0L
        lastReason = null
    }
}