package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile

sealed interface PlayerNavEvents {
    data class SendToMedia(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : PlayerNavEvents

    data object NavigateToSettingScreen : PlayerNavEvents
}