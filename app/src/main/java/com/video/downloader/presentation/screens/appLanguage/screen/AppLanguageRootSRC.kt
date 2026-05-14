package com.video.downloader.presentation.screens.appLanguage.screen

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
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.video.downloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel

@Composable
fun AppLanguageRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: AppLanguageViewModel = hiltViewModel<AppLanguageViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navEvents.collect { navEvent ->
                when (navEvent) {
                    AppLanguageNavEvents.NavigateBack -> {
                        backStack.removeLastOrNull()
                    }

                    AppLanguageNavEvents.NavigateToMain -> {
                        backStack.removeLastOrNull()
                        backStack.add(Screen.Main)
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