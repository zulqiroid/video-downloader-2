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
import com.video.downloader.presentation.screens.onboarding.screen.OnboardingRootSRC
import com.video.downloader.presentation.screens.platformDetail.screen.PlatformDetailRootSRC
import com.video.downloader.presentation.screens.splash.screen.SplashRootSRC


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

            entry<Screen.PlatformDetail> { platformDetail ->
                PlatformDetailRootSRC(
                    backStack = backStack,
                    platform = platformDetail.platform
                )
            }
        }
    )
}