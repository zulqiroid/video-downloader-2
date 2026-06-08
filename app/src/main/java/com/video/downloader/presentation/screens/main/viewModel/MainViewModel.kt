package com.video.downloader.presentation.screens.main.viewModel

import androidx.lifecycle.ViewModel
import com.video.downloader.presentation.screens.main.events.MainEvents
import com.video.downloader.presentation.screens.main.events.MainNavEvents
import com.video.downloader.presentation.screens.main.events.MoreFileDetailsDialogueEvents
import com.video.downloader.presentation.screens.main.states.MainStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel()  {

    private val _state = MutableStateFlow(MainStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MainNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(event: MainEvents){
        when(event){
            is MainEvents.OnTabSelected -> {
                _state.value = _state.value.copy(
                    selectedTab = event.tab
                )
            }

            is MainEvents.OnMoreClicked -> {
                _state.update {
                    it.copy(
                        showPlayerDialogue = true,
                        playerMediaItem = event.mediaFile
                    )
                }
            }
        }

    }

    fun onFileDialogueEvents(event: MoreFileDetailsDialogueEvents){
        when(event){
            MoreFileDetailsDialogueEvents.OnDeleteClicked -> {
                dismissPlayerDialog()
            }
            MoreFileDetailsDialogueEvents.OnFileInfoClicked -> {
                dismissPlayerDialog()
            }
            MoreFileDetailsDialogueEvents.OnMoveToVaultClicked -> {
                dismissPlayerDialog()
            }
            MoreFileDetailsDialogueEvents.OnPlayerDialogDismissed -> {
                dismissPlayerDialog()
            }
            MoreFileDetailsDialogueEvents.OnRenameClicked -> {
                dismissPlayerDialog()
            }
            MoreFileDetailsDialogueEvents.OnShareClicked -> {
                dismissPlayerDialog()
            }
        }
    }

    private fun dismissPlayerDialog() {
        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null
            )
        }
    }


}