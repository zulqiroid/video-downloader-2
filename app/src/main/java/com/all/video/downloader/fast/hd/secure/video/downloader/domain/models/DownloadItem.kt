package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models

data class DownloadItem(
    val id: Long,

    // Direct downloadable media URL jo worker download karega.
    val url: String,

    // Original link jo user ne paste kiya tha.
    val sourceUrl: String = url,

    val fileName: String,
    val filePath: String,
    val progress: Int,
    val status: DownloadStatus,

    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val speedBytesPerSec: Long = 0L,
    val lastEtaSeconds: Long = -1L,
    val workId: String? = null,

    val platform: String = "",
    val quality: String = "",
    val thumbnailUrl: String? = null,
)

enum class DownloadStatus {
    DOWNLOADING,
    PAUSED,
    SUCCESS,
    FAILED
}