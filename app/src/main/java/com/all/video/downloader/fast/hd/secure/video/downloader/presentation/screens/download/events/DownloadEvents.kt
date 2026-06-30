package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadTab

sealed interface DownloadEvents {

    data class OnTabChange(val tab: DownloadTab) : DownloadEvents

    data class OnPauseDownloadingClicked(val id: Long) : DownloadEvents
    data class OnResumeDownloadingClicked(val id: Long) : DownloadEvents

    data class OnCancelDownloadRequested(val item: MediaItem) : DownloadEvents
    data class OnDeleteFileRequested(val item: MediaItem) : DownloadEvents

    data object OnConfirmActionClicked : DownloadEvents
    data object OnConfirmActionDismissed : DownloadEvents

    data class OnDownloadMoreClicked(val item: MediaItem) : DownloadEvents
    data object OnDownloadMenuDismissed : DownloadEvents

    data class OnFileInfoClicked(val item: MediaItem) : DownloadEvents
    data object OnFileInfoDismissed : DownloadEvents

    data class OnCompletedMediaClicked(val itemId: Long) : DownloadEvents

    data object OnLoadNextCompletedFiles : DownloadEvents

    data object OnSettingClicked : DownloadEvents
}