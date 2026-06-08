package com.video.downloader.presentation.screens.player.events

import com.video.downloader.domain.models.MediaFile

sealed interface PlayerEvents {

    data class OnMediaPermissionResult(
        val granted: Boolean,
        val permanentlyDenied: Boolean
    ) : PlayerEvents

    data class SendToMedia(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ): PlayerEvents

    data object OnSettingClicked : PlayerEvents
}