package com.video.downloader.presentation.screens.download.states

import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.models.PlayerUiItem

data class DownloadStates(
    val isDownloading: Boolean = false,
    val errorMessage: String? = null,

    val selectedTab: DownloadTab = DownloadTab.COMPLETED,
    val downloading: List<MediaItem> = emptyList(),
    val completed: List<MediaItem> = emptyList(),

    )


