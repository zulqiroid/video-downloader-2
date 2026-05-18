package com.video.downloader.presentation.screens.more.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.more.events.MoreNavEvents
import com.video.downloader.presentation.screens.more.viewModel.MoreViewModel


@Composable
fun MoreRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { navEvent ->
            when (navEvent) {
                MoreNavEvents.NavigateBack -> {
                    backStack.remove(Screen.More)
                }
            }
        }
    }


    MoreSRC(
        state = state,
        onEvent = viewModel::onEvent
    )

}