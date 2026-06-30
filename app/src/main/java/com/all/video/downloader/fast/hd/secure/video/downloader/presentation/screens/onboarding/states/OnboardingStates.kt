package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.states

data class OnboardingStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val onboardingPage: List<OnboardingPage> = defaultOnboardingPages(),
    val currentPage: Int = 0,

    val isPremiumEnable: Boolean = false,
    )
