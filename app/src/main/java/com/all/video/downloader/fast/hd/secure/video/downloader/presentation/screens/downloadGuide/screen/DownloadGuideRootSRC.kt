package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.screen

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
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.events.DownloadGuideNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.viewModel.DownloadGuideViewModel
import androidx.activity.compose.BackHandler
import com.core.ads.ui.rememberInterstitialBackNavigationAction
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.events.DownloadGuideEvents

@Composable
fun DownloadGuideRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: DownloadGuideViewModel = hiltViewModel<DownloadGuideViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val navigateBackWithInterstitial = rememberInterstitialBackNavigationAction(
        screenKey = VideoDownloaderAdScreenKeys.DOWNLOAD_GUIDE
    ) {
        backStack.removeLastOrNull()
    }

    BackHandler {
        viewModel.onEvent(DownloadGuideEvents.BackClicked)
    }

    LaunchedEffect(viewModel, lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navEvents.collect { navEvent ->
                when (navEvent) {
                    DownloadGuideNavEvents.NavigateBack -> {
                        navigateBackWithInterstitial()
                    }

                    DownloadGuideNavEvents.NavigateToHome -> {
                        backStack.removeLastOrNull()

                        if (backStack.none { it is Screen.Main }) {
                            backStack.add(Screen.Main)
                        }
                    }
                }
            }
        }
    }

    DownloadGuideSRC(
        state = state,
        onEvent = viewModel::onEvent
    )
}