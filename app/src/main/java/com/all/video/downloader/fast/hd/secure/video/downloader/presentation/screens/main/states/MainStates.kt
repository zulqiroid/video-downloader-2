package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states

 import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem



data class MainStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedTab: BottomNavItems = BottomNavItems.Home,
    val totalTabs: List<BottomNavItems> = getBottomNavItems(),

    val showPlayerDialogue: Boolean = false,
    val playerMediaItem: MediaItem? = null,

    val renameMediaItem: MediaItem? = null,
    val renameDraftName: String = "",
    val renameError: String? = null,
    val isRenaming: Boolean = false,

    val fileInfoItem: MediaItem? = null,

    val deleteMediaItem: MediaItem? = null,
    val isDeleting: Boolean = false,

    val isMovingToVault: Boolean = false,

    val pendingMediaStoreAction: PendingMediaStoreAction? = null,

    val showPremiumLabel: Boolean = false,
)