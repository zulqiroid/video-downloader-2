package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.screen

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
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.events.OnboardingEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.events.OnboardingNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.viewModel.OnboardingViewModel
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys


@Composable
fun OnboardingRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: OnboardingViewModel = hiltViewModel<OnboardingViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect {
            when(it){
                OnboardingNavEvents.NavigateToMain -> {
                    interstitialAdGate?.showForFeature(
                        activity = activity,
                        featureKey = VideoDownloaderAdFeatureKeys.ONBOARDING_COMPLETE,
                        onComplete = {
                            backStack.removeAll(backStack)
                            backStack.add(Screen.Main)
                            if (state.isPremiumEnable) {
                                backStack.add(Screen.Premium)
                            }
                        }
                    ) ?: run {
                        backStack.removeAll(backStack)
                        backStack.add(Screen.Main)
                        if (state.isPremiumEnable) {
                            backStack.add(Screen.Premium)
                        }
                    }
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