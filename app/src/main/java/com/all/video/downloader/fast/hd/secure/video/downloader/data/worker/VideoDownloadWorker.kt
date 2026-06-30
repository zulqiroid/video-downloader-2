package com.all.video.downloader.fast.hd.secure.video.downloader.data.worker

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.DownloadProgressStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext
import androidx.documentfile.provider.DocumentFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.settings.DownloadLocationType

@HiltWorker
class VideoDownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val okHttpClient: OkHttpClient,
    private val progressStore: DownloadProgressStore,
    private val notificationHelper: VideoDownloadNotificationHelper,
) : CoroutineWorker(
    appContext = context,
    params = workerParameters
) {

    override suspend fun doWork(): Result {

        val notificationsEnabled = inputData.getBoolean(
            KEY_NOTIFICATIONS_ENABLED,
            true
        )

        val downloadLocationType = inputData.getString(
            KEY_DOWNLOAD_LOCATION_TYPE
        ).orEmpty()

        val downloadLocationTreeUri = inputData.getString(
            KEY_DOWNLOAD_LOCATION_TREE_URI
        ).orEmpty()

        val id = inputData.getLong(KEY_ID, INVALID_ID)
        val url = inputData.getString(KEY_URL).orEmpty()
        val sourceUrl = inputData.getString(KEY_SOURCE_URL).orEmpty()
        val fileName = inputData.getString(KEY_FILE_NAME).orEmpty()
        val platform = inputData.getString(KEY_PLATFORM).orEmpty()
        val quality = inputData.getString(KEY_QUALITY).orEmpty()
        val thumbnailUrl = inputData
            .getString(KEY_THUMBNAIL_URL)
            .orEmpty()
            .takeIf { it.isNotBlank() }

        if (id == INVALID_ID || url.isBlank() || fileName.isBlank()) {
            return Result.failure()
        }

        return try {
            setForeground(
                notificationHelper.createProgressForegroundInfo(
                    context = context,
                    workId = this.id,
                    downloadId = id,
                    fileName = fileName,
                    progress = 0,
                    speedBytesPerSec = 0L,
                    etaSeconds = -1L
                )
            )

            withContext(Dispatchers.IO) {
                runDownload(
                    id = id,
                    url = url,
                    sourceUrl = sourceUrl.ifBlank { url },
                    fileName = fileName,
                    platform = platform,
                    quality = quality,
                    thumbnailUrl = thumbnailUrl,
                    notificationsEnabled = notificationsEnabled,
                    downloadLocationType = downloadLocationType,
                    downloadLocationTreeUri = downloadLocationTreeUri
                )
            }
        } catch (controlledStop: DownloadControlledStopException) {
            Result.success()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            markFailed(
                id = id,
                url = url,
                sourceUrl = sourceUrl.ifBlank { url },
                fileName = fileName,
                platform = platform,
                quality = quality,
                thumbnailUrl = thumbnailUrl
            )

            notificationHelper.showDownloadFailed(
                context = context,
                downloadId = id,
                fileName = fileName
            )

            Result.failure()
        }
    }

    private suspend fun runDownload(
        id: Long,
        url: String,
        sourceUrl: String,
        fileName: String,
        platform: String,
        quality: String,
        thumbnailUrl: String?,
        notificationsEnabled: Boolean,
        downloadLocationType: String,
        downloadLocationTreeUri: String,
    ): Result {
        ensureDownloadCanContinue(id)

        val tempFile = File(
            getTempDownloadFolder(),
            "$fileName.part"
        )

        val existingBytes = tempFile
            .takeIf { it.exists() }
            ?.length()
            ?: 0L

        val requestBuilder = Request.Builder()
            .url(url)

        if (existingBytes > 0L) {
            requestBuilder.header(
                RANGE_HEADER,
                "bytes=$existingBytes-"
            )
        }

        val request = requestBuilder.build()
        val call = okHttpClient.newCall(request)

        val cancellationHandle = coroutineContext[Job]?.invokeOnCompletion { throwable ->
            if (throwable is CancellationException) {
                call.cancel()
            }
        }

        try {
            call.execute().use { response ->
                ensureDownloadCanContinue(id)

                if (!response.isSuccessful) {
                    throw IllegalStateException("Server error: ${response.code}")
                }

                val supportsResume = response.code == HTTP_PARTIAL_CONTENT
                val shouldAppend = existingBytes > 0L && supportsResume

                if (existingBytes > 0L && !supportsResume) {
                    tempFile.delete()
                }

                val startBytes = if (shouldAppend) existingBytes else 0L
                val responseContentLength = response.body.contentLength()

                val totalBytes = when {
                    responseContentLength <= 0L -> 0L
                    shouldAppend -> startBytes + responseContentLength
                    else -> responseContentLength
                }

                saveProgressSafely(
                    id = id,
                    url = url,
                    sourceUrl = sourceUrl,
                    fileName = fileName,
                    filePath = tempFile.absolutePath,
                    progress = calculateProgress(startBytes, totalBytes),
                    status = DownloadStatus.DOWNLOADING,
                    downloadedBytes = startBytes,
                    totalBytes = totalBytes,
                    speedBytesPerSec = 0L,
                    lastEtaSeconds = -1L,
                    platform = platform,
                    quality = quality,
                    thumbnailUrl = thumbnailUrl
                )

                response.body.byteStream().use { inputStream ->
                    RandomAccessFile(tempFile, RANDOM_ACCESS_MODE).use { outputFile ->
                        outputFile.seek(startBytes)

                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var downloadedBytes = startBytes

                        var lastProgressTime = System.currentTimeMillis()
                        var lastProgressBytes = downloadedBytes

                        while (true) {
                            coroutineContext.ensureActive()
                            ensureDownloadCanContinue(id)

                            val read = inputStream.read(buffer)

                            if (read == END_OF_STREAM) {
                                break
                            }

                            outputFile.write(buffer, 0, read)
                            downloadedBytes += read

                            val now = System.currentTimeMillis()

                            if (now - lastProgressTime >= PROGRESS_UPDATE_INTERVAL_MS) {
                                val bytesDiff = downloadedBytes - lastProgressBytes
                                val timeDiff = now - lastProgressTime

                                val speedBytesPerSec = if (timeDiff > 0L) {
                                    (bytesDiff * 1000L) / timeDiff
                                } else {
                                    0L
                                }

                                val remainingBytes = totalBytes - downloadedBytes

                                val etaSeconds = if (speedBytesPerSec > 0L && totalBytes > 0L) {
                                    remainingBytes / speedBytesPerSec
                                } else {
                                    -1L
                                }

                                val progress = calculateProgress(
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes
                                )

                                saveProgressSafely(
                                    id = id,
                                    url = url,
                                    sourceUrl = sourceUrl,
                                    fileName = fileName,
                                    filePath = tempFile.absolutePath,
                                    progress = progress,
                                    status = DownloadStatus.DOWNLOADING,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes,
                                    speedBytesPerSec = speedBytesPerSec,
                                    lastEtaSeconds = etaSeconds,
                                    platform = platform,
                                    quality = quality,
                                    thumbnailUrl = thumbnailUrl
                                )

                                lastProgressTime = now
                                lastProgressBytes = downloadedBytes
                            }
                        }
                    }
                }
            }
        } finally {
            cancellationHandle?.dispose()
        }

        ensureDownloadCanContinue(id)

        val savedFilePath = publishCompletedVideo(
            tempFile = tempFile,
            fileName = fileName,
            downloadLocationType = downloadLocationType,
            downloadLocationTreeUri = downloadLocationTreeUri
        )

        tempFile.delete()

        ensureDownloadCanContinue(id)

        /*
         * Completed file ka source of truth device folder hai.
         * Room database mein SUCCESS record save nahi karna.
         */
        progressStore.remove(id)

        if (notificationsEnabled) {
            notificationHelper.showDownloadComplete(
                context = context,
                downloadId = id,
                fileName = File(savedFilePath).name
            )
        }

        return Result.success()
    }

    private suspend fun markFailed(
        id: Long,
        url: String,
        sourceUrl: String,
        fileName: String,
        platform: String,
        quality: String,
        thumbnailUrl: String?,
    ) {
        val existingItem = progressStore.get(id)

        if (existingItem == null || existingItem.status == DownloadStatus.PAUSED) {
            return
        }

        val tempFile = File(
            getTempDownloadFolder(),
            "$fileName.part"
        )

        progressStore.upsert(
            DownloadItem(
                id = id,
                url = url,
                sourceUrl = sourceUrl,
                fileName = fileName,
                filePath = tempFile.absolutePath,
                progress = calculateProgress(
                    downloadedBytes = tempFile.length(),
                    totalBytes = existingItem.totalBytes
                ),
                status = DownloadStatus.FAILED,
                downloadedBytes = tempFile.length(),
                totalBytes = existingItem.totalBytes,
                speedBytesPerSec = 0L,
                lastEtaSeconds = -1L,
                workId = this.id.toString(),
                platform = platform,
                quality = quality,
                thumbnailUrl = thumbnailUrl
            )
        )
    }

    private suspend fun saveProgressSafely(
        id: Long,
        url: String,
        sourceUrl: String,
        fileName: String,
        filePath: String,
        progress: Int,
        status: DownloadStatus,
        downloadedBytes: Long,
        totalBytes: Long,
        speedBytesPerSec: Long,
        lastEtaSeconds: Long,
        platform: String,
        quality: String,
        thumbnailUrl: String?,
    ) {
        ensureDownloadCanContinue(id)

        progressStore.upsert(
            DownloadItem(
                id = id,
                url = url,
                sourceUrl = sourceUrl,
                fileName = fileName,
                filePath = filePath,
                progress = progress.coerceIn(0, 100),
                status = status,
                downloadedBytes = downloadedBytes,
                totalBytes = totalBytes,
                speedBytesPerSec = speedBytesPerSec,
                lastEtaSeconds = lastEtaSeconds,
                workId = this.id.toString(),
                platform = platform,
                quality = quality,
                thumbnailUrl = thumbnailUrl
            )
        )

        if (status == DownloadStatus.DOWNLOADING) {
            setForeground(
                notificationHelper.createProgressForegroundInfo(
                    context = context,
                    workId = this.id,
                    downloadId = id,
                    fileName = fileName,
                    progress = progress,
                    speedBytesPerSec = speedBytesPerSec,
                    etaSeconds = lastEtaSeconds
                )
            )
        }
    }

    private suspend fun ensureDownloadCanContinue(id: Long) {
        if (isStopped) {
            throw DownloadControlledStopException()
        }

        val currentItem = progressStore.get(id)
            ?: throw DownloadControlledStopException()

        if (currentItem.status == DownloadStatus.PAUSED) {
            throw DownloadControlledStopException()
        }
    }

    private fun publishCompletedVideo(
        tempFile: File,
        fileName: String,
        downloadLocationType: String,
        downloadLocationTreeUri: String
    ): String {
        if (!tempFile.exists()) {
            throw IOException("Downloaded temp file not found.")
        }

        return when (
            DownloadLocationType.fromStorageValue(downloadLocationType)
        ) {
            DownloadLocationType.INTERNAL_STORAGE -> {
                saveToPublicDownloadFolder(
                    tempFile = tempFile,
                    fileName = fileName
                )
            }

            DownloadLocationType.SD_CARD -> {
                saveToSdCardFolderOrFallback(
                    tempFile = tempFile,
                    fileName = fileName
                )
            }

            DownloadLocationType.CUSTOM_FOLDER -> {
                saveToCustomFolderOrFallback(
                    tempFile = tempFile,
                    fileName = fileName,
                    treeUri = downloadLocationTreeUri
                )
            }
        }
    }

    private fun saveToPublicDownloadFolder(
        tempFile: File,
        fileName: String
    ): String {
        val outputFile = File(
            getPublicDownloadFolder(),
            fileName
        )

        if (outputFile.exists()) {
            outputFile.delete()
        }

        tempFile.copyTo(
            target = outputFile,
            overwrite = true
        )

        scanCompletedVideo(outputFile)

        return outputFile.absolutePath
    }

    private fun saveToSdCardFolderOrFallback(
        tempFile: File,
        fileName: String
    ): String {
        val sdCardFolder = context.applicationContext
            .getExternalFilesDirs(Environment.DIRECTORY_MOVIES)
            .filterNotNull()
            .firstOrNull { file ->
                Environment.isExternalStorageRemovable(file)
            }
            ?.let { folder ->
                File(folder, PUBLIC_FOLDER_NAME)
            }

        if (sdCardFolder == null) {
            return saveToPublicDownloadFolder(
                tempFile = tempFile,
                fileName = fileName
            )
        }

        if (!sdCardFolder.exists()) {
            sdCardFolder.mkdirs()
        }

        val outputFile = File(sdCardFolder, fileName)

        if (outputFile.exists()) {
            outputFile.delete()
        }

        tempFile.copyTo(
            target = outputFile,
            overwrite = true
        )

        scanCompletedVideo(outputFile)

        return outputFile.absolutePath
    }

    private fun saveToCustomFolderOrFallback(
        tempFile: File,
        fileName: String,
        treeUri: String
    ): String {
        if (treeUri.isBlank()) {
            return saveToPublicDownloadFolder(
                tempFile = tempFile,
                fileName = fileName
            )
        }

        val pickedFolder = DocumentFile.fromTreeUri(
            context,
            Uri.parse(treeUri)
        ) ?: return saveToPublicDownloadFolder(
            tempFile = tempFile,
            fileName = fileName
        )

        val mimeType = resolveMimeType(fileName)

        val existingFile = pickedFolder.findFile(fileName)
        existingFile?.delete()

        val outputDocument = pickedFolder.createFile(
            mimeType,
            fileName
        ) ?: return saveToPublicDownloadFolder(
            tempFile = tempFile,
            fileName = fileName
        )

        context.contentResolver.openOutputStream(outputDocument.uri)?.use { outputStream ->
            tempFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: return saveToPublicDownloadFolder(
            tempFile = tempFile,
            fileName = fileName
        )

        return outputDocument.uri.toString()
    }

    private fun saveVideoUsingMediaStore(
        tempFile: File,
        fileName: String,
    ): Uri {
        val resolver = context.applicationContext.contentResolver

        val collection = MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )

        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, resolveMimeType(fileName))
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_MOVIES}/$PUBLIC_FOLDER_NAME"
            )
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val uri = resolver.insert(collection, values)
            ?: throw IOException("Unable to create video in gallery.")

        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                tempFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IOException("Unable to open gallery output stream.")

            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)

            resolver.update(
                uri,
                values,
                null,
                null
            )

            return uri
        } catch (throwable: Throwable) {
            resolver.delete(uri, null, null)
            throw throwable
        }
    }

    private fun saveVideoLegacy(
        tempFile: File,
        fileName: String,
    ): File {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            ),
            PUBLIC_FOLDER_NAME
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }

        val outputFile = File(folder, fileName)

        if (outputFile.exists()) {
            outputFile.delete()
        }

        tempFile.copyTo(
            target = outputFile,
            overwrite = true
        )

        scanLegacyVideo(outputFile)

        return outputFile
    }

    private fun scanLegacyVideo(file: File) {
        MediaScannerConnection.scanFile(
            context.applicationContext,
            arrayOf(file.absolutePath),
            arrayOf(resolveMimeType(file.name)),
            null
        )
    }

    private fun getSavedFileSize(path: String): Long {
        return if (path.startsWith(CONTENT_URI_PREFIX)) {
            runCatching {
                context.applicationContext.contentResolver
                    .openFileDescriptor(Uri.parse(path), "r")
                    ?.use { descriptor ->
                        descriptor.statSize
                    }
            }.getOrNull() ?: 0L
        } else {
            File(path).length()
        }
    }

    private fun getTempDownloadFolder(): File {
        val baseFolder = context.applicationContext
            .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: context.applicationContext.filesDir

        return File(
            baseFolder,
            TEMP_FOLDER_NAME
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private fun resolveMimeType(fileName: String): String {
        val extension = fileName
            .substringAfterLast('.', DEFAULT_VIDEO_EXTENSION)
            .lowercase(Locale.US)
            .ifBlank { DEFAULT_VIDEO_EXTENSION }

        return MimeTypeMap
            .getSingleton()
            .getMimeTypeFromExtension(extension)
            ?: "video/mp4"
    }

    private fun calculateProgress(
        downloadedBytes: Long,
        totalBytes: Long,
    ): Int {
        if (totalBytes <= 0L) return 0

        return ((downloadedBytes * 100L) / totalBytes)
            .toInt()
            .coerceIn(0, 100)
    }

    private fun getPublicDownloadFolder(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            ),
            PUBLIC_FOLDER_NAME
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private fun scanCompletedVideo(file: File) {
        MediaScannerConnection.scanFile(
            context.applicationContext,
            arrayOf(file.absolutePath),
            arrayOf(resolveMimeType(file.name)),
            null
        )
    }

    private class DownloadControlledStopException : RuntimeException()

    companion object {
        const val KEY_ID = "download_id"
        const val KEY_URL = "download_url"
        const val KEY_SOURCE_URL = "download_source_url"
        const val KEY_FILE_NAME = "download_file_name"
        const val KEY_PLATFORM = "download_platform"
        const val KEY_QUALITY = "download_quality"
        const val KEY_THUMBNAIL_URL = "download_thumbnail_url"

        private const val INVALID_ID = -1L
        private const val PUBLIC_FOLDER_NAME = "Video Downloader"
        private const val TEMP_FOLDER_NAME = "DownloadParts"
        private const val RANGE_HEADER = "Range"
        private const val RANDOM_ACCESS_MODE = "rw"
        private const val END_OF_STREAM = -1
        private const val HTTP_PARTIAL_CONTENT = 206
        private const val PROGRESS_UPDATE_INTERVAL_MS = 800L
        private const val DEFAULT_VIDEO_EXTENSION = "mp4"
        private const val CONTENT_URI_PREFIX = "content://"

        const val KEY_NOTIFICATIONS_ENABLED = "download_notifications_enabled"
        const val KEY_DOWNLOAD_LOCATION_TYPE = "download_location_type"
        const val KEY_DOWNLOAD_LOCATION_TREE_URI = "download_location_tree_uri"
    }
}