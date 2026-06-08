package com.video.downloader.presentation.screens.player.events

import com.video.downloader.domain.models.MediaFile

sealed interface PlayerNavEvents {
    data class SendToMedia(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : PlayerNavEvents

    data object NavigateToSettingScreen : PlayerNavEvents
}