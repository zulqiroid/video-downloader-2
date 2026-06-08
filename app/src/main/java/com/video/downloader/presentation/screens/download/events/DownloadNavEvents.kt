package com.video.downloader.presentation.screens.download.events

sealed interface DownloadNavEvents {

    data object NavigateToSettingScreen : DownloadNavEvents
}