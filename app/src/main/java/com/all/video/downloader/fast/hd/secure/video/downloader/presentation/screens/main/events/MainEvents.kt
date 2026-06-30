package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.BottomNavItems


sealed interface MainEvents {

    data class OnTabSelected(
        val tab: BottomNavItems
    ) : MainEvents

    data class OnMoreClicked(
        val mediaFile: MediaItem
    ) : MainEvents

    data class OnRenameMediaRequested(
        val item: MediaItem
    ) : MainEvents

    data class OnMoveMediaToVaultRequested(
        val item: MediaItem
    ) : MainEvents

    data class OnShareMediaRequested(
        val item: MediaItem
    ) : MainEvents

    data class OnFileInfoRequested(
        val item: MediaItem
    ) : MainEvents

    data class OnDeleteMediaRequested(
        val item: MediaItem
    ) : MainEvents

    data class OnMediaStoreApprovalResult(
        val approved: Boolean
    ) : MainEvents

    data object OnOpenDownloadProgressFromNotification : MainEvents
}