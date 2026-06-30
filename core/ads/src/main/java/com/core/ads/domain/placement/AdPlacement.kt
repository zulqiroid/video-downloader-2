package com.core.ads.domain.placement

/**
 * AdPlacement represents a logical location where an ad can be requested.
 *
 * Important:
 * - This is NOT the AdMob ad unit ID.
 * - This is your app-level placement key.
 * - Remote Config should use the same placement names.
 *
 * Example:
 * home_interstitial
 * result_interstitial
 * home_banner
 * language_native_small
 * reward_unlock_feature
 */
@JvmInline
value class AdPlacement(val value: String) {

    init {
        require(value.isNotBlank()) {
            "AdPlacement value cannot be blank."
        }
    }

    override fun toString(): String = value

    companion object {
        /**
         * AppOpen is usually app-level, not screen-level.
         */
        val APP_OPEN = AdPlacement("app_open")

        /**
         * Default placements are useful when the app has only one ad unit ID
         * for the whole app and does not care about screen-specific placement.
         */
        val DEFAULT_INTERSTITIAL = AdPlacement("default_interstitial")
        val DEFAULT_REWARDED = AdPlacement("default_rewarded")
        val DEFAULT_BANNER = AdPlacement("default_banner")
        val DEFAULT_NATIVE = AdPlacement("default_native")

        /**
         * Example app-specific placements.
         * You can add more in app module later.
         */
        val HOME_INTERSTITIAL = AdPlacement("home_interstitial")
        val RESULT_INTERSTITIAL = AdPlacement("result_interstitial")
        val EXIT_INTERSTITIAL = AdPlacement("exit_interstitial")

        val HOME_BANNER = AdPlacement("home")
        val EXPLORE_BANNER = AdPlacement("explore")
        val CREATE_BANNER = AdPlacement("create")
        val MORE_BANNER = AdPlacement("more")
        val APP_LANGUAGE_BANNER = AdPlacement("app_language")
        val ONBOARDING_BANNER = AdPlacement("onboarding")
        val RESULT_BANNER = AdPlacement("result_banner")

        val LANGUAGE_NATIVE_SMALL = AdPlacement("language_native_small")
        val HOME_NATIVE_LARGE = AdPlacement("home_native_large")

        val REWARD_UNLOCK_FEATURE = AdPlacement("reward_unlock_feature")
    }
}