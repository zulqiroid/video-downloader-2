package com.video.downloader.presentation.screens.player.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.domain.usecases.GetAudiosUseCase
import com.video.downloader.domain.usecases.GetVideosUseCase
import com.video.downloader.presentation.screens.player.events.PlayerEvents
import com.video.downloader.presentation.screens.player.events.PlayerNavEvents
import com.video.downloader.presentation.screens.player.events.PlayerNavEvents.*
import com.video.downloader.presentation.screens.player.states.PlayerStates
import com.video.downloader.presentation.screens.player.states.PlayerTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.copy


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getVideos: GetVideosUseCase,
    private val getAudios: GetAudiosUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<PlayerNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private var observeMediaJob: Job? = null

    fun onEvent(event: PlayerEvents) {
        when (event) {
            is PlayerEvents.OnMediaPermissionResult -> {
                _state.update {
                    it.copy(
                        isMediaPermissionGranted = event.granted,
                        showMediaPermissionDialog = !event.granted,
                        shouldOpenMediaPermissionSettings = event.permanentlyDenied
                    )
                }
            }

            is PlayerEvents.SendToMedia -> {
                viewModelScope.launch {
                    _navEvents.emit(
                        SendToMedia(
                            mediaList = event.mediaList,
                            startIndex = event.startIndex
                        )
                    )
                }
            }

            PlayerEvents.OnSettingClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(
                        PlayerNavEvents.NavigateToSettingScreen
                    )
                }
            }
        }
    }



    fun onMediaPermissionGranted() {
        if (observeMediaJob?.isActive == true) return

        observeMediaJob = viewModelScope.launch {
            combine(
                getVideos(),
                getAudios()
            ) { videos, audios ->
                videos to audios
            }
                .onStart {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }
                .collect { (videos, audios) ->
                    _state.update {
                        it.copy(
                            videos = videos,
                            audios = audios,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onMediaPermissionDenied() {
        observeMediaJob?.cancel()
        observeMediaJob = null

        _state.update {
            it.copy(
                videos = emptyList(),
                audios = emptyList(),
                isLoading = false
            )
        }
    }

    fun onTabChange(tab: PlayerTab) {
        _state.update {
            it.copy(
                selectedTab = tab
            )
        }

     }

}