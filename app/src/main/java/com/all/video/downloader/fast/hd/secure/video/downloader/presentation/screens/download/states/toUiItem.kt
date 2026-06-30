package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadItem

fun DownloadItem.toUiItem(): DownloadUiItem {

    val totalMB = totalBytes / (1024f * 1024f)
    val formattedSize = String.format("%.2f", totalMB)

    return DownloadUiItem(
        id = id,
        title = fileName,
        progress = progress,
        status = status,
        sizeText = "$formattedSize MB",
        timeText = "Completed",
        filePath = filePath
    )
}