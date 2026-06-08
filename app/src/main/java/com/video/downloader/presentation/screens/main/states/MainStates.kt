package com.video.downloader.presentation.screens.main.states

import com.video.downloader.domain.models.MediaFile
import com.video.downloader.domain.models.MediaItem

data class MainStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedTab: BottomNavItems = BottomNavItems.Home,
    val totalTabs: List<BottomNavItems> = getBottomNavItems(),

    val showPlayerDialogue: Boolean = false,
    val playerMediaItem: MediaItem? = null,

    )
