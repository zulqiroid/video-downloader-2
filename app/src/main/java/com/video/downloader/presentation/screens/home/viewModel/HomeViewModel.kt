package com.video.downloader.presentation.screens.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.domain.models.Platforms
import com.video.downloader.presentation.screens.home.events.HomeEvents
import com.video.downloader.presentation.screens.home.events.HomeNavEvents
import com.video.downloader.presentation.screens.home.states.HomeStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeStates())
    val state: StateFlow<HomeStates> = _state.asStateFlow()

    private val _navEvents  = MutableSharedFlow<HomeNavEvents>()
    val navEvents = _navEvents.asSharedFlow()


    fun onEvent(event: HomeEvents) {
        when (event) {
            is HomeEvents.VideoUrlChanged -> onVideoUrlChanged(event.value)

            is HomeEvents.PasteClicked -> onPasteClicked(event.clipboardText)

            HomeEvents.DownloadClicked -> onDownloadClicked()

            HomeEvents.WatchTrendingReelsClicked -> onWatchTrendingReelsClicked()
            is HomeEvents.PlatformClicked -> onPlatformClicked(event.platform)
            is HomeEvents.FeatureClicked -> {}
            HomeEvents.HowToDownloadVideosClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(HomeNavEvents.NavigateToDownloadGuide)
                }
            }

            HomeEvents.SettingClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(HomeNavEvents.NavigateToMore)
                }
            }
        }
    }

    private fun onWatchTrendingReelsClicked() {
        // Later: emit NavEvent or notify Main screen to switch selected tab to Reels.
    }

    private fun onPlatformClicked(platform: Platforms) {
        viewModelScope.launch {
            _navEvents.emit(
                HomeNavEvents.NavigateToPlatformDetail(
                    platform = platform
                )
            )
        }
    }

    private fun onVideoUrlChanged(value: String) {
        _state.update { currentState ->
            currentState.copy(
                videoUrl = value,
                error = null
            )
        }
    }

    private fun onPasteClicked(clipboardText: String) {
        _state.update { currentState ->
            currentState.copy(
                videoUrl = clipboardText.trim(),
                error = null
            )
        }
    }

    private fun onDownloadClicked() {
        viewModelScope.launch {

            val currentUrl = _state.value.videoUrl.trim()

            if (currentUrl.isBlank()) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Please paste a video link first"
                    )
                }
                return@launch
            }

            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    error = null
                )
            }

            delay(5000)

            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    error = null
                )
            }

        }
     }
}