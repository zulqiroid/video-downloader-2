package com.video.downloader.presentation.screens.home.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.screens.home.events.HomeEvents
import com.video.downloader.presentation.screens.home.viewModel.HomeViewModel


@Composable
fun HomeRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    mainPaddingValues: PaddingValues
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboard.current

    HomeSRC(
        mainPaddingValues = mainPaddingValues,
        state = state,
        onEvent = { event ->
            when (event) {
                is HomeEvents.PasteClicked -> {
                    val clipboardText = clipboardManager.nativeClipboard.text?.toString().orEmpty()
                    viewModel.onEvent(
                        HomeEvents.PasteClicked(
                            clipboardText = clipboardText
                        )
                    )
                }

                else -> viewModel.onEvent(event)
            }
        }
    )

}