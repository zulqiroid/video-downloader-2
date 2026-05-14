package com.video.downloader.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
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
        entryProvider = entryProvider{

            entry<Screen.Splash> {
                SplashRootSRC(
                    backStack = backStack
                )
            }

            entry<Screen.Main> {
                Text(text = "Main Screen")
            }
        }
    )
}