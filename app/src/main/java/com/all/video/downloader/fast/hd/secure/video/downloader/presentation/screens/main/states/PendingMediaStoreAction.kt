package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem


sealed interface PendingMediaStoreAction {

    data class Rename(
        val item: MediaItem,
        val newNameWithoutExtension: String
    ) : PendingMediaStoreAction

    data class Delete(
        val item: MediaItem
    ) : PendingMediaStoreAction

    /**
     * Move to vault mein file already vault mein copy/encrypt ho chuki hoti hai.
     * Approval ke baad sirf original gallery file delete karni hoti hai.
     */
    data class DeleteOriginalAfterVault(
        val item: MediaItem
    ) : PendingMediaStoreAction
}