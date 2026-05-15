package com.video.downloader.presentation.screens.platformDetail.events

sealed interface PlatformDetailEvents {

    data object BackClicked : PlatformDetailEvents

    data class VideoUrlChanged(
        val value: String
    ) : PlatformDetailEvents

    data class PasteClicked(
        val clipboardText: String
    ) : PlatformDetailEvents

    data object DownloadVideoClicked : PlatformDetailEvents

    data object SeeAllClicked : PlatformDetailEvents

    data class TrendingVideoClicked(
        val videoId: String
    ) : PlatformDetailEvents
}