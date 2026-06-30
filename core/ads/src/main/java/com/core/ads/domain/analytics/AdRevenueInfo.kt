package com.core.ads.domain.analytics

/**
 * Represents ad revenue from paid event callback.
 *
 * In Google Mobile Ads, revenue is usually reported in micros.
 * Example:
 * 1 USD = 1_000_000 micros.
 */
data class AdRevenueInfo(
    val valueMicros: Long,
    val currencyCode: String,
    val precisionType: Int? = null
)