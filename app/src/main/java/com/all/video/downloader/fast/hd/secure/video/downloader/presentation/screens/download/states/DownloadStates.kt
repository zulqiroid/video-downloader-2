package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem

data class DownloadStates(
    val isDownloading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: DownloadTab = DownloadTab.COMPLETED,
    val downloading: List<MediaItem> = emptyList(),
    val completed: List<MediaItem> = emptyList(),
    val selectedDownloadItem: MediaItem? = null,
    val fileInfoItem: MediaItem? = null,
    val confirmation: DownloadConfirmationState? = null,

    val isCompletedInitialLoading: Boolean = false,
    val isCompletedPageLoading: Boolean = false,
    val hasMoreCompleted: Boolean = true,
    val completedErrorMessage: String? = null,
)