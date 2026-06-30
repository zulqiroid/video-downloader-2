package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.events

sealed interface OnboardingEvents {

    data class PageChanged(val page: Int) : OnboardingEvents

    object NextClicked : OnboardingEvents

    object FinishOnBoarding : OnboardingEvents

}