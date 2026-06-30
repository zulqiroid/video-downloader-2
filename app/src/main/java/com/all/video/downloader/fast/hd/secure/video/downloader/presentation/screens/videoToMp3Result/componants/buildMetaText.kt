package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem
import java.util.Locale

fun buildMetaText(
    item: VideoToMp3ConversionItem
): String {
    val bitrate = item.resolvedConfig?.bitrateKbps?.let {
        "${it}kbps"
    } ?: "MP3"

    val size = formatFileSize(item.fileSizeBytes)

    return if (size == "-") {
        bitrate
    } else {
        "$bitrate  •  $size"
    }
}

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "-"

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