package com.video.downloader.presentation.screens.platformDetail.viewModel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.domain.models.Platforms
import com.video.downloader.presentation.screens.platformDetail.events.PlatformDetailEvents
import com.video.downloader.presentation.screens.platformDetail.events.PlatformDetailNavEvents
import com.video.downloader.presentation.screens.platformDetail.states.PlatformDetailStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlatformDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {


    private val _state = MutableStateFlow(PlatformDetailStates())
    val state: StateFlow<PlatformDetailStates> = _state.asStateFlow()

    private val _navEvents = Channel<PlatformDetailNavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    fun onEvent(event: PlatformDetailEvents) {
        when (event) {
            PlatformDetailEvents.BackClicked -> onBackClicked()

            is PlatformDetailEvents.VideoUrlChanged -> onVideoUrlChanged(event.value)

            is PlatformDetailEvents.PasteClicked -> onPasteClicked(event.clipboardText)

            PlatformDetailEvents.DownloadVideoClicked -> onDownloadVideoClicked()

            PlatformDetailEvents.SeeAllClicked -> onSeeAllClicked()

            is PlatformDetailEvents.TrendingVideoClicked -> onTrendingVideoClicked(event.videoId)
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _navEvents.send(PlatformDetailNavEvents.NavigateBack)
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
        val cleanedText = clipboardText.trim()

        if (cleanedText.isBlank()) {
            _state.update { currentState ->
                currentState.copy(error = "Clipboard is empty")
            }
            return
        }

        _state.update { currentState ->
            currentState.copy(
                videoUrl = cleanedText,
                error = null
            )
        }
    }

    private fun onDownloadVideoClicked() {
        val url = _state.value.videoUrl.trim()

        if (url.isBlank()) {
            _state.update { currentState ->
                currentState.copy(error = "Please paste a video link first")
            }
            return
        }

        _state.update { currentState ->
            currentState.copy(
                isLoading = true,
                error = null
            )
        }

        // Later: connect with WorkManager download flow.
    }

    fun setPlatform(platform: Platforms) {
        _state.update { currentState ->
            currentState.copy(
                platform = platform,
            )
        }
    }

    private fun onSeeAllClicked() {
        // Later: navigate/open full trending list.
    }

    private fun onTrendingVideoClicked(videoId: String) {
        // Later: open preview/details.
    }

}