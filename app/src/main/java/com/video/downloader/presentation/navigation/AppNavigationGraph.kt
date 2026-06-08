package com.video.downloader.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.video.downloader.presentation.screens.appLanguage.screen.AppLanguageRootSRC
import com.video.downloader.presentation.screens.downloadGuide.screen.DownloadGuideRootSRC
import com.video.downloader.presentation.screens.main.screen.MainRootSRC
import com.video.downloader.presentation.screens.medaPlayer.screen.MediaPlayerScreen
import com.video.downloader.presentation.screens.more.screen.MoreRootSRC
import com.video.downloader.presentation.screens.onboarding.screen.OnboardingRootSRC
import com.video.downloader.presentation.screens.platformDetail.screen.PlatformDetailRootSRC
import com.video.downloader.presentation.screens.screenCasting.screen.ScreenCastingRootSRC
import com.video.downloader.presentation.screens.splash.screen.SplashRootSRC
import com.video.downloader.presentation.screens.videoToMp3.screen.VideoToMp3RootSRC
import com.video.downloader.presentation.screens.videoToMp3Result.screen.VideoToMp3ResultRootSRC
import com.video.downloader.presentation.screens.videoSplitter.screen.VideoSplitterRootSRC


@Composable
fun AppNavigationGraph() {
    val backStack = rememberNavBackStack(Screen.Splash)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashRootSRC(backStack = backStack)
            }

            entry<Screen.AppLanguage> {
                AppLanguageRootSRC(backStack = backStack)
            }

            entry<Screen.Onboarding> {
                OnboardingRootSRC(backStack = backStack)
            }

            entry<Screen.Main> {
                MainRootSRC(
                    backStack = backStack
                )
            }

            entry<Screen.DownloadGuide> {
                DownloadGuideRootSRC(backStack = backStack)
            }

            entry<Screen.MediaPlayer> {args ->
                MediaPlayerScreen(
                    mediaList = args.mediaList,
                    startIndex = args.startIndex,
                    backStack = backStack
                )
            }

            entry<Screen.PlatformDetail> { platformDetail ->
                PlatformDetailRootSRC(
                    backStack = backStack,
                    platform = platformDetail.platform
                )
            }
            entry<Screen.More> {
                MoreRootSRC(
                    backStack = backStack,
                 )
            }

            entry<Screen.VideoToMp3> {
                VideoToMp3RootSRC(
                    backStack = backStack
                )
            }

            entry<Screen.VideoToMp3Result> { args ->
                VideoToMp3ResultRootSRC(
                    conversionId = args.conversionId,
                    backStack = backStack
                )
            }
            entry<Screen.VideoSplitter> {
                VideoSplitterRootSRC(
                    backStack = backStack
                )
            }

            entry<Screen.ScreenCasting> {
                ScreenCastingRootSRC(
                    backStack = backStack
                )
            }
        }
    )
}