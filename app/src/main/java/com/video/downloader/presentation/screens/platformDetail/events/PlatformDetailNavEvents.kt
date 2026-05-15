package com.video.downloader.presentation.screens.platformDetail.events

sealed interface PlatformDetailNavEvents {

    data object NavigateBack : PlatformDetailNavEvents
}