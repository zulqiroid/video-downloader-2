package com.core.ads.domain

data class CommonAdRetryConfig(
    val maxLoadRetryCount: Int = 3,
    val initialRetryDelayMillis: Long = 2_000L,
    val maxRetryDelayMillis: Long = 60_000L
)