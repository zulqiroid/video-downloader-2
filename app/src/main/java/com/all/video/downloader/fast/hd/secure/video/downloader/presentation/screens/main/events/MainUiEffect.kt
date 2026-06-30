package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem


import android.content.IntentSender

sealed interface MainUiEffect {

    data class ShareMediaItem(
        val item: MediaItem
    ) : MainUiEffect

    data class RequestMediaStoreApproval(
        val intentSender: IntentSender,
        val message: String
    ) : MainUiEffect

    data class ShowMessage(
        val message: String
    ) : MainUiEffect
}