package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaFile(
    val id: Long,
    val filePath: String,
    val fileName: String,
    val isVideo: Boolean,
    val contentUri: String
)