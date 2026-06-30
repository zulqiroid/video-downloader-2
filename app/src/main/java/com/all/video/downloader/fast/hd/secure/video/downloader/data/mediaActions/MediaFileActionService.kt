package com.all.video.downloader.fast.hd.secure.video.downloader.data.mediaActions

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.mediaActions.MediaFileActionResult
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaType
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.vault.VaultFileUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.vault.VaultSecurityUseCases
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import android.os.Build


@Singleton
class MediaFileActionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vaultFileUseCases: VaultFileUseCases,
    private val vaultSecurityUseCases: VaultSecurityUseCases
) {

    suspend fun renameMediaItem(
        item: MediaItem,
        newNameWithoutExtension: String,
        allowUserApproval: Boolean = true
    ): MediaFileActionResult<MediaItem> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val validationError = validateFileName(newNameWithoutExtension)
                if (validationError != null) {
                    return@withContext MediaFileActionResult.Failure(validationError)
                }

                val oldFile = item.resolveExistingFile()
                val extension = oldFile?.extension
                    ?.ifBlank { item.extension }
                    ?: item.extension

                val safeExtension = extension
                    .trim()
                    .trimStart('.')

                val finalFileName = if (safeExtension.isBlank()) {
                    newNameWithoutExtension.trim()
                } else {
                    "${newNameWithoutExtension.trim()}.$safeExtension"
                }

                val contentUri = item.resolveContentUri()

                /**
                 * MediaStore item hai to pehle MediaStore DISPLAY_NAME update karna best hai.
                 * Android 10+ par permission required ho sakti hai.
                 */
                if (contentUri != null) {
                    when (
                        val updateResult = updateMediaStoreDisplayName(
                            uri = contentUri,
                            newDisplayName = finalFileName,
                            allowUserApproval = allowUserApproval
                        )
                    ) {
                        is MediaFileActionResult.RequiresUserApproval -> {
                            return@withContext updateResult
                        }

                        is MediaFileActionResult.Failure -> {
                            /**
                             * Agar MediaStore update fail ho, direct file fallback sirf tab try karein
                             * jab actual file path available ho.
                             */
                            if (oldFile == null) {
                                return@withContext updateResult
                            }
                        }

                        is MediaFileActionResult.Success -> {
                            scanPaths(
                                oldPath = oldFile?.absolutePath,
                                newPath = oldFile?.parentFile
                                    ?.let { parent -> File(parent, finalFileName).absolutePath }
                            )

                            return@withContext MediaFileActionResult.Success(
                                item.copy(
                                    title = newNameWithoutExtension.trim(),
                                    displayName = finalFileName,
                                    fileName = finalFileName,
                                    filePath = oldFile?.parentFile
                                        ?.let { parent -> File(parent, finalFileName).absolutePath }
                                        ?: item.filePath,
                                    extension = safeExtension
                                )
                            )
                        }
                    }
                }

                val sourceFile = oldFile
                    ?: return@withContext MediaFileActionResult.Failure("File not found.")

                val targetFile = File(
                    sourceFile.parentFile,
                    finalFileName
                )

                if (targetFile.exists()) {
                    return@withContext MediaFileActionResult.Failure(
                        "A file with this name already exists."
                    )
                }

                val renamed = sourceFile.renameTo(targetFile)

                if (!renamed) {
                    return@withContext MediaFileActionResult.Failure(
                        "Unable to rename this file."
                    )
                }

                scanPaths(
                    oldPath = sourceFile.absolutePath,
                    newPath = targetFile.absolutePath
                )

                MediaFileActionResult.Success(
                    item.copy(
                        title = newNameWithoutExtension.trim(),
                        displayName = finalFileName,
                        fileName = finalFileName,
                        filePath = targetFile.absolutePath,
                        extension = safeExtension
                    )
                )
            }.getOrElse { throwable ->
                Timber.tag(TAG).e(throwable, "renameMediaItem failed")

                MediaFileActionResult.Failure(
                    message = throwable.message ?: "Unable to rename this file.",
                    throwable = throwable
                )
            }
        }
    }

    suspend fun deleteMediaItem(
        item: MediaItem,
        allowUserApproval: Boolean = true
    ): MediaFileActionResult<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val contentUri = item.resolveContentUri()

                if (contentUri != null) {
                    when (
                        val deleteResult = deleteMediaStoreUri(
                            uri = contentUri,
                            allowUserApproval = allowUserApproval
                        )
                    ) {
                        is MediaFileActionResult.RequiresUserApproval -> {
                            return@withContext deleteResult
                        }

                        is MediaFileActionResult.Success -> {
                            item.filePath
                                .takeIf { it.isNotBlank() }
                                ?.let { path ->
                                    scanPaths(
                                        oldPath = path,
                                        newPath = null
                                    )
                                }

                            return@withContext MediaFileActionResult.Success(Unit)
                        }

                        is MediaFileActionResult.Failure -> {
                            /**
                             * Direct file fallback sirf tab try karein jab real file path available ho.
                             */
                        }
                    }
                }

                val file = item.resolveExistingFile()
                    ?: return@withContext MediaFileActionResult.Failure("File not found.")

                val deleted = file.delete()

                if (!deleted) {
                    return@withContext MediaFileActionResult.Failure(
                        "Unable to delete this file."
                    )
                }

                scanPaths(
                    oldPath = file.absolutePath,
                    newPath = null
                )

                MediaFileActionResult.Success(Unit)
            }.getOrElse { throwable ->
                Timber.tag(TAG).e(throwable, "deleteMediaItem failed")

                MediaFileActionResult.Failure(
                    message = throwable.message ?: "Unable to delete this file.",
                    throwable = throwable
                )
            }
        }
    }

    suspend fun moveMediaItemToVault(
        item: MediaItem,
        allowUserApproval: Boolean = true
    ): MediaFileActionResult<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val isVaultConfigured = vaultSecurityUseCases
                    .observeVaultConfigured()
                    .first()

                if (!isVaultConfigured) {
                    return@withContext MediaFileActionResult.Failure(
                        "Please set up your Vault PIN first."
                    )
                }

                val sourceUri = item.resolveVaultSourceUri()
                    ?: return@withContext MediaFileActionResult.Failure("File not found.")

                /**
                 * Pehle file vault mein secure copy/encrypt hogi.
                 */
                vaultFileUseCases.addFileToVault(sourceUri.toString())

                /**
                 * Phir original gallery/media file delete hogi.
                 * Android 10+ par user approval required ho sakta hai.
                 */
                when (
                    val deleteResult = deleteMediaItem(
                        item = item,
                        allowUserApproval = allowUserApproval
                    )
                ) {
                    is MediaFileActionResult.Success -> {
                        MediaFileActionResult.Success(Unit)
                    }

                    is MediaFileActionResult.RequiresUserApproval -> {
                        deleteResult
                    }

                    is MediaFileActionResult.Failure -> {
                        MediaFileActionResult.Failure(
                            "File moved to Vault, but original gallery file could not be removed."
                        )
                    }
                }
            }.getOrElse { throwable ->
                Timber.tag(TAG).e(throwable, "moveMediaItemToVault failed")

                MediaFileActionResult.Failure(
                    message = throwable.message ?: "Unable to move file to Vault.",
                    throwable = throwable
                )
            }
        }
    }

    fun buildShareUri(
        item: MediaItem
    ): Uri? {
        item.uri.takeIf { it.startsWith("content://") }?.let { uri ->
            return uri.toUri()
        }

        item.mediaStoreId?.let { mediaStoreId ->
            val collectionUri = when (item.mediaType) {
                MediaType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                MediaType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> null
            }

            if (collectionUri != null) {
                return ContentUris.withAppendedId(
                    collectionUri,
                    mediaStoreId
                )
            }
        }

        val file = item.resolveExistingFile() ?: return null

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )
    }

    private fun updateMediaStoreDisplayName(
        uri: Uri,
        newDisplayName: String,
        allowUserApproval: Boolean
    ): MediaFileActionResult<Unit> {
        return try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, newDisplayName)
            }

            val rows = context.contentResolver.update(
                uri,
                values,
                null,
                null
            )

            if (rows > 0) {
                MediaFileActionResult.Success(Unit)
            } else {
                MediaFileActionResult.Failure("Unable to rename this file.")
            }
        } catch (throwable: Throwable) {
            throwable.toWriteApprovalResult(
                uri = uri,
                allowUserApproval = allowUserApproval,
                message = "Allow access to rename this file."
            ) ?: MediaFileActionResult.Failure(
                message = throwable.message ?: "Unable to rename this file.",
                throwable = throwable
            )
        }
    }

    private fun deleteMediaStoreUri(
        uri: Uri,
        allowUserApproval: Boolean
    ): MediaFileActionResult<Unit> {
        return try {
            val rows = context.contentResolver.delete(
                uri,
                null,
                null
            )

            if (rows > 0 || isMediaStoreUriGone(uri)) {
                MediaFileActionResult.Success(Unit)
            } else {
                MediaFileActionResult.Failure("Unable to delete this file.")
            }
        } catch (throwable: Throwable) {
            throwable.toDeleteApprovalResult(
                uri = uri,
                allowUserApproval = allowUserApproval,
                message = "Allow access to delete this file."
            ) ?: MediaFileActionResult.Failure(
                message = throwable.message ?: "Unable to delete this file.",
                throwable = throwable
            )
        }
    }

    private fun Throwable.toWriteApprovalResult(
        uri: Uri,
        allowUserApproval: Boolean,
        message: String
    ): MediaFileActionResult.RequiresUserApproval? {
        if (!allowUserApproval) return null

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                val pendingIntent = MediaStore.createWriteRequest(
                    context.contentResolver,
                    listOf(uri)
                )

                MediaFileActionResult.RequiresUserApproval(
                    intentSender = pendingIntent.intentSender,
                    message = message
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    this is RecoverableSecurityException -> {
                MediaFileActionResult.RequiresUserApproval(
                    intentSender = userAction.actionIntent.intentSender,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun Throwable.toDeleteApprovalResult(
        uri: Uri,
        allowUserApproval: Boolean,
        message: String
    ): MediaFileActionResult.RequiresUserApproval? {
        if (!allowUserApproval) return null

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                val pendingIntent = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(uri)
                )

                MediaFileActionResult.RequiresUserApproval(
                    intentSender = pendingIntent.intentSender,
                    message = message
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    this is RecoverableSecurityException -> {
                MediaFileActionResult.RequiresUserApproval(
                    intentSender = userAction.actionIntent.intentSender,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun isMediaStoreUriGone(uri: Uri): Boolean {
        return runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns._ID),
                null,
                null,
                null
            )?.use { cursor ->
                !cursor.moveToFirst()
            } ?: false
        }.getOrDefault(false)
    }

    private fun MediaItem.resolveContentUri(): Uri? {
        uri.takeIf { it.startsWith("content://") }?.let {
            return it.toUri()
        }

        val id = mediaStoreId ?: return null

        val collectionUri = when (mediaType) {
            MediaType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            MediaType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> return null
        }

        return ContentUris.withAppendedId(collectionUri, id)
    }

    private fun MediaItem.resolveVaultSourceUri(): Uri? {
        resolveContentUri()?.let { return it }

        val file = resolveExistingFile() ?: return null

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )
    }

    private fun MediaItem.resolveExistingFile(): File? {
        val path = filePath.takeIf { it.isNotBlank() }
            ?: return null

        return File(path).takeIf { file ->
            file.exists()
        }
    }

    private fun validateFileName(
        value: String
    ): String? {
        val name = value.trim()

        if (name.isBlank()) {
            return "File name cannot be empty."
        }

        val invalidCharacters = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

        if (name.any { char -> char in invalidCharacters }) {
            return "File name contains invalid characters."
        }

        return null
    }

    private fun scanPaths(
        oldPath: String?,
        newPath: String?
    ) {
        val paths = listOfNotNull(oldPath, newPath)
            .filter { it.isNotBlank() }
            .toTypedArray()

        if (paths.isEmpty()) return

        MediaScannerConnection.scanFile(
            context,
            paths,
            null,
            null
        )
    }

    private companion object {
        private const val TAG = "MediaFileActions"
    }
}