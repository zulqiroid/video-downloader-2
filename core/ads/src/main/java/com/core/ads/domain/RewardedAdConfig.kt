package com.core.ads.domain

import com.core.ads.domain.placement.AdPlacement
import kotlin.text.get

data class RewardedAdConfig(
    val enabled: Boolean = false,

    /**
     * Global/fallback rewarded ID.
     */
    val adUnitId: String? = null,

    val globalMinIntervalBetweenShowsMillis: Long =
        DEFAULT_MIN_INTERVAL_BETWEEN_SHOWS_MILLIS,

    val maxAdAgeMillis: Long = DEFAULT_MAX_AD_AGE_MILLIS,

    val minIntervalBetweenShowsMillis: Long =
        DEFAULT_MIN_INTERVAL_BETWEEN_SHOWS_MILLIS,

    val retryConfig: CommonAdRetryConfig = CommonAdRetryConfig(),

    val placements: Map<String, RewardedPlacementConfig> = emptyMap()
) {

    fun resolve(placement: AdPlacement): ResolvedRewardedAdConfig? {
        if (!enabled) return null

        val placementConfig = placements[placement.value]

        if (placementConfig?.enabled == false) return null

        val resolvedAdUnitId = placementConfig
            ?.adUnitId
            ?.takeIf { it.isNotBlank() }
            ?: adUnitId?.takeIf { it.isNotBlank() }
            ?: return null

        return ResolvedRewardedAdConfig(
            placement = placement,
            adUnitId = resolvedAdUnitId,
            maxAdAgeMillis = placementConfig?.maxAdAgeMillis ?: maxAdAgeMillis,
            minIntervalBetweenShowsMillis = placementConfig?.minIntervalBetweenShowsMillis
                ?: minIntervalBetweenShowsMillis,
            globalMinIntervalBetweenShowsMillis = globalMinIntervalBetweenShowsMillis,
            rewardType = placementConfig?.rewardType,
            rewardAmount = placementConfig?.rewardAmount,
            isUsingPlacementAdUnitId = !placementConfig?.adUnitId.isNullOrBlank()
        )
    }

    fun getPlacementConfig(
        placementKey: String
    ): RewardedPlacementConfig {
        return placements[placementKey] ?: RewardedPlacementConfig()
    }

    fun isPlacementUsable(placement: AdPlacement): Boolean {
        return resolve(placement) != null
    }

    companion object {
        private const val ONE_MINUTE_MILLIS = 60 * 1000L
        private const val ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS

        const val DEFAULT_MAX_AD_AGE_MILLIS: Long = 4 * ONE_HOUR_MILLIS
        const val DEFAULT_MIN_INTERVAL_BETWEEN_SHOWS_MILLIS: Long = ONE_MINUTE_MILLIS
    }
}

data class RewardedPlacementConfig(
    val enabled: Boolean? = null,
    val adUnitId: String? = null,
    val maxAdAgeMillis: Long? = null,
    val minIntervalBetweenShowsMillis: Long? = null,

    /**
     * Metadata from Remote Config.
     * Actual reward is still granted only from Google SDK callback.
     */
    val rewardType: String? = null,
    val rewardAmount: Int? = null
)

data class ResolvedRewardedAdConfig(
    val placement: AdPlacement,
    val adUnitId: String,
    val maxAdAgeMillis: Long,
    val minIntervalBetweenShowsMillis: Long,
    val globalMinIntervalBetweenShowsMillis: Long,
    val rewardType: String?,
    val rewardAmount: Int?,
    val isUsingPlacementAdUnitId: Boolean
)