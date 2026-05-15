package com.video.downloader.presentation.screens.main.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel


@Composable
fun MainRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: MainViewModel = hiltViewModel<MainViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    MainSRC(
        state = state,
        viewModel = viewModel
    )



}