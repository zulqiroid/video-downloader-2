package com.video.downloader.presentation.screens.videoToMp3.events

sealed interface VideoToMp3NavEvents {

    data object NavigateBack : VideoToMp3NavEvents

    data object LaunchVideoPicker : VideoToMp3NavEvents

    data class NavigateToResult(
        val conversionId: String
    ) : VideoToMp3NavEvents
}