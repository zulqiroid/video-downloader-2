package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events.DownloadEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events.DownloadNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadTab
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.viewModel.DownloadViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys

@Composable
fun DownloadRootSRC(
    mainViewModel: MainViewModel,
    mainPaddingValues: PaddingValues,
    backStack: NavBackStack<NavKey>,
    notificationOpenRequestId: Long = 0L,
    viewModel: DownloadViewModel = hiltViewModel(),
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    LaunchedEffect(notificationOpenRequestId) {
        if (notificationOpenRequestId > 0L) {
            viewModel.onEvents(
                DownloadEvents.OnTabChange(DownloadTab.IN_PROGRESS)
            )
        }
    }

    LaunchedEffect(interstitialAdGate) {
        interstitialAdGate?.preloadFeature(
            VideoDownloaderAdFeatureKeys.DOWNLOAD_OPTIONS_OPEN
        )

        interstitialAdGate?.preloadFeature(
            VideoDownloaderAdFeatureKeys.DOWNLOAD_MEDIA_OPEN
        )
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                DownloadNavEvents.NavigateToSettingScreen -> {
                    backStack.add(Screen.More)
                }
                is DownloadNavEvents.SendToMedia -> {
                    interstitialAdGate?.showForFeature(
                        activity = activity,
                        featureKey = VideoDownloaderAdFeatureKeys.DOWNLOAD_MEDIA_OPEN,
                        onComplete = {
                            backStack.add(
                                Screen.MediaPlayer(
                                    mediaList = event.mediaList,
                                    startIndex = event.startIndex
                                )
                            )
                        }
                    ) ?: backStack.add(
                        Screen.MediaPlayer(
                            mediaList = event.mediaList,
                            startIndex = event.startIndex
                        )
                    )
                }
            }
        }
    }

    DownloadSRC(
        mainViewModel = mainViewModel,
        mainPaddingValues = mainPaddingValues,
        state = state,
        viewModel = viewModel,
        showPremiumLabel = showPremiumLabel,
        onPremiumClick = onPremiumClick
    )
}