package com.video.downloader.presentation.screens.player.states

import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.models.PlayerUiItem

fun PlayerStates.currentTabItems(): List<MediaItem> {
        return when (selectedTab) {
            PlayerTab.VIDEO -> videos
            PlayerTab.AUDIO -> audios
        }
    }
