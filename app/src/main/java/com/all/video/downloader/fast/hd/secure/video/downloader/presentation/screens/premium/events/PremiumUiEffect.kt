package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events

sealed interface PremiumUiEffect {

    data class ShowMessage(
        val message: String
    ) : PremiumUiEffect

    data class OpenUrl(
        val url: String,
        val fallbackMessage: String
    ) : PremiumUiEffect
}