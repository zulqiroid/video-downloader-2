package com.video.downloader.presentation.screens.videoToMp3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Status
import com.video.downloader.domain.usecases.videoToMp3.VideoToMp3UseCases
import com.video.downloader.presentation.screens.videoToMp3.events.VideoToMp3Events
import com.video.downloader.presentation.screens.videoToMp3.events.VideoToMp3NavEvents
import com.video.downloader.presentation.screens.videoToMp3.states.VideoToMp3States
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoToMp3ViewModel @Inject constructor(
    private val useCases: VideoToMp3UseCases
) : ViewModel() {

    private val _state = MutableStateFlow(VideoToMp3States())
    val state = _state.asStateFlow()

    private val _navEvents = Channel<VideoToMp3NavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    private val startedWorkIds = mutableSetOf<String>()
    private var lastResultNavigationId: String? = null

    init {
        observeConversions()
    }

    fun onEvent(event: VideoToMp3Events) {
        when (event) {
            VideoToMp3Events.BackClicked -> {
                viewModelScope.launch {
                    _navEvents.send(VideoToMp3NavEvents.NavigateBack)
                }
            }

            VideoToMp3Events.UploadVideoClicked -> {
                viewModelScope.launch {
                    _navEvents.send(VideoToMp3NavEvents.LaunchVideoPicker)
                }
            }

            is VideoToMp3Events.VideoSelected -> {
                _state.update { current ->
                    current.copy(
                        selectedVideoUri = event.uri.toString(),
                        selectedVideoName = event.fileName,
                        errorMessage = null
                    )
                }
            }

            is VideoToMp3Events.PresetSelected -> {
                _state.update { current ->
                    current.copy(
                        selectedPreset = event.preset,
                        errorMessage = null
                    )
                }
            }

            VideoToMp3Events.ConvertClicked -> {
                startConversion()
            }

            is VideoToMp3Events.CancelClicked -> {
                cancelConversion(event.id)
            }
        }
    }

    private fun observeConversions() {
        viewModelScope.launch {
            useCases.observeConversions().collect { items ->
                _state.update { current ->
                    current.copy(conversions = items)
                }

                val readyItem = items.firstOrNull { item ->
                    item.id in startedWorkIds &&
                            item.status == VideoToMp3Status.READY_TO_SAVE &&
                            item.id != lastResultNavigationId
                }

                if (readyItem != null) {
                    lastResultNavigationId = readyItem.id

                    _navEvents.send(
                        VideoToMp3NavEvents.NavigateToResult(
                            conversionId = readyItem.id
                        )
                    )
                }
            }
        }
    }

    private fun startConversion() {
        val current = _state.value
        val inputUri = current.selectedVideoUri

        if (inputUri.isNullOrBlank()) {
            _state.update {
                it.copy(errorMessage = "Please upload a video first.")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null
                )
            }

            runCatching {
                useCases.startConversion(
                    inputUri = inputUri,
                    fileName = current.selectedVideoName.ifBlank {
                        "selected_video.mp4"
                    },
                    preset = current.selectedPreset
                )
            }.onSuccess { workId ->
                startedWorkIds += workId
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        errorMessage = throwable.message ?: "Unable to start conversion."
                    )
                }
            }

            _state.update {
                it.copy(isSubmitting = false)
            }
        }
    }

    private fun cancelConversion(id: String) {
        viewModelScope.launch {
            useCases.cancelConversion(id)
        }
    }
}