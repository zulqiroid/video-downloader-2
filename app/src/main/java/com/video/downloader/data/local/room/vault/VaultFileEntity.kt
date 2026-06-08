package com.video.downloader.data.local.room.vault

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.video.downloader.domain.models.vault.VaultFile
import com.video.downloader.domain.models.vault.VaultFileType
import com.video.downloader.domain.models.vault.VaultOriginalDeleteStatus

@Entity(tableName = "vault_files")
data class VaultFileEntity(
    @PrimaryKey
    val id: String,
    val originalUri: String,
    val originalFileName: String,
    val mimeType: String,
    val fileType: String,
    val originalSizeBytes: Long,
    val encryptedFilePath: String,
    val thumbnailPath: String?,
    val ivBase64: String,
    val durationMillis: Long,
    val createdAtMillis: Long,
    val originalDeleteStatus: String
)

fun VaultFileEntity.toDomain(): VaultFile {
    return VaultFile(
        id = id,
        originalUri = originalUri,
        originalFileName = originalFileName,
        mimeType = mimeType,
        fileType = runCatching {
            VaultFileType.valueOf(fileType)
        }.getOrDefault(VaultFileType.VIDEO),
        originalSizeBytes = originalSizeBytes,
        encryptedFilePath = encryptedFilePath,
        thumbnailPath = thumbnailPath,
        ivBase64 = ivBase64,
        durationMillis = durationMillis,
        createdAtMillis = createdAtMillis,
        originalDeleteStatus = runCatching {
            VaultOriginalDeleteStatus.valueOf(originalDeleteStatus)
        }.getOrDefault(VaultOriginalDeleteStatus.DELETE_FAILED)
    )
}