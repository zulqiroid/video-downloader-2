package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.events

sealed interface OnboardingNavEvents {

    data class ScrollToPage(val page: Int) : OnboardingNavEvents

    object NavigateNext : OnboardingNavEvents

    object NavigateToMain: OnboardingNavEvents


}