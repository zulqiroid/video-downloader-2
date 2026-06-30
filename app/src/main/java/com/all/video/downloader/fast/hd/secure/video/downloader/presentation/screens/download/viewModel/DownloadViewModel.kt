package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.mapper.toMediaFiles
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaType
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.DownloadUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events.DownloadEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events.DownloadNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadConfirmationAction
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadConfirmationState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadUseCases: DownloadUseCases
) : ViewModel() {

    private val completedPaginationMutex = Mutex()

    private val _state = MutableStateFlow(DownloadStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<DownloadNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        observeActiveDownloads()
        observeCompletedFolderChanges()
    }

    fun onEvents(event: DownloadEvents) {
        when (event) {
            is DownloadEvents.OnPauseDownloadingClicked -> {
                pauseDownload(event.id)
            }

            is DownloadEvents.OnResumeDownloadingClicked -> {
                resumeDownload(event.id)
            }

            is DownloadEvents.OnCancelDownloadRequested -> {
                showConfirmation(
                    item = event.item,
                    action = DownloadConfirmationAction.CANCEL_DOWNLOAD
                )
            }

            is DownloadEvents.OnDeleteFileRequested -> {
                showConfirmation(
                    item = event.item,
                    action = DownloadConfirmationAction.DELETE_FILE
                )
            }

            DownloadEvents.OnConfirmActionClicked -> {
                confirmCurrentAction()
            }

            DownloadEvents.OnConfirmActionDismissed -> {
                _state.update { current ->
                    current.copy(confirmation = null)
                }
            }

            is DownloadEvents.OnDownloadMoreClicked -> {
                _state.update { current ->
                    current.copy(selectedDownloadItem = event.item)
                }
            }

            DownloadEvents.OnDownloadMenuDismissed -> {
                _state.update { current ->
                    current.copy(selectedDownloadItem = null)
                }
            }

            is DownloadEvents.OnFileInfoClicked -> {
                _state.update { current ->
                    current.copy(
                        selectedDownloadItem = null,
                        fileInfoItem = event.item
                    )
                }
            }

            DownloadEvents.OnFileInfoDismissed -> {
                _state.update { current ->
                    current.copy(fileInfoItem = null)
                }
            }

            is DownloadEvents.OnCompletedMediaClicked -> {
                openCompletedMedia(event.itemId)
            }

            DownloadEvents.OnLoadNextCompletedFiles -> {
                loadNextCompletedFiles()
            }

            is DownloadEvents.OnTabChange -> {
                _state.update { current ->
                    current.copy(selectedTab = event.tab)
                }
            }

            DownloadEvents.OnSettingClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(DownloadNavEvents.NavigateToSettingScreen)
                }
            }
        }
    }

    private fun observeActiveDownloads() {
        viewModelScope.launch {
            downloadUseCases.observeDownloads().collect { items ->
                val activeDownloads = items.filterActiveDownloads()

                _state.update { current ->
                    current.copy(
                        downloading = activeDownloads,
                        selectedDownloadItem = current.selectedDownloadItem?.refreshFrom(
                            activeDownloads + current.completed
                        ),
                        fileInfoItem = current.fileInfoItem?.refreshFrom(
                            activeDownloads + current.completed
                        ),
                        confirmation = current.confirmation?.refreshFrom(
                            activeDownloads + current.completed
                        )
                    )
                }
            }
        }
    }

    private fun observeCompletedFolderChanges() {
        viewModelScope.launch {
            downloadUseCases.observeDownloadedFileChanges().collect {
                refreshCompletedVisibleWindow()
            }
        }
    }

    private fun refreshCompletedVisibleWindow() {
        viewModelScope.launch {
            if (!completedPaginationMutex.tryLock()) {
                return@launch
            }

            try {
                val currentCompletedCount = _state.value.completed.size

                val visibleLimit = maxOf(
                    COMPLETED_PAGE_SIZE,
                    currentCompletedCount
                )

                val requestLimit = visibleLimit + EXTRA_ITEM_FOR_HAS_MORE

                _state.update { current ->
                    current.copy(
                        isCompletedInitialLoading = current.completed.isEmpty(),
                        isCompletedPageLoading = false,
                        completedErrorMessage = null
                    )
                }

                val rawItems = downloadUseCases.getDownloadedFilesPage(
                    offset = 0,
                    limit = requestLimit
                )

                val hasMore = rawItems.size > visibleLimit

                val completedFiles = rawItems
                    .take(visibleLimit)
                    .filterCompleted()
                    .distinctBy { item -> item.filePath }

                _state.update { current ->
                    current.copy(
                        completed = completedFiles,
                        hasMoreCompleted = hasMore,
                        isCompletedInitialLoading = false,
                        isCompletedPageLoading = false,
                        completedErrorMessage = null,
                        selectedDownloadItem = current.selectedDownloadItem?.refreshFrom(
                            current.downloading + completedFiles
                        ),
                        fileInfoItem = current.fileInfoItem?.refreshFrom(
                            current.downloading + completedFiles
                        ),
                        confirmation = current.confirmation?.refreshFrom(
                            current.downloading + completedFiles
                        )
                    )
                }
            } catch (throwable: Throwable) {
                _state.update { current ->
                    current.copy(
                        isCompletedInitialLoading = false,
                        isCompletedPageLoading = false,
                        completedErrorMessage = throwable.message
                    )
                }
            } finally {
                completedPaginationMutex.unlock()
            }
        }
    }

    private fun loadNextCompletedFiles() {
        viewModelScope.launch {
            val snapshot = _state.value

            if (snapshot.isCompletedInitialLoading ||
                snapshot.isCompletedPageLoading ||
                !snapshot.hasMoreCompleted
            ) {
                return@launch
            }

            if (!completedPaginationMutex.tryLock()) {
                return@launch
            }

            try {
                val offset = _state.value.completed.size

                _state.update { current ->
                    current.copy(
                        isCompletedPageLoading = true,
                        completedErrorMessage = null
                    )
                }

                val rawItems = downloadUseCases.getDownloadedFilesPage(
                    offset = offset,
                    limit = COMPLETED_PAGE_SIZE + EXTRA_ITEM_FOR_HAS_MORE
                )

                val hasMore = rawItems.size > COMPLETED_PAGE_SIZE

                val pageItems = rawItems
                    .take(COMPLETED_PAGE_SIZE)
                    .filterCompleted()

                _state.update { current ->
                    val mergedCompleted = (current.completed + pageItems)
                        .distinctBy { item -> item.filePath }

                    current.copy(
                        completed = mergedCompleted,
                        hasMoreCompleted = hasMore,
                        isCompletedPageLoading = false,
                        isCompletedInitialLoading = false,
                        completedErrorMessage = null,
                        selectedDownloadItem = current.selectedDownloadItem?.refreshFrom(
                            current.downloading + mergedCompleted
                        ),
                        fileInfoItem = current.fileInfoItem?.refreshFrom(
                            current.downloading + mergedCompleted
                        ),
                        confirmation = current.confirmation?.refreshFrom(
                            current.downloading + mergedCompleted
                        )
                    )
                }
            } catch (throwable: Throwable) {
                _state.update { current ->
                    current.copy(
                        isCompletedPageLoading = false,
                        completedErrorMessage = throwable.message
                    )
                }
            } finally {
                completedPaginationMutex.unlock()
            }
        }
    }

    private fun openCompletedMedia(itemId: Long) {
        viewModelScope.launch {
            val playableCompletedItems = _state.value.completed
                .filter { item ->
                    item.isPlayable &&
                            item.mediaType == MediaType.VIDEO &&
                            item.filePath.isNotBlank() &&
                            item.downloadStatus == DownloadStatus.SUCCESS
                }
                .distinctBy { item -> item.filePath }

            val startIndex = playableCompletedItems.indexOfFirst { item ->
                item.id == itemId
            }

            if (startIndex == -1) {
                return@launch
            }

            _navEvents.emit(
                DownloadNavEvents.SendToMedia(
                    mediaList = playableCompletedItems.toMediaFiles(),
                    startIndex = startIndex
                )
            )
        }
    }

    private fun pauseDownload(id: Long) {
        viewModelScope.launch {
            closeDownloadMenu()
            downloadUseCases.pauseDownload(id)
        }
    }

    private fun resumeDownload(id: Long) {
        viewModelScope.launch {
            closeDownloadMenu()
            downloadUseCases.resumeDownload(id)
        }
    }

    private fun showConfirmation(
        item: MediaItem,
        action: DownloadConfirmationAction,
    ) {
        _state.update { current ->
            current.copy(
                selectedDownloadItem = null,
                confirmation = DownloadConfirmationState(
                    item = item,
                    action = action
                )
            )
        }
    }

    private fun confirmCurrentAction() {
        val confirmation = _state.value.confirmation ?: return

        viewModelScope.launch {
            removeDownloadCompletely(id = confirmation.item.id)
        }
    }

    private suspend fun removeDownloadCompletely(id: Long) {
        downloadUseCases.cancelDownload(id)

        _state.update { current ->
            current.copy(
                downloading = current.downloading.filterNot { item -> item.id == id },
                completed = current.completed.filterNot { item -> item.id == id },
                selectedDownloadItem = null,
                fileInfoItem = current.fileInfoItem?.takeIf { item -> item.id != id },
                confirmation = null
            )
        }
    }

    private fun closeDownloadMenu() {
        _state.update { current ->
            current.copy(selectedDownloadItem = null)
        }
    }

    private fun MediaItem.refreshFrom(items: List<MediaItem>): MediaItem? {
        return items.firstOrNull { item ->
            item.id == id
        } ?: this
    }

    private fun DownloadConfirmationState.refreshFrom(
        items: List<MediaItem>
    ): DownloadConfirmationState {
        val freshItem = items.firstOrNull { item ->
            item.id == this.item.id
        } ?: this.item

        return copy(item = freshItem)
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
            item.downloadStatus == DownloadStatus.SUCCESS &&
                    item.filePath.isNotBlank()
        }
    }

    private companion object {
        private const val COMPLETED_PAGE_SIZE = 20
        private const val EXTRA_ITEM_FOR_HAS_MORE = 1
    }
}