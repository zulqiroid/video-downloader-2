package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.PlayerUiItem

fun PlayerStates.currentTabItems(): List<MediaItem> {
        return when (selectedTab) {
            PlayerTab.VIDEO -> videos
            PlayerTab.AUDIO -> audios
        }
    }
