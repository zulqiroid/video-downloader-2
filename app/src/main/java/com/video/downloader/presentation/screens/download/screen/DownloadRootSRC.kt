package com.video.downloader.presentation.screens.download.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.download.events.DownloadNavEvents
import com.video.downloader.presentation.screens.download.viewModel.DownloadViewModel
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel


@Composable
fun DownloadRootSRC(
    mainViewModel: MainViewModel,
    mainPaddingValues : PaddingValues,
    backStack: NavBackStack<NavKey>,
    viewModel: DownloadViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect {
            when(it){
                DownloadNavEvents.NavigateToSettingScreen -> {
                    backStack.add(Screen.More)
                }
            }
        }
    }


    DownloadSRC(
        mainViewModel = mainViewModel,
        mainPaddingValues = mainPaddingValues,
        state = state,
        viewModel = viewModel
    )

}