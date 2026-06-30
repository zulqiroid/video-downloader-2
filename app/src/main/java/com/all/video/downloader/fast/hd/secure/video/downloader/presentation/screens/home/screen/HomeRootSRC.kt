package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboard
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen.DownloadGuide
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen.More
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen.PlatformDetail
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.events.HomeEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.events.HomeNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.viewModel.HomeViewModel

@Composable
fun HomeRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    mainPaddingValues: PaddingValues,
    toWatchReelsTriggered: () -> Unit,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboard.current

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    /**
     * Smart warmup:
     *
     * Home is the hub of the app. User usually opens tools/platform details from here.
     * Preloading here reduces "NotAvailable" at actual show time.
     *
     * Ye sirf preload hai, ad show nahi hoti.
     */
    LaunchedEffect(interstitialAdGate) {
        interstitialAdGate?.preloadFeature(
            VideoDownloaderAdFeatureKeys.PLATFORM_DETAIL_OPEN
        )

        interstitialAdGate?.preloadFeature(
            VideoDownloaderAdFeatureKeys.VIDEO_TO_MP3_ENTRY
        )

        interstitialAdGate?.preloadFeature(
            VideoDownloaderAdFeatureKeys.VIDEO_SPLITTER_ENTRY
        )
    }

    LaunchedEffect(viewModel.navEvents, interstitialAdGate, activity) {
        viewModel.navEvents.collect { navEvent ->
            when (navEvent) {
                HomeNavEvents.NavigateToDownloadGuide -> {
                    /**
                     * Download guide is a help/education screen.
                     * UX wise direct navigation better hai.
                     */
                    backStack.add(DownloadGuide)
                }

                is HomeNavEvents.NavigateToPlatformDetail -> {
                    interstitialAdGate?.showForFeature(
                        activity = activity,
                        featureKey = VideoDownloaderAdFeatureKeys.PLATFORM_DETAIL_OPEN,
                        onComplete = {
                            backStack.add(
                                PlatformDetail(
                                    platform = navEvent.platform
                                )
                            )
                        }
                    ) ?: backStack.add(
                        PlatformDetail(
                            platform = navEvent.platform
                        )
                    )
                }

                HomeNavEvents.NavigateToMore -> {
                    /**
                     * More/settings type screen par entry ad avoid karte hain.
                     * Back navigation interstitial already available hai.
                     */
                    backStack.add(More)
                }

                HomeNavEvents.NavigateToWatchReels -> {
                    /**
                     * Reels is bottom-tab style destination.
                     * Tab switch trigger already handle karega.
                     */
                    toWatchReelsTriggered()
                }

                HomeNavEvents.NavigateToVideoToMp3 -> {
                    interstitialAdGate?.showForFeature(
                        activity = activity,
                        featureKey = VideoDownloaderAdFeatureKeys.VIDEO_TO_MP3_ENTRY,
                        onComplete = {
                            backStack.add(Screen.VideoToMp3)
                        }
                    ) ?: backStack.add(Screen.VideoToMp3)
                }

                HomeNavEvents.NavigateToVideoSplitter -> {
                    interstitialAdGate?.showForFeature(
                        activity = activity,
                        featureKey = VideoDownloaderAdFeatureKeys.VIDEO_SPLITTER_ENTRY,
                        onComplete = {
                            backStack.add(Screen.VideoSplitter)
                        }
                    ) ?: backStack.add(Screen.VideoSplitter)
                }

                HomeNavEvents.NavigateToScreenCasting -> {
                    /**
                     * Screen casting par entry ad avoid rakhi hai because Start Casting button
                     * par controlled interstitial already wired hai.
                     *
                     * Agar yahan bhi ad lagayen to user ko:
                     * Home -> ScreenCasting ad
                     * StartCasting -> another ad chance
                     *
                     * Ye aggressive UX ban sakti hai.
                     */
                    backStack.add(Screen.ScreenCasting)
                }
            }
        }
    }

    HomeSRC(
        mainPaddingValues = mainPaddingValues,
        state = state,
        onEvent = { event ->
            when (event) {
                is HomeEvents.PasteClicked -> {
                    val clipboardText =
                        clipboardManager.nativeClipboard.text
                            ?.toString()
                            .orEmpty()

                    viewModel.onEvent(
                        HomeEvents.PasteClicked(
                            clipboardText = clipboardText
                        )
                    )
                }

                else -> viewModel.onEvent(event)
            }
        },
        showPremiumLabel = showPremiumLabel,
        onPremiumClick = onPremiumClick,
    )
}