package com.video.downloader.presentation.screens.main.events

import com.video.downloader.domain.models.MediaFile
import com.video.downloader.domain.models.MediaItem
import com.video.downloader.presentation.screens.main.states.BottomNavItems
import com.video.downloader.presentation.screens.player.events.PlayerEvents

sealed interface MainEvents {

    data class OnTabSelected(val tab: BottomNavItems) : MainEvents

    data class OnMoreClicked(
        val mediaFile: MediaItem
    ) : MainEvents


}