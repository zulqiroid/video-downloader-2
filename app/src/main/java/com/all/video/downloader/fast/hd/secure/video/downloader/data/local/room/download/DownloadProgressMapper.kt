package com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus

fun DownloadProgressEntity.toDomain(): DownloadItem {
    return DownloadItem(
        id = id,
        url = url,
        sourceUrl = sourceUrl,
        fileName = fileName,
        filePath = filePath,
        progress = progress.coerceIn(0, 100),
        status = status.toDownloadStatus(),
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes,
        speedBytesPerSec = speedBytesPerSec,
        lastEtaSeconds = lastEtaSeconds,
        workId = workId,
        platform = platform,
        quality = quality,
        thumbnailUrl = thumbnailUrl
    )
}

fun DownloadItem.toEntity(
    existingEntity: DownloadProgressEntity? = null,
): DownloadProgressEntity {
    val now = System.currentTimeMillis()

    return DownloadProgressEntity(
        id = id,
        url = url,
        sourceUrl = sourceUrl,
        fileName = fileName,
        filePath = filePath,
        progress = progress.coerceIn(0, 100),
        status = status.name,
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes,
        speedBytesPerSec = speedBytesPerSec,
        lastEtaSeconds = lastEtaSeconds,
        workId = workId,
        platform = platform,
        quality = quality,
        thumbnailUrl = thumbnailUrl,
        createdAtMillis = existingEntity?.createdAtMillis ?: now,
        updatedAtMillis = now
    )
}

private fun String.toDownloadStatus(): DownloadStatus {
    return runCatching {
        DownloadStatus.valueOf(this)
    }.getOrDefault(DownloadStatus.FAILED)
}