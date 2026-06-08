package com.video.downloader.presentation.screens.videoSplitter.events

sealed interface VideoSplitterNavEvents {

    data object NavigateBack : VideoSplitterNavEvents

    data object LaunchVideoPicker : VideoSplitterNavEvents
}