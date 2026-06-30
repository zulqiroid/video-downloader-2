package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.events

sealed interface DownloadGuideEvents {

    data object BackClicked : DownloadGuideEvents

    data object TryNowClicked : DownloadGuideEvents
}