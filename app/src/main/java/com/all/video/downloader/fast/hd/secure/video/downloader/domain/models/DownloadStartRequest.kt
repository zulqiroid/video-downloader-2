package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models

data class DownloadStartRequest(
    val sourceUrl: String,
    val downloadUrl: String,
    val title: String,
    val platform: String,
    val quality: String,
    val thumbnailUrl: String? = null,
)