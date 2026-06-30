package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states

data class VideoOptionsState(
    val fileName: String = "",
    val fileMeta: String = "", // "1.2 GB • 1080p • 02:14:30"
    val playbackSpeed: String = "1x"
)