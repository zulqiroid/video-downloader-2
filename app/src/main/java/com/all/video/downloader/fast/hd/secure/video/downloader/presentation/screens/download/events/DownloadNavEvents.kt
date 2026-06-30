package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile

sealed interface DownloadNavEvents {

    data object NavigateToSettingScreen : DownloadNavEvents

    data class SendToMedia(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : DownloadNavEvents
}