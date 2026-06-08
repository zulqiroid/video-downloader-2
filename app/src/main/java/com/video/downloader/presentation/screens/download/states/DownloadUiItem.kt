package com.video.downloader.presentation.screens.download.states

import com.video.downloader.domain.models.DownloadStatus

data class DownloadUiItem(
    val id: Long,
    val title: String,
    val progress: Int,
    val status: DownloadStatus,
    val sizeText: String = "",
    val timeText: String = "",
    val filePath: String? = null
)