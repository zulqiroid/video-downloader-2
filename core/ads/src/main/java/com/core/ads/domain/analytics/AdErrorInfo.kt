package com.core.ads.domain.analytics

/**
 * SDK error info mapped into a clean domain model.
 *
 * Google LoadAdError / AdError should be converted into this model
 * inside data layer. Domain layer should not depend on Google SDK classes.
 */
data class AdErrorInfo(
    val code: Int? = null,
    val domain: String? = null,
    val message: String? = null,
    val cause: String? = null
)