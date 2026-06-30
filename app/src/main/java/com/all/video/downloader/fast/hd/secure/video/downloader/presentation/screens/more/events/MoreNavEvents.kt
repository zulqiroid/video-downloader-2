package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events

sealed interface MoreNavEvents {
    data object NavigateBack : MoreNavEvents
    data object NavigateToLanguage : MoreNavEvents
    data object NavigateToDownloadGuide : MoreNavEvents
}