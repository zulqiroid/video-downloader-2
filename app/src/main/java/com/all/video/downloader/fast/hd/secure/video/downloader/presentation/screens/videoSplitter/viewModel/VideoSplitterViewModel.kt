package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitRange
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitterJobStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.videoSplitter.VideoSplitterUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.events.VideoSplitterEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.events.VideoSplitterNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.states.VideoSplitterStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoSplitterViewModel @Inject constructor(
    private val useCases: VideoSplitterUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(VideoSplitterStates())
    val state = _state.asStateFlow()

    private val _navEvents = Channel<VideoSplitterNavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        observeSplitJobs()
    }

    fun onEvent(event: VideoSplitterEvents) {
        when (event) {
            VideoSplitterEvents.BackClicked -> {
                viewModelScope.launch {
                    _navEvents.send(VideoSplitterNavEvents.NavigateBack)
                }
            }

            VideoSplitterEvents.UploadVideoClicked -> {
                viewModelScope.launch {
                    _navEvents.send(VideoSplitterNavEvents.LaunchVideoPicker)
                }
            }

            is VideoSplitterEvents.VideoSelected -> {
                val safeDuration = event.durationMs.coerceAtLeast(0L)

                _state.update {
                    it.copy(
                        selectedVideoUri = event.uri.toString(),
                        selectedVideoName = event.fileName,
                        selectedVideoSizeBytes = event.sizeBytes,
                        durationMs = safeDuration,
                        rangeStartMs = 0L,
                        rangeEndMs = safeDuration,
                        errorMessage = null,
                        saveSuccessMessage = null,
                        showResultScreen = false,
                        resultClips = emptyList()
                    )
                }
            }

            is VideoSplitterEvents.RangeChanged -> {
                val duration = _state.value.durationMs

                val start = event.startMs.coerceIn(0L, duration)
                val end = event.endMs.coerceIn(0L, duration)

                _state.update {
                    it.copy(
                        rangeStartMs = minOf(start, end),
                        rangeEndMs = maxOf(start, end),
                        errorMessage = null,
                        saveSuccessMessage = null
                    )
                }
            }

            VideoSplitterEvents.SplitVideoClicked -> {
                startSplit()
            }

            is VideoSplitterEvents.CancelSplitClicked -> {
                cancelSplit(event.id)
            }

            is VideoSplitterEvents.SaveClipClicked -> {
                saveClip(event.clipId)
            }

            VideoSplitterEvents.SavePermissionDenied -> {
                _state.update {
                    it.copy(
                        errorMessage = "Storage permission is required to save this clip.",
                        saveSuccessMessage = null
                    )
                }
            }

            VideoSplitterEvents.ResultBackClicked -> {
                _state.update {
                    it.copy(
                        showResultScreen = false,
                        resultClips = emptyList(),
                        currentJobId = null,
                        splitProgress = 0,
                        isSplitting = false,
                        isSavingClip = false,
                        savingClipId = null,
                        saveSuccessMessage = null,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun observeSplitJobs() {
        viewModelScope.launch {
            useCases.observeSplitJobs().collect { jobs ->
                val currentJobId = _state.value.currentJobId ?: return@collect
                val currentJob = jobs.firstOrNull { it.id == currentJobId } ?: return@collect

                when (currentJob.status) {
                    VideoSplitterJobStatus.QUEUED,
                    VideoSplitterJobStatus.SPLITTING -> {
                        _state.update {
                            it.copy(
                                isSplitting = true,
                                splitProgress = currentJob.progress.coerceIn(0, 99),
                                errorMessage = null
                            )
                        }
                    }

                    VideoSplitterJobStatus.SUCCESS -> {
                        _state.update {
                            it.copy(
                                isSplitting = false,
                                splitProgress = 100,
                                resultClips = currentJob.clips,
                                showResultScreen = true,
                                errorMessage = null
                            )
                        }
                    }

                    VideoSplitterJobStatus.FAILED -> {
                        _state.update {
                            it.copy(
                                isSplitting = false,
                                splitProgress = 0,
                                errorMessage = currentJob.errorMessage
                                    ?: "Unable to split this video."
                            )
                        }
                    }

                    VideoSplitterJobStatus.CANCELLED -> {
                        _state.update {
                            it.copy(
                                isSplitting = false,
                                splitProgress = 0,
                                currentJobId = null,
                                errorMessage = "Video splitting cancelled."
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startSplit() {
        val current = _state.value
        val inputUri = current.selectedVideoUri

        if (inputUri.isNullOrBlank()) {
            _state.update {
                it.copy(errorMessage = "Please upload a video first.")
            }
            return
        }

        if (current.rangeEndMs <= current.rangeStartMs) {
            _state.update {
                it.copy(errorMessage = "Please select a valid split range.")
            }
            return
        }

        if (current.selectedRangeDurationMs < MIN_SPLIT_DURATION_MS) {
            _state.update {
                it.copy(errorMessage = "Selected range is too short.")
            }
            return
        }

        val range = VideoSplitRange(
            id = "range_1",
            startMs = current.rangeStartMs,
            endMs = current.rangeEndMs
        )

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSplitting = true,
                    splitProgress = 0,
                    errorMessage = null,
                    saveSuccessMessage = null
                )
            }

            runCatching {
                useCases.startSplit(
                    inputUri = inputUri,
                    fileName = current.selectedVideoName.ifBlank {
                        "selected_video.mp4"
                    },
                    ranges = listOf(range)
                )
            }.onSuccess { workId ->
                _state.update {
                    it.copy(
                        currentJobId = workId
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSplitting = false,
                        splitProgress = 0,
                        errorMessage = throwable.message
                            ?: "Unable to start video splitting."
                    )
                }
            }
        }
    }

    private fun saveClip(clipId: String) {
        val clip = _state.value.resultClips.firstOrNull { it.id == clipId }

        if (clip == null) {
            _state.update {
                it.copy(
                    errorMessage = "Clip not found.",
                    saveSuccessMessage = null
                )
            }
            return
        }

        if (clip.isSaved) {
            _state.update {
                it.copy(
                    saveSuccessMessage = "Clip already saved.",
                    errorMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSavingClip = true,
                    savingClipId = clipId,
                    errorMessage = null,
                    saveSuccessMessage = null
                )
            }

            runCatching {
                useCases.saveClipToGallery(clip)
            }.onSuccess { savedUri ->
                _state.update { current ->
                    current.copy(
                        isSavingClip = false,
                        savingClipId = null,
                        resultClips = current.resultClips.map { item ->
                            if (item.id == clipId) {
                                item.copy(savedUri = savedUri)
                            } else {
                                item
                            }
                        },
                        saveSuccessMessage = "Clip saved to Movies/VideoSplitter.",
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSavingClip = false,
                        savingClipId = null,
                        errorMessage = throwable.message
                            ?: "Unable to save this clip.",
                        saveSuccessMessage = null
                    )
                }
            }
        }
    }

    private fun cancelSplit(id: String) {
        viewModelScope.launch {
            useCases.cancelSplit(id)
        }
    }

    private companion object {
        private const val MIN_SPLIT_DURATION_MS = 1_000L
    }
}