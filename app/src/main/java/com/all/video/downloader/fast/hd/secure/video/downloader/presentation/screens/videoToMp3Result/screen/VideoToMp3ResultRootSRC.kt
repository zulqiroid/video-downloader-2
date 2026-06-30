package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.events.VideoToMp3ResultEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.events.VideoToMp3ResultNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.viewModel.VideoToMp3ResultViewModel
import com.core.ads.ui.rememberInterstitialBackNavigationAction
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys

@Composable
fun VideoToMp3ResultRootSRC(
    conversionId: String,
    backStack: NavBackStack<NavKey>,
    viewModel: VideoToMp3ResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navigateBackWithInterstitial = rememberInterstitialBackNavigationAction(
        screenKey = VideoDownloaderAdScreenKeys.VIDEO_TO_MP3_RESULT
    ) {
        backStack.removeLastOrNull()
    }

    LaunchedEffect(conversionId) {
        viewModel.setConversionId(conversionId)
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                VideoToMp3ResultNavEvents.NavigateBack -> {
                    navigateBackWithInterstitial()
                }
            }
        }
    }

    BackHandler {
        viewModel.onEvent(VideoToMp3ResultEvents.BackClicked)
    }

    VideoToMp3ResultSRC(
        state = state,
        onEvent = viewModel::onEvent
    )
}