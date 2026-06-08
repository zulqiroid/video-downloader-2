package com.video.downloader.data.repository.implementation.vault

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.video.downloader.data.local.room.vault.VaultFileDao
import com.video.downloader.data.local.room.vault.VaultFileEntity
import com.video.downloader.data.local.room.vault.toDomain
import com.video.downloader.data.security.vault.VaultFileEncryptor
import com.video.downloader.di.IoDispatcher
 import com.video.downloader.domain.models.vault.VaultFile
import com.video.downloader.domain.models.vault.VaultFileType
import com.video.downloader.domain.models.vault.VaultOriginalDeleteStatus
import com.video.downloader.domain.repository.vault.VaultFileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.video.downloader.domain.models.MediaFile
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.CipherInputStream
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Singleton
class VaultFileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vaultFileDao: VaultFileDao,
    private val vaultFileEncryptor: VaultFileEncryptor,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VaultFileRepository {

    override fun observeVaultFiles(): Flow<List<VaultFile>> {
        return vaultFileDao.observeVaultFiles()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun addFileToVault(
        sourceUri: String
    ): VaultFile {
        return withContext(ioDispatcher) {
            val uri = Uri.parse(sourceUri)
            val metadata = readVaultSourceMetadata(uri)

            val id = UUID.randomUUID().toString()

            val encryptedFile = File(
                getEncryptedVaultDirectory(),
                "$id.vault"
            )

            val thumbnailPath = runCatching {
                createThumbnailIfPossible(
                    id = id,
                    sourceUri = uri,
                    fileType = metadata.fileType
                )
            }.getOrNull()

            val encryptionResult = vaultFileEncryptor.encryptToFile(
                sourceUri = uri,
                encryptedOutputFile = encryptedFile
            )

            val entity = VaultFileEntity(
                id = id,
                originalUri = sourceUri,
                originalFileName = metadata.fileName,
                mimeType = metadata.mimeType,
                fileType = metadata.fileType.name,
                originalSizeBytes = metadata.sizeBytes,
                encryptedFilePath = encryptionResult.encryptedFilePath,
                thumbnailPath = thumbnailPath,
                ivBase64 = encryptionResult.ivBase64,
                durationMillis = metadata.durationMillis,
                createdAtMillis = System.currentTimeMillis(),
                originalDeleteStatus = VaultOriginalDeleteStatus.PENDING_DELETE.name
            )

            try {
                vaultFileDao.upsertVaultFile(entity)
                entity.toDomain()
            } catch (throwable: Throwable) {
                encryptedFile.delete()
                thumbnailPath?.let { File(it).delete() }
                throw throwable
            }
        }
    }

    override suspend fun prepareVaultFileForPlayback(
        id: String
    ): MediaFile {
        return withContext(ioDispatcher) {
            cleanOldPlaybackTempFiles()

            val entity = vaultFileDao.getVaultFileById(id)
                ?: throw IllegalArgumentException("Vault file not found.")

            val encryptedFile = File(entity.encryptedFilePath)

            if (!encryptedFile.exists()) {
                throw IllegalStateException("Secured Vault file is missing.")
            }

            val extension = resolveSafeExtension(
                fileName = entity.originalFileName,
                fileType = entity.fileType
            )

            val tempFile = File(
                getPlaybackTempDirectory(),
                "${entity.id}_${System.currentTimeMillis()}.$extension"
            )

            tempFile.parentFile?.mkdirs()

            try {
                val cipher = vaultFileEncryptor.createDecryptCipher(
                    ivBase64 = entity.ivBase64
                )

                CipherInputStream(
                    FileInputStream(encryptedFile),
                    cipher
                ).use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(
                            out = output,
                            bufferSize = PLAYBACK_BUFFER_SIZE
                        )
                    }
                }

                MediaFile(
                    id = entity.id.hashCode().toLong(),
                    filePath = tempFile.absolutePath,
                    fileName = entity.originalFileName,
                    isVideo = entity.fileType == VaultFileType.VIDEO.name,
                    contentUri = tempFile.toURI().toString()
                )
            } catch (throwable: Throwable) {
                tempFile.delete()
                throw throwable
            }
        }
    }

    override suspend fun restoreVaultFile(id: String): String {
        return withContext(ioDispatcher) {
            val entity = vaultFileDao.getVaultFileById(id)
                ?: throw IllegalArgumentException("Vault file not found.")

            val encryptedFile = File(entity.encryptedFilePath)

            if (!encryptedFile.exists()) {
                throw IllegalStateException("Secured Vault file is missing.")
            }

            val fileType = runCatching {
                VaultFileType.valueOf(entity.fileType)
            }.getOrDefault(VaultFileType.VIDEO)

            val outputFileName = sanitizeFileName(entity.originalFileName)

            val mimeType = entity.mimeType.ifBlank {
                fallbackMimeType(
                    fileName = outputFileName,
                    fileType = fileType
                )
            }

            val cipher = vaultFileEncryptor.createDecryptCipher(
                ivBase64 = entity.ivBase64
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                restoreUsingScopedStorage(
                    encryptedFile = encryptedFile,
                    cipher = cipher,
                    outputFileName = outputFileName,
                    mimeType = mimeType,
                    fileType = fileType
                )
            } else {
                restoreUsingLegacyStorage(
                    encryptedFile = encryptedFile,
                    cipher = cipher,
                    outputFileName = outputFileName,
                    mimeType = mimeType,
                    fileType = fileType
                )
            }
        }
    }

    override suspend fun markOriginalFileDeleted(id: String) {
        withContext(ioDispatcher) {
            vaultFileDao.updateOriginalDeleteStatus(
                id = id,
                status = VaultOriginalDeleteStatus.DELETED.name
            )
        }
    }

    override suspend fun markOriginalFileDeleteFailed(id: String) {
        withContext(ioDispatcher) {
            vaultFileDao.updateOriginalDeleteStatus(
                id = id,
                status = VaultOriginalDeleteStatus.DELETE_FAILED.name
            )
        }
    }

    override suspend fun deleteVaultFile(id: String) {
        withContext(ioDispatcher) {
            val entity = vaultFileDao.getVaultFileById(id) ?: return@withContext

            File(entity.encryptedFilePath).delete()

            entity.thumbnailPath?.let { path ->
                File(path).delete()
            }

            vaultFileDao.deleteVaultFileById(id)
        }
    }

    private fun readVaultSourceMetadata(uri: Uri): VaultSourceMetadata {
        val mimeType = context.contentResolver.getType(uri).orEmpty()
        val fileName = queryDisplayName(uri)
        val sizeBytes = queryFileSize(uri)

        val fileType = resolveVaultFileType(
            mimeType = mimeType,
            fileName = fileName
        )

        return VaultSourceMetadata(
            fileName = fileName,
            mimeType = mimeType.ifBlank {
                fallbackMimeType(fileName, fileType)
            },
            fileType = fileType,
            sizeBytes = sizeBytes,
            durationMillis = readDurationMillis(uri)
        )
    }

    private fun queryDisplayName(uri: Uri): String {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (cursor.moveToFirst() && index != -1) {
                return sanitizeFileName(cursor.getString(index).orEmpty())
            }
        }

        return "vault_file_${System.currentTimeMillis()}"
    }

    private fun queryFileSize(uri: Uri): Long {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.SIZE)

            if (cursor.moveToFirst() && index != -1) {
                return cursor.getLong(index).coerceAtLeast(0L)
            }
        }

        return 0L
    }

    private fun readDurationMillis(uri: Uri): Long {
        return runCatching {
            val retriever = MediaMetadataRetriever()

            try {
                retriever.setDataSource(context, uri)

                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toLongOrNull() ?: 0L
            } finally {
                retriever.release()
            }
        }.getOrDefault(0L)
    }

    private fun createThumbnailIfPossible(
        id: String,
        sourceUri: Uri,
        fileType: VaultFileType
    ): String? {
        if (fileType != VaultFileType.VIDEO) return null

        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(context, sourceUri)

            val bitmap = retriever.getFrameAtTime(
                1_000_000L,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: return null

            val thumbnailFile = File(
                getThumbnailVaultDirectory(),
                "$id.jpg"
            )

            thumbnailFile.parentFile?.mkdirs()

            thumbnailFile.outputStream().use { output ->
                bitmap.compress(
                    android.graphics.Bitmap.CompressFormat.JPEG,
                    86,
                    output
                )
            }

            bitmap.recycle()

            thumbnailFile.absolutePath
        } finally {
            retriever.release()
        }
    }

    private fun resolveVaultFileType(
        mimeType: String,
        fileName: String
    ): VaultFileType {
        val lowerMime = mimeType.lowercase()
        val lowerName = fileName.lowercase()

        return when {
            lowerMime.startsWith("video/") -> VaultFileType.VIDEO
            lowerMime.startsWith("audio/") -> VaultFileType.AUDIO

            lowerName.endsWith(".mp4") ||
                    lowerName.endsWith(".mkv") ||
                    lowerName.endsWith(".webm") ||
                    lowerName.endsWith(".mov") ||
                    lowerName.endsWith(".avi") -> VaultFileType.VIDEO

            lowerName.endsWith(".mp3") ||
                    lowerName.endsWith(".m4a") ||
                    lowerName.endsWith(".wav") ||
                    lowerName.endsWith(".aac") ||
                    lowerName.endsWith(".ogg") -> VaultFileType.AUDIO

            else -> throw IllegalArgumentException(
                "Only video and audio files can be added to Vault."
            )
        }
    }

    private fun fallbackMimeType(
        fileName: String,
        fileType: VaultFileType
    ): String {
        val extension = fileName.substringAfterLast(".", "")

        val mime = MimeTypeMap
            .getSingleton()
            .getMimeTypeFromExtension(extension.lowercase())

        if (!mime.isNullOrBlank()) return mime

        return when (fileType) {
            VaultFileType.VIDEO -> "video/mp4"
            VaultFileType.AUDIO -> "audio/mpeg"
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .trim()
            .ifBlank {
                "vault_file_${System.currentTimeMillis()}"
            }
    }

    private fun getEncryptedVaultDirectory(): File {
        return File(context.filesDir, "vault/encrypted").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun getThumbnailVaultDirectory(): File {
        return File(context.filesDir, "vault/thumbnails").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun getPlaybackTempDirectory(): File {
        return File(context.cacheDir, "vault/playback").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun cleanOldPlaybackTempFiles() {
        val directory = getPlaybackTempDirectory()
        val now = System.currentTimeMillis()

        directory.listFiles()?.forEach { file ->
            val isOld = now - file.lastModified() > PLAYBACK_TEMP_MAX_AGE_MILLIS

            if (isOld) {
                file.delete()
            }
        }
    }

    private fun resolveSafeExtension(
        fileName: String,
        fileType: String
    ): String {
        val extension = fileName
            .substringAfterLast(".", "")
            .lowercase()
            .replace(Regex("[^a-z0-9]"), "")

        if (extension.isNotBlank()) {
            return extension
        }

        return if (fileType == VaultFileType.AUDIO.name) {
            "mp3"
        } else {
            "mp4"
        }
    }

    private fun restoreUsingScopedStorage(
        encryptedFile: File,
        cipher: javax.crypto.Cipher,
        outputFileName: String,
        mimeType: String,
        fileType: VaultFileType
    ): String {
        val resolver = context.contentResolver

        val collectionUri = when (fileType) {
            VaultFileType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            VaultFileType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val relativePath = when (fileType) {
            VaultFileType.VIDEO -> "${Environment.DIRECTORY_MOVIES}/VideoDownloaderVault"
            VaultFileType.AUDIO -> "${Environment.DIRECTORY_MUSIC}/VideoDownloaderVault"
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, outputFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            put(MediaStore.MediaColumns.IS_PENDING, 1)

            when (fileType) {
                VaultFileType.VIDEO -> {
                    put(
                        MediaStore.Video.Media.TITLE,
                        outputFileName.substringBeforeLast(".")
                    )
                }

                VaultFileType.AUDIO -> {
                    put(
                        MediaStore.Audio.Media.TITLE,
                        outputFileName.substringBeforeLast(".")
                    )
                }
            }
        }

        val outputUri = resolver.insert(
            collectionUri,
            values
        ) ?: throw IllegalStateException("Unable to create restored media file.")

        try {
            resolver.openOutputStream(outputUri, "w")?.use { output ->
                CipherInputStream(
                    FileInputStream(encryptedFile),
                    cipher
                ).use { input ->
                    input.copyTo(
                        out = output,
                        bufferSize = RESTORE_BUFFER_SIZE
                    )
                }
            } ?: throw IllegalStateException("Unable to restore Vault file.")

            val publishValues = ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }

            resolver.update(
                outputUri,
                publishValues,
                null,
                null
            )

            return outputUri.toString()
        } catch (throwable: Throwable) {
            resolver.delete(outputUri, null, null)
            throw throwable
        }
    }

    private suspend fun restoreUsingLegacyStorage(
        encryptedFile: File,
        cipher: javax.crypto.Cipher,
        outputFileName: String,
        mimeType: String,
        fileType: VaultFileType
    ): String {
        val parentDirectory = when (fileType) {
            VaultFileType.VIDEO -> Environment.DIRECTORY_MOVIES
            VaultFileType.AUDIO -> Environment.DIRECTORY_MUSIC
        }

        val outputDirectory = File(
            Environment.getExternalStoragePublicDirectory(parentDirectory),
            "VideoDownloaderVault"
        ).apply {
            if (!exists()) mkdirs()
        }

        val finalFile = File(
            outputDirectory,
            createUniqueFileName(
                directory = outputDirectory,
                requestedName = outputFileName
            )
        )

        try {
            CipherInputStream(
                FileInputStream(encryptedFile),
                cipher
            ).use { input ->
                FileOutputStream(finalFile).use { output ->
                    input.copyTo(
                        out = output,
                        bufferSize = RESTORE_BUFFER_SIZE
                    )
                }
            }

            val scannedUri = scanRestoredFile(
                path = finalFile.absolutePath,
                mimeType = mimeType
            )

            return scannedUri ?: finalFile.toURI().toString()
        } catch (throwable: Throwable) {
            finalFile.delete()
            throw throwable
        }
    }

    private fun createUniqueFileName(
        directory: File,
        requestedName: String
    ): String {
        val cleanName = requestedName.ifBlank {
            "restored_${System.currentTimeMillis()}"
        }

        val baseName = cleanName.substringBeforeLast(
            delimiter = ".",
            missingDelimiterValue = cleanName
        )

        val extension = cleanName.substringAfterLast(
            delimiter = ".",
            missingDelimiterValue = ""
        )

        var candidate = cleanName
        var index = 1

        while (File(directory, candidate).exists()) {
            candidate = if (extension.isBlank()) {
                "$baseName ($index)"
            } else {
                "$baseName ($index).$extension"
            }

            index++
        }

        return candidate
    }

    private suspend fun scanRestoredFile(
        path: String,
        mimeType: String
    ): String? {
        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(path),
                arrayOf(mimeType)
            ) { _, uri ->
                if (continuation.isActive) {
                    continuation.resume(uri?.toString())
                }
            }
        }
    }

    private companion object {
        private const val PLAYBACK_BUFFER_SIZE = 64 * 1024
        private const val PLAYBACK_TEMP_MAX_AGE_MILLIS = 24 * 60 * 60 * 1000L
        private const val RESTORE_BUFFER_SIZE = 64 * 1024
    }
}

private data class VaultSourceMetadata(
    val fileName: String,
    val mimeType: String,
    val fileType: VaultFileType,
    val sizeBytes: Long,
    val durationMillis: Long
)