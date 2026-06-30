package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.videoToMp3.VideoToMp3UseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.events.VideoToMp3ResultEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.events.VideoToMp3ResultNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.states.VideoToMp3ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoToMp3ResultViewModel @Inject constructor(
    private val useCases: VideoToMp3UseCases
) : ViewModel() {

    private val _state = MutableStateFlow(VideoToMp3ResultState())
    val state = _state.asStateFlow()

    private val _navEvents = Channel<VideoToMp3ResultNavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    private var observeJob: Job? = null

    fun setConversionId(conversionId: String) {
        if (_state.value.conversionId == conversionId) return

        _state.update {
            it.copy(conversionId = conversionId)
        }

        observeConversion(conversionId)
    }

    fun onEvent(event: VideoToMp3ResultEvents) {
        when (event) {
            VideoToMp3ResultEvents.BackClicked -> {
                viewModelScope.launch {
                    _navEvents.send(VideoToMp3ResultNavEvents.NavigateBack)
                }
            }

            VideoToMp3ResultEvents.SaveClicked -> {
                saveMp3()
            }
        }
    }

    private fun observeConversion(conversionId: String) {
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            useCases.observeConversions().collect { items ->
                val item = items.firstOrNull { conversion ->
                    conversion.id == conversionId
                }

                _state.update {
                    it.copy(item = item)
                }
            }
        }
    }

    private fun saveMp3() {
        val item = _state.value.item ?: return

        if (_state.value.isSaving || _state.value.isSaved) return

        val tempPath = item.tempOutputPath

        if (tempPath.isNullOrBlank()) {
            _state.update {
                it.copy(errorMessage = "Converted MP3 file not found.")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            runCatching {
                useCases.saveConvertedMp3(
                    tempOutputPath = tempPath,
                    outputNameWithoutExtension = item.fileName.substringBeforeLast(".")
                )
            }.onSuccess { savedUri ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        savedOutputUri = savedUri,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Unable to save MP3 file."
                    )
                }
            }
        }
    }
}