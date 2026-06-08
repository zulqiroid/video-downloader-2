package com.video.downloader.presentation.screens.videoToMp3Result.events

sealed interface VideoToMp3ResultEvents {
    data object BackClicked : VideoToMp3ResultEvents
    data object SaveClicked : VideoToMp3ResultEvents
}