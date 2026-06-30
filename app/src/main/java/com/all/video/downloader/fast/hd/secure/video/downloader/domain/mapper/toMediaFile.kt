package com.all.video.downloader.fast.hd.secure.video.downloader.domain.mapper

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaType

fun MediaItem.toMediaFile(): MediaFile {
    return MediaFile(
        id = id,
        filePath = filePath,
        fileName = fileName
            .ifBlank { displayName }
            .ifBlank { title },
        isVideo = mediaType == MediaType.VIDEO,
        contentUri = uri.ifBlank { filePath }
    )
}

fun List<MediaItem>.toMediaFiles(): List<MediaFile> {
    return map { item ->
        item.toMediaFile()
    }
}