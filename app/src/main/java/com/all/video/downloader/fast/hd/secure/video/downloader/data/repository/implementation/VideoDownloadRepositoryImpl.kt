package com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation

import android.content.Context
import android.database.ContentObserver
import android.os.Environment
import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
 import com.all.video.downloader.fast.hd.secure.video.downloader.data.worker.VideoDownloadWorker
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStartRequest
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.DownloadProgressStore
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.VideoDownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.settings.DownloadLocationType
import kotlinx.coroutines.flow.first

@Singleton
class VideoDownloadRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val progressStore: DownloadProgressStore,
    private val localDataStoreRepository: LocalDataStoreRepository
) : VideoDownloadRepository {

    private val appContext = context.applicationContext
    private val workManager = WorkManager.getInstance(appContext)

    override fun observeDownloads(): Flow<List<DownloadItem>> {
        return progressStore.observeDownloads()
            .map { items ->
                items.filterActiveDownloads()
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    @OptIn(FlowPreview::class)
    override fun observeDownloadedFileChanges(): Flow<Unit> {
        return observePublicDownloadFolderChanges()
            .onStart {
                emit(Unit)
            }
            .debounce(FOLDER_CHANGE_DEBOUNCE_MS)
            .conflate()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getDownloadedFilesPage(
        offset: Int,
        limit: Int
    ): List<DownloadItem> {
        return withContext(Dispatchers.IO) {
            queryDownloadedFilesPage(
                offset = offset.coerceAtLeast(0),
                limit = limit.coerceIn(1, MAX_PAGE_SIZE_REQUEST)
            )
        }
    }

    override suspend fun startDownload(request: DownloadStartRequest): Long {
        val id = System.currentTimeMillis()

        val fileName = buildSafeVideoFileName(
            title = request.title,
            fallbackId = id,
            downloadUrl = request.downloadUrl
        )

        enqueueDownload(
            id = id,
            request = request,
            fileName = fileName,
            replaceExisting = true
        )

        return id
    }

    override suspend fun pauseDownload(id: Long) {
        val item = progressStore.get(id) ?: return

        if (item.status != DownloadStatus.DOWNLOADING) {
            return
        }

        val tempFile = File(
            getTempDownloadFolder(),
            "${item.fileName}.part"
        )

        val downloadedBytes = tempFile
            .takeIf { it.exists() }
            ?.length()
            ?: item.downloadedBytes

        progressStore.upsert(
            item.copy(
                status = DownloadStatus.PAUSED,
                downloadedBytes = downloadedBytes,
                progress = calculateProgress(
                    downloadedBytes = downloadedBytes,
                    totalBytes = item.totalBytes
                ),
                speedBytesPerSec = 0L,
                lastEtaSeconds = -1L
            )
        )

        workManager.cancelUniqueWork(uniqueWorkName(id))
    }

    override suspend fun resumeDownload(id: Long) {
        val item = progressStore.get(id) ?: return

        if (item.status != DownloadStatus.PAUSED && item.status != DownloadStatus.FAILED) {
            return
        }

        enqueueDownload(
            id = item.id,
            request = DownloadStartRequest(
                sourceUrl = item.sourceUrl,
                downloadUrl = item.url,
                title = item.fileName.substringBeforeLast('.', item.fileName),
                platform = item.platform,
                quality = item.quality,
                thumbnailUrl = item.thumbnailUrl
            ),
            fileName = item.fileName,
            replaceExisting = true
        )
    }

    override suspend fun cancelDownload(id: Long) {
        val activeItem = progressStore.get(id)

        progressStore.remove(id)
        workManager.cancelUniqueWork(uniqueWorkName(id))

        if (activeItem != null) {
            File(
                getTempDownloadFolder(),
                "${activeItem.fileName}.part"
            ).delete()

            activeItem.filePath
                .takeIf { path -> path.isNotBlank() }
                ?.let { path -> File(path).delete() }

            return
        }

        findPublicDownloadedFileById(id)?.delete()
    }

    override suspend fun getAllDownloadedFiles(): List<DownloadItem> {
        return getDownloadedFilesPage(
            offset = 0,
            limit = DEFAULT_COMPLETED_PAGE_SIZE
        )
    }

    private suspend fun enqueueDownload(
        id: Long,
        request: DownloadStartRequest,
        fileName: String,
        replaceExisting: Boolean,
    ) {
        val tempFile = File(
            getTempDownloadFolder(),
            "$fileName.part"
        )

        val currentItem = progressStore.get(id)

        progressStore.upsert(
            DownloadItem(
                id = id,
                url = request.downloadUrl,
                sourceUrl = request.sourceUrl,
                fileName = fileName,
                filePath = tempFile.absolutePath,
                progress = currentItem?.progress ?: calculateProgress(
                    downloadedBytes = tempFile.length(),
                    totalBytes = currentItem?.totalBytes ?: 0L
                ),
                status = DownloadStatus.DOWNLOADING,
                downloadedBytes = tempFile.length(),
                totalBytes = currentItem?.totalBytes ?: 0L,
                speedBytesPerSec = 0L,
                lastEtaSeconds = -1L,
                platform = request.platform,
                quality = request.quality,
                thumbnailUrl = request.thumbnailUrl
            )
        )

        val notificationsEnabled = localDataStoreRepository
            .isNotificationsEnabled()
            .first()

        val downloadLocationType = localDataStoreRepository
            .getDownloadLocationType()
            .first()
            ?: DownloadLocationType.INTERNAL_STORAGE.storageValue

        val downloadLocationTreeUri = localDataStoreRepository
            .getDownloadLocationTreeUri()
            .first()
            .orEmpty()

        val inputData = Data.Builder()
            .putLong(VideoDownloadWorker.KEY_ID, id)
            .putString(VideoDownloadWorker.KEY_URL, request.downloadUrl)
            .putString(VideoDownloadWorker.KEY_SOURCE_URL, request.sourceUrl)
            .putString(VideoDownloadWorker.KEY_FILE_NAME, fileName)
            .putString(VideoDownloadWorker.KEY_PLATFORM, request.platform)
            .putString(VideoDownloadWorker.KEY_QUALITY, request.quality)
            .putString(VideoDownloadWorker.KEY_THUMBNAIL_URL, request.thumbnailUrl.orEmpty())
            .putBoolean(VideoDownloadWorker.KEY_NOTIFICATIONS_ENABLED, notificationsEnabled)
            .putString(VideoDownloadWorker.KEY_DOWNLOAD_LOCATION_TYPE, downloadLocationType)
            .putString(VideoDownloadWorker.KEY_DOWNLOAD_LOCATION_TREE_URI, downloadLocationTreeUri)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(WORK_TAG)
            .addTag(uniqueWorkName(id))
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName(id),
            if (replaceExisting) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun observePublicDownloadFolderChanges(): Flow<Unit> {
        return callbackFlow {
            val folder = getPublicDownloadFolder()

            trySend(Unit)

            val fileObserver = object : FileObserver(
                folder.absolutePath,
                FILE_OBSERVER_EVENTS
            ) {
                override fun onEvent(
                    event: Int,
                    path: String?
                ) {
                    if (event shouldTriggerFolderRescanFor path) {
                        trySend(Unit)
                    }
                }
            }

            val mediaStoreObserver = object : ContentObserver(
                Handler(Looper.getMainLooper())
            ) {
                override fun onChange(selfChange: Boolean) {
                    trySend(Unit)
                }

                override fun onChange(
                    selfChange: Boolean,
                    uri: android.net.Uri?
                ) {
                    trySend(Unit)
                }
            }

            fileObserver.startWatching()

            appContext.contentResolver.registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true,
                mediaStoreObserver
            )

            awaitClose {
                fileObserver.stopWatching()

                runCatching {
                    appContext.contentResolver.unregisterContentObserver(mediaStoreObserver)
                }
            }
        }
    }

    private infix fun Int.shouldTriggerFolderRescanFor(path: String?): Boolean {
        val cleanEvent = this and FileObserver.ALL_EVENTS

        val isSupportedEvent = cleanEvent == FileObserver.CREATE ||
                cleanEvent == FileObserver.DELETE ||
                cleanEvent == FileObserver.MOVED_FROM ||
                cleanEvent == FileObserver.MOVED_TO ||
                cleanEvent == FileObserver.CLOSE_WRITE ||
                cleanEvent == FileObserver.MODIFY ||
                cleanEvent == FileObserver.DELETE_SELF ||
                cleanEvent == FileObserver.MOVE_SELF

        if (!isSupportedEvent) {
            return false
        }

        if (cleanEvent == FileObserver.DELETE_SELF || cleanEvent == FileObserver.MOVE_SELF) {
            return true
        }

        if (path.isNullOrBlank()) {
            return true
        }

        return path.substringAfterLast('.')
            .lowercase(Locale.US) in SUPPORTED_VIDEO_EXTENSIONS
    }

    private fun queryDownloadedFilesPage(
        offset: Int,
        limit: Int
    ): List<DownloadItem> {
        val mediaStoreItems = queryMediaStoreDownloadedFilesPage(
            offset = offset,
            limit = limit
        )

        if (mediaStoreItems.isNotEmpty() || offset > 0) {
            return mediaStoreItems
        }

        return queryFolderDownloadedFilesPage(
            offset = offset,
            limit = limit
        )
    }

    private fun queryMediaStoreDownloadedFilesPage(
        offset: Int,
        limit: Int
    ): List<DownloadItem> {
        val folder = getPublicDownloadFolder()
        val isAndroidQOrAbove = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

        val projection = if (isAndroidQOrAbove) {
            arrayOf(
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATE_ADDED
            )
        } else {
            arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATE_ADDED
            )
        }

        val selection = if (isAndroidQOrAbove) {
            "${MediaStore.Video.Media.RELATIVE_PATH}=? AND ${MediaStore.Video.Media.IS_PENDING}=0"
        } else {
            "${MediaStore.Video.Media.DATA} LIKE ?"
        }

        val selectionArgs = if (isAndroidQOrAbove) {
            arrayOf(
                "${Environment.DIRECTORY_MOVIES}/$PUBLIC_FOLDER_NAME/"
            )
        } else {
            arrayOf(
                "${folder.absolutePath}/%"
            )
        }

        val sortOrder = """
            ${MediaStore.Video.Media.DATE_MODIFIED} DESC,
            ${MediaStore.Video.Media.DATE_ADDED} DESC
            LIMIT $limit OFFSET $offset
        """.trimIndent()

        val result = mutableListOf<DownloadItem>()

        return runCatching {
            appContext.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val displayNameColumn = cursor.getColumnIndexOrThrow(
                    MediaStore.Video.Media.DISPLAY_NAME
                )
                val sizeColumn = cursor.getColumnIndexOrThrow(
                    MediaStore.Video.Media.SIZE
                )

                val dataColumn = if (!isAndroidQOrAbove) {
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                } else {
                    -1
                }

                while (cursor.moveToNext()) {
                    val fileName = cursor.getString(displayNameColumn).orEmpty()

                    if (!fileName.hasSupportedVideoExtension()) {
                        continue
                    }

                    val absolutePath = if (isAndroidQOrAbove) {
                        File(folder, fileName).absolutePath
                    } else {
                        cursor.getString(dataColumn).orEmpty()
                    }

                    val file = File(absolutePath)

                    if (!file.exists() || !file.isFile) {
                        continue
                    }

                    val size = cursor.getLong(sizeColumn)
                        .takeIf { value -> value > 0L }
                        ?: file.length()

                    result.add(
                        file.toCompletedDownloadItem(
                            sizeBytes = size
                        )
                    )
                }
            }

            result
        }.getOrElse {
            queryFolderDownloadedFilesPage(
                offset = offset,
                limit = limit
            )
        }
    }

    private fun queryFolderDownloadedFilesPage(
        offset: Int,
        limit: Int
    ): List<DownloadItem> {
        val folder = getPublicDownloadFolder()

        if (!folder.exists()) {
            return emptyList()
        }

        return folder
            .listFiles()
            ?.asSequence()
            ?.filter { file ->
                file.isFile &&
                        !file.name.endsWith(PART_FILE_EXTENSION) &&
                        file.name.hasSupportedVideoExtension()
            }
            ?.sortedByDescending { file ->
                file.lastModified()
            }
            ?.drop(offset)
            ?.take(limit)
            ?.map { file ->
                file.toCompletedDownloadItem(
                    sizeBytes = file.length()
                )
            }
            ?.toList()
            .orEmpty()
    }

    private fun File.toCompletedDownloadItem(
        sizeBytes: Long
    ): DownloadItem {
        return DownloadItem(
            id = absolutePath.hashCode().toLong(),
            url = absolutePath,
            sourceUrl = absolutePath,
            fileName = name,
            filePath = absolutePath,
            progress = 100,
            status = DownloadStatus.SUCCESS,
            downloadedBytes = sizeBytes,
            totalBytes = sizeBytes,
            speedBytesPerSec = 0L,
            lastEtaSeconds = 0L,
            platform = "",
            quality = "Downloaded",
            thumbnailUrl = null
        )
    }

    private fun findPublicDownloadedFileById(id: Long): File? {
        val folder = getPublicDownloadFolder()

        if (!folder.exists()) {
            return null
        }

        return folder
            .listFiles()
            ?.firstOrNull { file ->
                file.isFile &&
                        file.absolutePath.hashCode().toLong() == id
            }
    }

    private fun List<DownloadItem>.filterActiveDownloads(): List<DownloadItem> {
        return filter { item ->
            item.status == DownloadStatus.DOWNLOADING ||
                    item.status == DownloadStatus.PAUSED ||
                    item.status == DownloadStatus.FAILED
        }
    }

    private fun String.hasSupportedVideoExtension(): Boolean {
        return substringAfterLast('.', missingDelimiterValue = "")
            .lowercase(Locale.US) in SUPPORTED_VIDEO_EXTENSIONS
    }

    private fun getTempDownloadFolder(): File {
        val baseFolder = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: appContext.filesDir

        return File(
            baseFolder,
            TEMP_FOLDER_NAME
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
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

    private fun calculateProgress(
        downloadedBytes: Long,
        totalBytes: Long,
    ): Int {
        if (totalBytes <= 0L) return 0

        return ((downloadedBytes * 100L) / totalBytes)
            .toInt()
            .coerceIn(0, 100)
    }

    private fun buildSafeVideoFileName(
        title: String,
        fallbackId: Long,
        downloadUrl: String,
    ): String {
        val safeTitle = title
            .trim()
            .ifBlank { "video_$fallbackId" }
            .replace(ILLEGAL_FILE_CHARS_REGEX, "_")
            .replace(MULTIPLE_UNDERSCORES_REGEX, "_")
            .trim('_', '.', ' ')
            .take(MAX_FILE_NAME_BASE_LENGTH)
            .ifBlank { "video_$fallbackId" }

        val extension = resolveVideoExtension(downloadUrl)

        return "$safeTitle.$extension"
    }

    private fun resolveVideoExtension(downloadUrl: String): String {
        val path = runCatching {
            URI(downloadUrl).path.orEmpty()
        }.getOrDefault(downloadUrl)

        val extension = path
            .substringAfterLast('.', missingDelimiterValue = "")
            .lowercase(Locale.US)
            .takeIf { value -> value in SUPPORTED_VIDEO_EXTENSIONS }

        return extension ?: DEFAULT_VIDEO_EXTENSION
    }

    private fun uniqueWorkName(id: Long): String {
        return "$WORK_TAG-$id"
    }

    private companion object {
        private const val PUBLIC_FOLDER_NAME = "Video Downloader"
        private const val TEMP_FOLDER_NAME = "DownloadParts"
        private const val WORK_TAG = "video_download"
        private const val PART_FILE_EXTENSION = ".part"
        private const val DEFAULT_VIDEO_EXTENSION = "mp4"
        private const val MAX_FILE_NAME_BASE_LENGTH = 80

        private const val DEFAULT_COMPLETED_PAGE_SIZE = 50
        private const val MAX_PAGE_SIZE_REQUEST = 150
        private const val FOLDER_CHANGE_DEBOUNCE_MS = 600L

        private val FILE_OBSERVER_EVENTS =
            FileObserver.CREATE or
                    FileObserver.DELETE or
                    FileObserver.MOVED_FROM or
                    FileObserver.MOVED_TO or
                    FileObserver.CLOSE_WRITE or
                    FileObserver.MODIFY or
                    FileObserver.DELETE_SELF or
                    FileObserver.MOVE_SELF

        private val ILLEGAL_FILE_CHARS_REGEX = Regex("""[\\/:*?"<>|]+""")
        private val MULTIPLE_UNDERSCORES_REGEX = Regex("_+")

        private val SUPPORTED_VIDEO_EXTENSIONS = setOf(
            "mp4",
            "mkv",
            "webm",
            "mov",
            "avi",
            "m4v"
        )
    }
}