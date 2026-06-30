package com.all.video.downloader.fast.hd.secure.video.downloader.domain.mapper

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.PlayerUiItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadUiItem

fun DownloadUiItem.toPlayerUiItem(): PlayerUiItem {
    return PlayerUiItem(
        id = id,
        title = title,
        duration = timeText,
        size = sizeText,
        quality = "",
        filePath = filePath.orEmpty(),
        date = ""
    )
}