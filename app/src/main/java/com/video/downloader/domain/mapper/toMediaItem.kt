package com.video.downloader.domain.mapper

import com.video.downloader.domain.models.DownloadItem
import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.models.MediaSource
import com.video.downloader.domain.models.MediaType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun DownloadItem.toMediaItem(): MediaItem {
    val file = File(filePath)
    val finalSizeBytes = when {
        totalBytes > 0L -> totalBytes
        downloadedBytes > 0L -> downloadedBytes
        file.exists() -> file.length()
        else -> 0L
    }

    return MediaItem(
        id = id,
        mediaStoreId = null,

        title = fileName.removeFileExtension(),
        displayName = fileName,
        fileName = fileName,

        uri = file.toURI().toString(),
        filePath = filePath,
        relativePath = "",
        parentPath = file.parent.orEmpty(),
        extension = fileName.fileExtension(),
        mimeType = guessMimeType(fileName),

        mediaType = MediaType.VIDEO,
        mediaSource = MediaSource.DOWNLOAD,

        sizeBytes = finalSizeBytes,
        formattedSize = formatFileSize(finalSizeBytes),

        durationMillis = 0L,
        formattedDuration = "",

        width = 0,
        height = 0,
        resolution = "",
        qualityLabel = "",
        frameRate = null,
        bitrate = null,
        rotation = null,

        dateAddedSeconds = if (file.exists()) file.lastModified() / 1000L else 0L,
        dateModifiedSeconds = if (file.exists()) file.lastModified() / 1000L else 0L,
        formattedDate = if (file.exists()) formatDate(file.lastModified()) else "",

        url = url,
        progress = progress,
        downloadStatus = status,
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes,
        speedBytesPerSecond = speedBytesPerSec,
        etaSeconds = lastEtaSeconds,
        formattedEta = formatEta(lastEtaSeconds),
        workId = workId,

        isPlayable = filePath.isNotBlank(),
        isDownloaded = status.name == "SUCCESS",

        extra = mapOf(
            "downloadStatus" to status.name,
            "sourceUrl" to url
        )
    )
}

fun List<DownloadItem>.toMediaItems(): List<MediaItem> {
    return map { item ->
        item.toMediaItem()
    }
}

private fun String.removeFileExtension(): String {
    if (isBlank()) return ""
    return substringBeforeLast(
        delimiter = ".",
        missingDelimiterValue = this
    )
}

private fun String.fileExtension(): String {
    if (isBlank()) return ""
    return substringAfterLast(
        delimiter = ".",
        missingDelimiterValue = ""
    ).lowercase(Locale.getDefault())
}

private fun guessMimeType(fileName: String): String {
    return when (fileName.fileExtension()) {
        "mp4" -> "video/mp4"
        "mkv" -> "video/x-matroska"
        "webm" -> "video/webm"
        "avi" -> "video/x-msvideo"
        "mov" -> "video/quicktime"
        "mp3" -> "audio/mpeg"
        "m4a" -> "audio/mp4"
        "wav" -> "audio/wav"
        else -> ""
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "0 B"

    val kb = 1024.0
    val mb = kb * 1024.0
    val gb = mb * 1024.0

    return when {
        bytes >= gb -> String.format(Locale.US, "%.2f GB", bytes / gb)
        bytes >= mb -> String.format(Locale.US, "%.2f MB", bytes / mb)
        bytes >= kb -> String.format(Locale.US, "%.2f KB", bytes / kb)
        else -> "$bytes B"
    }
}

private fun formatEta(seconds: Long): String {
    if (seconds < 0L) return ""
    if (seconds == 0L) return "Completed"

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return when {
        hours > 0L -> "${hours}h ${minutes}m left"
        minutes > 0L -> "${minutes}m ${remainingSeconds}s left"
        else -> "${remainingSeconds}s left"
    }
}

private fun formatDate(millis: Long): String {
    if (millis <= 0L) return ""

    return try {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formatter.format(Date(millis))
    } catch (_: Throwable) {
        ""
    }
}