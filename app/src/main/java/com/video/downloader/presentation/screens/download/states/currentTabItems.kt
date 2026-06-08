package com.video.downloader.presentation.screens.download.states

import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.models.PlayerUiItem

fun DownloadStates.currentTabItems(): List<MediaItem> {
    return when (selectedTab) {
        DownloadTab.IN_PROGRESS -> downloading
        DownloadTab.COMPLETED -> completed
    }
}