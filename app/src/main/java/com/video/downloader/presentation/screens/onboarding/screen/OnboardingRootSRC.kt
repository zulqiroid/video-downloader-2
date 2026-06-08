package com.video.downloader.presentation.screens.onboarding.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.video.downloader.presentation.screens.onboarding.events.OnboardingEvents
import com.video.downloader.presentation.screens.onboarding.events.OnboardingNavEvents
import com.video.downloader.presentation.screens.onboarding.viewModel.OnboardingViewModel


@Composable
fun OnboardingRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: OnboardingViewModel = hiltViewModel<OnboardingViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect {
            when(it){
                 OnboardingNavEvents.NavigateToMain -> {
                     backStack.removeAll(backStack)
                     backStack.add(Screen.Main)
                 }
                else -> Unit
             }
        }
    }

    OnboardingSRC(
        state = state,
        onEvent = viewModel::onEvent,
        effectFlow = viewModel.navEvents,
        onFinished = {
            viewModel.onEvent(OnboardingEvents.FinishOnBoarding)
        }
    )

}