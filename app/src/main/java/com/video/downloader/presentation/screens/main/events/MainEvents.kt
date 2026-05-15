package com.video.downloader.presentation.screens.main.events

import com.video.downloader.presentation.screens.main.states.BottomNavItems

sealed interface MainEvents {

    data class OnTabSelected(val tab: BottomNavItems) : MainEvents

}