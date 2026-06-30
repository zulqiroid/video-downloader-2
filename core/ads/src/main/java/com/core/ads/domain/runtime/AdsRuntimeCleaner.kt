package com.core.ads.domain.runtime

interface AdsRuntimeCleaner {

    fun clearAll(
        reason: AdsCleanupReason,
        message: String? = null
    )
}