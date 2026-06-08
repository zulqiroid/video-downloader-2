package com.video.downloader.presentation.screens.download.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.domain.models.DownloadStatus
import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.usecases.DownloadUseCases
import com.video.downloader.presentation.screens.download.events.DownloadEvents
import com.video.downloader.presentation.screens.download.events.DownloadNavEvents
import com.video.downloader.presentation.screens.download.states.DownloadStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadUseCases: DownloadUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(DownloadStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<DownloadNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        loadLocalFiles()
        observeDownloads()
    }

    fun onEvents(event: DownloadEvents) {
        when (event) {
            is DownloadEvents.OnDeleteDownloadingClicked -> {
                deleteDownload(event.id)
            }

            is DownloadEvents.OnPauseDownloadingClicked -> {
                pauseDownload(event.id)
            }

            is DownloadEvents.OnResumeDownloadingClicked -> {
                resumeDownload(event.id)
            }

            is DownloadEvents.OnTabChange -> {
                _state.update { current ->
                    current.copy(
                        selectedTab = event.tab
                    )
                }
            }

            DownloadEvents.OnSettingClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(DownloadNavEvents.NavigateToSettingScreen)
                }
            }
        }
    }

    private fun loadLocalFiles() {
        viewModelScope.launch {
            val completedFiles: List<MediaItem> = downloadUseCases.getDownloadedFiles()

            _state.update { current ->
                current.copy(
                    completed = completedFiles
                        .filterCompleted()
                        .distinctBy { item -> item.id }
                )
            }
        }
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            downloadUseCases.observeDownloads().collect { items ->
                val downloading = items.filterActiveDownloads()
                val completedFromActiveDownloads = items.filterCompleted()

                _state.update { current ->
                    val mergedCompleted = (current.completed + completedFromActiveDownloads)
                        .distinctBy { item -> item.id }

                    current.copy(
                        downloading = downloading,
                        completed = mergedCompleted
                    )
                }
            }
        }
    }

    private fun pauseDownload(id: Long) {
        viewModelScope.launch {
            downloadUseCases.pauseDownload(id)
        }
    }

    private fun resumeDownload(id: Long) {
        viewModelScope.launch {
            downloadUseCases.resumeDownload(id)
        }
    }

    private fun deleteDownload(id: Long) {
        viewModelScope.launch {
            downloadUseCases.cancelDownload(id)

            _state.update { current ->
                current.copy(
                    downloading = current.downloading.filterNot { item ->
                        item.id == id
                    },
                    completed = current.completed.filterNot { item ->
                        item.id == id
                    }
                )
            }
        }
    }

    private fun List<MediaItem>.filterActiveDownloads(): List<MediaItem> {
        return filter { item ->
            item.downloadStatus == DownloadStatus.DOWNLOADING ||
                    item.downloadStatus == DownloadStatus.PAUSED ||
                    item.downloadStatus == DownloadStatus.FAILED
        }
    }

    private fun List<MediaItem>.filterCompleted(): List<MediaItem> {
        return filter { item ->
            item.downloadStatus == DownloadStatus.SUCCESS ||
                    item.isDownloaded
        }
    }
}