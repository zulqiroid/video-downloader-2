package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models

data class PlayerUiItem(
        val id: Long,
        val title: String,
        val duration: String,
        val size: String,
        val quality: String,
        val filePath: String,
        val date: String
    )