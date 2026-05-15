package com.video.downloader.presentation.screens.downloadGuide.events

sealed interface DownloadGuideNavEvents {

    data object NavigateBack : DownloadGuideNavEvents

    data object NavigateToHome : DownloadGuideNavEvents
}