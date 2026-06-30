package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.events

sealed interface VideoSplitterNavEvents {

    data object NavigateBack : VideoSplitterNavEvents

    data object LaunchVideoPicker : VideoSplitterNavEvents
}