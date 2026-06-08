package com.video.downloader.presentation.screens.main.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.screens.main.events.MoreFileDetailsDialogueEvents
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.video.downloader.presentation.screens.player.componants.PlayerMediaActionsDialog


@Composable
fun MainRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: MainViewModel = hiltViewModel<MainViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainSRC(
        backStack = backStack,
        state = state,
        viewModel = viewModel
    )

    PlayerMediaActionsDialog(
        isVisible = state.showPlayerDialogue,
        mediaFile = state.playerMediaItem,
        onDismiss = {
            viewModel.onFileDialogueEvents(MoreFileDetailsDialogueEvents.OnPlayerDialogDismissed)
        },
        onRenameClick = {
            viewModel.onFileDialogueEvents(MoreFileDetailsDialogueEvents.OnRenameClicked)
        },
        onMoveToVaultClick = {
            viewModel.onFileDialogueEvents(MoreFileDetailsDialogueEvents.OnMoveToVaultClicked)
        },
        onShareClick = {
            viewModel.onFileDialogueEvents(MoreFileDetailsDialogueEvents.OnShareClicked)
        },
        onFileInfoClick = {
            viewModel.onFileDialogueEvents(MoreFileDetailsDialogueEvents.OnFileInfoClicked)
        },
        onDeleteClick = {
            viewModel.onFileDialogueEvents(MoreFileDetailsDialogueEvents.OnDeleteClicked)
        }
    )



}