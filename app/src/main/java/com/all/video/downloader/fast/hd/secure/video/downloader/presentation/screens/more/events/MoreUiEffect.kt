package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events

sealed interface MoreUiEffect {

    data object ShareApp : MoreUiEffect

    data object OpenRatePage : MoreUiEffect

    data object OpenCustomFolderPicker : MoreUiEffect

    data class SendFeedbackEmail(
        val toEmail: String,
        val subject: String,
        val body: String,
        val chooserTitle: String = "Send feedback"
    ) : MoreUiEffect

    data class OpenPrivacyPolicy(
        val url: String
    ) : MoreUiEffect

    data class ShowMessage(
        val message: String
    ) : MoreUiEffect
}