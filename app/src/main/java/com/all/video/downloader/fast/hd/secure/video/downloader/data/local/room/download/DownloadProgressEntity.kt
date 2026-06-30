package com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "download_progress",
    indices = [
        Index(value = ["status"]),
        Index(value = ["updatedAtMillis"])
    ]
)
data class DownloadProgressEntity(
    @PrimaryKey
    val id: Long,

    // Direct downloadable URL.
    val url: String,

    // Original pasted/source URL.
    val sourceUrl: String,

    val fileName: String,
    val filePath: String,

    val progress: Int,
    val status: String,

    val downloadedBytes: Long,
    val totalBytes: Long,
    val speedBytesPerSec: Long,
    val lastEtaSeconds: Long,

    val workId: String?,

    val platform: String,
    val quality: String,
    val thumbnailUrl: String?,

    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)