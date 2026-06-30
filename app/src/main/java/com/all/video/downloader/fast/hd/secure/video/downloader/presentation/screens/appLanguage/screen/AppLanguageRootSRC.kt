package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.screen

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
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel

@Composable
fun AppLanguageRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: AppLanguageViewModel = hiltViewModel<AppLanguageViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    LaunchedEffect(viewModel, lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navEvents.collect { navEvent ->
                when (navEvent) {
                    AppLanguageNavEvents.NavigateBack -> {
                        backStack.removeLastOrNull()
                    }

                    AppLanguageNavEvents.NavigateToMain -> {
                        val openedFromMoreSettings = backStack.any { key ->
                            key == Screen.More
                        }

                        if (openedFromMoreSettings) {
                            backStack.removeLastOrNull()
                            return@collect
                        }

                        interstitialAdGate?.showForFeature(
                            activity = activity,
                            featureKey = VideoDownloaderAdFeatureKeys.APP_LANGUAGE_CONTINUE,
                            onComplete = {
                                backStack.removeLastOrNull()
                                backStack.add(Screen.Onboarding)
                            }
                        ) ?: run {
                            backStack.removeLastOrNull()
                            backStack.add(Screen.Onboarding)
                        }
                    }
                }
            }
        }
    }

    AppLanguageSRC(
        state = state,
        onEvent = viewModel::onEvent
    )
}