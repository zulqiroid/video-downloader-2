package com.video.downloader.presentation.screens.reels.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.video.downloader.presentation.screens.reels.events.ReelsNavEvents
import com.video.downloader.presentation.screens.reels.states.ReelsStates
import com.video.downloader.presentation.screens.reels.viewModel.ReelsViewModel


@Composable
fun ReelsRootSRC(
    mainViewModel: MainViewModel,
    backStack: NavBackStack<NavKey>,
    viewModel: ReelsViewModel = hiltViewModel(),
    mainPaddingValues: PaddingValues
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect {
            when(it){
                ReelsNavEvents.NavigateToSettingScreen -> {
                    backStack.add(Screen.More)
                }
            }
        }
    }

    ReelsSRC(
        mainPaddingValues = mainPaddingValues,
        state = state,
        viewModel = viewModel,
    )
}