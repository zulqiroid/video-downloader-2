package com.video.downloader.presentation.screens.onboarding.states

data class OnboardingStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val onboardingPage: List<OnboardingPage> = defaultOnboardingPages(),
    val currentPage: Int = 0,
)
