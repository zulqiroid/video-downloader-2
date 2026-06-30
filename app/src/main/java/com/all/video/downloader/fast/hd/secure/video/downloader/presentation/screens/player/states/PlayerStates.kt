package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem

data class PlayerStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedTab: PlayerTab = PlayerTab.VIDEO,
    val videos: List<MediaItem> = emptyList(),
    val audios: List<MediaItem> = emptyList(),

    val isVideoInitialLoading: Boolean = false,
    val isAudioInitialLoading: Boolean = false,

    val isVideoPageLoading: Boolean = false,
    val isAudioPageLoading: Boolean = false,

    val hasMoreVideos: Boolean = true,
    val hasMoreAudios: Boolean = true,

    val videoErrorMessage: String? = null,
    val audioErrorMessage: String? = null,

    val isMediaPermissionGranted: Boolean = false,
    val showMediaPermissionDialog: Boolean = false,
    val shouldOpenMediaPermissionSettings: Boolean = false,
)