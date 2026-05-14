package com.video.downloader.presentation.screens.onboarding.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.video.downloader.R

data class OnboardingPage(
    @DrawableRes val image: Int?,
    @StringRes val title: Int?,
    @StringRes val description: Int?,
    val isLast: Boolean = false
)

fun defaultOnboardingPages(): List<OnboardingPage> = listOf(

    OnboardingPage(
        image = R.drawable.onboarding_img_1,
        title = R.string.unlimited_downloads,
        description = R.string.save_videos_from_all_your_favorite_platforms_without_limits,
        isLast = false
    ),
    OnboardingPage(
        image = R.drawable.onboarding_img_2,
        title = R.string.trending_reels,
        description = R.string.discover_and_download_the_latest_trending_reels_instantly,
        isLast = false
    ),
    OnboardingPage(
        image = R.drawable.onboarding_img_3,
        title = R.string.built_in_player,
        description = R.string.play_your_downloads_offline_with_a_reels_like_built_in_player,
        isLast = false
    ),
    OnboardingPage(
        image = R.drawable.onboarding_img_4,
        title = R.string.private_vault,
        description = R.string.protect_your_downloads_with_pin_or_biometric_security,
        isLast = false
    ),
    OnboardingPage(
        image = null,
        title = null,
        description = null,
        isLast = true
    )

)