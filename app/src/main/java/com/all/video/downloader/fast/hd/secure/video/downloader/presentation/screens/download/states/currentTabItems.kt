package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.PlayerUiItem

fun DownloadStates.currentTabItems(): List<MediaItem> {
    return when (selectedTab) {
        DownloadTab.IN_PROGRESS -> downloading
        DownloadTab.COMPLETED -> completed
    }
}