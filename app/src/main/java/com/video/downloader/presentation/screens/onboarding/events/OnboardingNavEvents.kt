package com.video.downloader.presentation.screens.onboarding.events

sealed interface OnboardingNavEvents {

    data class ScrollToPage(val page: Int) : OnboardingNavEvents

    object NavigateNext : OnboardingNavEvents

    object NavigateToMain: OnboardingNavEvents


}