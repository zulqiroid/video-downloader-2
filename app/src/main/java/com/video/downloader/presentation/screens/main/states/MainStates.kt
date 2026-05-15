package com.video.downloader.presentation.screens.main.states

data class MainStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedTab: BottomNavItems = BottomNavItems.Home,
    val totalTabs: List<BottomNavItems> = getBottomNavItems()

    )
