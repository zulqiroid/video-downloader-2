package com.video.downloader.presentation.screens.player.states

import com.video.downloader.domain.models.MediaFile
import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.models.PlayerUiItem

data class PlayerStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedTab: PlayerTab = PlayerTab.VIDEO,
    val videos: List<MediaItem> = emptyList(),
    val audios: List<MediaItem> = emptyList(),

    val isMediaPermissionGranted: Boolean = false,
    val showMediaPermissionDialog: Boolean = false,
    val shouldOpenMediaPermissionSettings: Boolean = false,


    )
