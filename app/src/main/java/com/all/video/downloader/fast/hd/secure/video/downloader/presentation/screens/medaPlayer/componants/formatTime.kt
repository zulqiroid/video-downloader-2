package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
