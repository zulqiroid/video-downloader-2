package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events

sealed interface ReelsUiEffect {

    data class ShareReel(
        val title: String,
        val text: String,
        val chooserTitle: String = "Share reel"
    ) : ReelsUiEffect

    data class ShowMessage(
        val message: String
    ) : ReelsUiEffect
}