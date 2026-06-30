package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

data class DownloadOptionItem(
    val id: DownloadOptionId,
    val title: Int,
    val description: Int,
    val icon: Int,
    val enabled: Boolean = true,
    val isDestructive: Boolean = false
)