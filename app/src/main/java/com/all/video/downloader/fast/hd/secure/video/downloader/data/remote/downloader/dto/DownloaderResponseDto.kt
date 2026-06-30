package com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.downloader.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DownloaderResponseDto(
    @SerialName("status")
    val status: Boolean = false,

    @SerialName("title")
    val title: String? = null,

    @SerialName("downloadables")
    val downloadables: List<DownloadableDto> = emptyList(),

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("platform")
    val platform: String? = null
)

@Serializable
data class DownloadableDto(
    @SerialName("quality")
    val quality: String? = null,

    @SerialName("url")
    val url: String? = null
)