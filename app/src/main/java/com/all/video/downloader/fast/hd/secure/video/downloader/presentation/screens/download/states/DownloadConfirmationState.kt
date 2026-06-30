package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem

data class DownloadConfirmationState(
    val item: MediaItem,
    val action: DownloadConfirmationAction
)

enum class DownloadConfirmationAction {
    CANCEL_DOWNLOAD,
    DELETE_FILE
}