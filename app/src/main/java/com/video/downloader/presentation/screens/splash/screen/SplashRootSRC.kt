package com.video.downloader.presentation.screens.splash.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.splash.events.SplashNavEvents
import com.video.downloader.presentation.screens.splash.viewModel.SplashViewModel

@Composable
fun SplashRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navEvents.collect { navEvent ->
                when (navEvent) {
                    SplashNavEvents.NavigateToMain -> {
                        backStack.removeLastOrNull()
                        backStack.add(Screen.Main)
                    }
                }
            }
        }
    }

    SplashSRC(
        state = state.value,
        onEvent = viewModel::onEvent
    )
}