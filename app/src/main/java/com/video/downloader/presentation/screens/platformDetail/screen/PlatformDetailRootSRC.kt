package com.video.downloader.presentation.screens.platformDetail.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboard
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.domain.models.Platforms
import com.video.downloader.presentation.screens.platformDetail.events.PlatformDetailEvents
import com.video.downloader.presentation.screens.platformDetail.events.PlatformDetailNavEvents
import com.video.downloader.presentation.screens.platformDetail.viewModel.PlatformDetailViewModel

@Composable
fun PlatformDetailRootSRC(
    backStack: NavBackStack<NavKey>,
    platform: Platforms,
    viewModel: PlatformDetailViewModel = hiltViewModel<PlatformDetailViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(platform) {
        viewModel.setPlatform(platform)
    }

    LaunchedEffect(viewModel, lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navEvents.collect { navEvent ->
                when (navEvent) {
                    PlatformDetailNavEvents.NavigateBack -> {
                        backStack.removeLastOrNull()
                    }
                }
            }
        }
    }

    PlatformSRC(
        state = state,
        onEvent = { event ->
            when (event) {
                is PlatformDetailEvents.PasteClicked -> {
                    val clipboardText = clipboard
                        .nativeClipboard
                        .text
                        ?.toString()
                        .orEmpty()
                        .trim()

                    viewModel.onEvent(
                        PlatformDetailEvents.PasteClicked(
                            clipboardText = clipboardText
                        )
                    )
                }

                else -> viewModel.onEvent(event)
            }
        }
    )
}