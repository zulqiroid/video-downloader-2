package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states.PlayerTab

sealed interface PlayerEvents {

    data class OnMediaPermissionResult(
        val granted: Boolean,
        val permanentlyDenied: Boolean
    ) : PlayerEvents

    data class SendToMedia(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : PlayerEvents

    data class OnLoadNextMediaPage(
        val tab: PlayerTab
    ) : PlayerEvents

    data object OnSettingClicked : PlayerEvents
}