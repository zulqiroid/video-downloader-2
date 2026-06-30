package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.vault

data class VaultFile(
    val id: String,
    val originalUri: String,
    val originalFileName: String,
    val mimeType: String,
    val fileType: VaultFileType,
    val originalSizeBytes: Long,
    val encryptedFilePath: String,
    val thumbnailPath: String?,
    val ivBase64: String,
    val durationMillis: Long,
    val createdAtMillis: Long,
    val originalDeleteStatus: VaultOriginalDeleteStatus
)

enum class VaultFileType {
    VIDEO,
    AUDIO
}

enum class VaultOriginalDeleteStatus {
    PENDING_DELETE,
    DELETED,
    DELETE_FAILED
}