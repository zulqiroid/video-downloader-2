package com.video.downloader.presentation.screens.download.events

import com.video.downloader.presentation.screens.download.states.DownloadTab

sealed interface DownloadEvents {

    data class OnTabChange(val tab: DownloadTab) : DownloadEvents

    data class OnDeleteDownloadingClicked(val id: Long) : DownloadEvents
    data class OnPauseDownloadingClicked(val id: Long) : DownloadEvents
    data class OnResumeDownloadingClicked(val id: Long) : DownloadEvents

    data object OnSettingClicked : DownloadEvents
}