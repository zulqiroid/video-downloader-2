package com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.all.video.downloader.fast.hd.secure.video.downloader.di.IoDispatcher
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaSource
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaType
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MediaRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeVideos(): Flow<List<MediaItem>> {
        return observeVideoChanges()
            .mapLatest {
                getVideosPage(
                    offset = 0,
                    limit = DEFAULT_PAGE_SIZE
                )
            }
            .distinctUntilChanged()
            .flowOn(ioDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeAudios(): Flow<List<MediaItem>> {
        return observeAudioChanges()
            .mapLatest {
                getAudiosPage(
                    offset = 0,
                    limit = DEFAULT_PAGE_SIZE
                )
            }
            .distinctUntilChanged()
            .flowOn(ioDispatcher)
    }

    override fun observeVideoChanges(): Flow<Unit> {
        return observeMediaStoreChanges(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun observeAudioChanges(): Flow<Unit> {
        return observeMediaStoreChanges(
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override suspend fun getVideosPage(
        offset: Int,
        limit: Int
    ): List<MediaItem> {
        return withContext(ioDispatcher) {
            queryVideosPage(
                offset = offset.coerceAtLeast(0),
                limit = limit.coerceIn(1, MAX_PAGE_SIZE)
            )
        }
    }

    override suspend fun getAudiosPage(
        offset: Int,
        limit: Int
    ): List<MediaItem> {
        return withContext(ioDispatcher) {
            queryAudiosPage(
                offset = offset.coerceAtLeast(0),
                limit = limit.coerceIn(1, MAX_PAGE_SIZE)
            )
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeMediaStoreChanges(
        uri: Uri
    ): Flow<Unit> {
        return callbackFlow {
            val observer = object : ContentObserver(
                Handler(Looper.getMainLooper())
            ) {
                override fun onChange(selfChange: Boolean) {
                    trySend(Unit)
                }

                override fun onChange(
                    selfChange: Boolean,
                    uri: Uri?
                ) {
                    trySend(Unit)
                }

                override fun onChange(
                    selfChange: Boolean,
                    uris: Collection<Uri>,
                    flags: Int
                ) {
                    trySend(Unit)
                }
            }

            context.contentResolver.registerContentObserver(
                uri,
                true,
                observer
            )

            trySend(Unit)

            awaitClose {
                runCatching {
                    context.contentResolver.unregisterContentObserver(observer)
                }
            }
        }
            .debounce(MEDIA_CHANGE_DEBOUNCE_MS)
            .conflate()
            .flowOn(ioDispatcher)
    }

    private fun queryVideosPage(
        offset: Int,
        limit: Int
    ): List<MediaItem> {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val result = mutableListOf<MediaItem>()

        queryMediaStore(
            uri = uri,
            projection = buildVideoProjection(),
            selection = buildCommonSelection(),
            sortColumn = MediaStore.Video.Media.DATE_ADDED,
            offset = offset,
            limit = limit
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                cursor.toVideoMediaItem(uri)?.let { item ->
                    result.add(item)
                }
            }
        }

        return result
    }

    private fun queryAudiosPage(
        offset: Int,
        limit: Int
    ): List<MediaItem> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val result = mutableListOf<MediaItem>()

        queryMediaStore(
            uri = uri,
            projection = buildAudioProjection(),
            selection = buildCommonSelection(),
            sortColumn = MediaStore.Audio.Media.DATE_ADDED,
            offset = offset,
            limit = limit
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                cursor.toAudioMediaItem(uri)?.let { item ->
                    result.add(item)
                }
            }
        }

        return result
    }

    private fun queryMediaStore(
        uri: Uri,
        projection: Array<String>,
        selection: String?,
        sortColumn: String,
        offset: Int,
        limit: Int
    ): Cursor? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val queryArgs = Bundle().apply {
                putString(
                    ContentResolverQueryArgs.SQL_SELECTION,
                    selection
                )
                putStringArray(
                    ContentResolverQueryArgs.SQL_SELECTION_ARGS,
                    null
                )
                putStringArray(
                    ContentResolverQueryArgs.SORT_COLUMNS,
                    arrayOf(sortColumn)
                )
                putInt(
                    ContentResolverQueryArgs.SORT_DIRECTION,
                    ContentResolverQueryArgs.SORT_DIRECTION_DESCENDING
                )
                putInt(
                    ContentResolverQueryArgs.LIMIT,
                    limit
                )
                putInt(
                    ContentResolverQueryArgs.OFFSET,
                    offset
                )
            }

            context.contentResolver.query(
                uri,
                projection,
                queryArgs,
                null
            )
        } else {
            context.contentResolver.query(
                uri,
                projection,
                selection,
                null,
                "$sortColumn DESC LIMIT $limit OFFSET $offset"
            )
        }
    }

    private fun Cursor.toVideoMediaItem(
        collectionUri: Uri
    ): MediaItem? {
        val id = getLongOrDefault(MediaStore.Video.Media._ID)

        if (id <= 0L) return null

        val contentUri = ContentUris.withAppendedId(
            collectionUri,
            id
        )

        val displayName = getStringOrEmpty(MediaStore.Video.Media.DISPLAY_NAME)
        val filePath = getStringOrEmpty(MediaStore.Video.Media.DATA)
        val sizeBytes = getLongOrDefault(MediaStore.Video.Media.SIZE)
        val durationMillis = getLongOrDefault(MediaStore.Video.Media.DURATION)
        val width = getIntOrDefault(MediaStore.Video.Media.WIDTH)
        val height = getIntOrDefault(MediaStore.Video.Media.HEIGHT)
        val mimeType = getStringOrEmpty(MediaStore.Video.Media.MIME_TYPE)
        val dateAddedSeconds = getLongOrDefault(MediaStore.Video.Media.DATE_ADDED)
        val dateModifiedSeconds = getLongOrDefault(MediaStore.Video.Media.DATE_MODIFIED)
        val bucketId = getStringOrEmpty(MediaStore.Video.Media.BUCKET_ID)
        val bucketName = getStringOrEmpty(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

        val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getStringOrEmpty(MediaStore.Video.Media.RELATIVE_PATH)
        } else {
            ""
        }

        val ownerPackageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getStringOrEmpty(MediaStore.Video.Media.OWNER_PACKAGE_NAME)
        } else {
            ""
        }

        val volumeName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getStringOrEmpty(MediaStore.Video.Media.VOLUME_NAME)
        } else {
            ""
        }

        return MediaItem(
            id = id,
            mediaStoreId = id,
            title = displayName.removeFileExtension(),
            displayName = displayName,
            fileName = displayName,
            uri = contentUri.toString(),
            filePath = filePath,
            relativePath = relativePath,
            parentPath = filePath.parentPath(),
            extension = displayName.fileExtension(),
            mimeType = mimeType,
            mediaType = MediaType.VIDEO,
            mediaSource = MediaSource.DEVICE,
            sizeBytes = sizeBytes,
            formattedSize = formatSize(sizeBytes),
            durationMillis = durationMillis,
            formattedDuration = formatDuration(durationMillis),
            width = width,
            height = height,
            resolution = buildResolutionLabel(width, height),
            qualityLabel = buildQualityLabel(width, height),
            frameRate = null,
            bitrate = null,
            rotation = null,
            dateAddedSeconds = dateAddedSeconds,
            dateModifiedSeconds = dateModifiedSeconds,
            formattedDate = formatDate(dateAddedSeconds),
            bucketId = bucketId,
            bucketName = bucketName,
            ownerPackageName = ownerPackageName,
            volumeName = volumeName,
            isPlayable = filePath.isNotBlank() || contentUri.toString().isNotBlank(),
            isDownloaded = false,
            extra = emptyMap()
        )
    }

    private fun Cursor.toAudioMediaItem(
        collectionUri: Uri
    ): MediaItem? {
        val id = getLongOrDefault(MediaStore.Audio.Media._ID)

        if (id <= 0L) return null

        val contentUri = ContentUris.withAppendedId(
            collectionUri,
            id
        )

        val displayName = getStringOrEmpty(MediaStore.Audio.Media.DISPLAY_NAME)
        val filePath = getStringOrEmpty(MediaStore.Audio.Media.DATA)
        val sizeBytes = getLongOrDefault(MediaStore.Audio.Media.SIZE)
        val durationMillis = getLongOrDefault(MediaStore.Audio.Media.DURATION)
        val mimeType = getStringOrEmpty(MediaStore.Audio.Media.MIME_TYPE)
        val dateAddedSeconds = getLongOrDefault(MediaStore.Audio.Media.DATE_ADDED)
        val dateModifiedSeconds = getLongOrDefault(MediaStore.Audio.Media.DATE_MODIFIED)

        val artist = getStringOrEmpty(MediaStore.Audio.Media.ARTIST)
        val album = getStringOrEmpty(MediaStore.Audio.Media.ALBUM)
        val albumArtist = getStringOrEmpty(MediaStore.Audio.Media.ALBUM_ARTIST)
        val composer = getStringOrEmpty(MediaStore.Audio.Media.COMPOSER)
        val trackNumber = getIntOrNull(MediaStore.Audio.Media.TRACK)
        val year = getIntOrNull(MediaStore.Audio.Media.YEAR)

        val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getStringOrEmpty(MediaStore.Audio.Media.RELATIVE_PATH)
        } else {
            ""
        }

        val ownerPackageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getStringOrEmpty(MediaStore.Audio.Media.OWNER_PACKAGE_NAME)
        } else {
            ""
        }

        val volumeName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getStringOrEmpty(MediaStore.Audio.Media.VOLUME_NAME)
        } else {
            ""
        }

        return MediaItem(
            id = id,
            mediaStoreId = id,
            title = displayName.removeFileExtension(),
            displayName = displayName,
            fileName = displayName,
            uri = contentUri.toString(),
            filePath = filePath,
            relativePath = relativePath,
            parentPath = filePath.parentPath(),
            extension = displayName.fileExtension(),
            mimeType = mimeType,
            mediaType = MediaType.AUDIO,
            mediaSource = MediaSource.DEVICE,
            sizeBytes = sizeBytes,
            formattedSize = formatSize(sizeBytes),
            durationMillis = durationMillis,
            formattedDuration = formatDuration(durationMillis),
            artist = artist,
            album = album,
            albumArtist = albumArtist,
            composer = composer,
            trackNumber = trackNumber,
            year = year,
            audioBitrate = null,
            sampleRate = null,
            channelCount = null,
            dateAddedSeconds = dateAddedSeconds,
            dateModifiedSeconds = dateModifiedSeconds,
            formattedDate = formatDate(dateAddedSeconds),
            ownerPackageName = ownerPackageName,
            volumeName = volumeName,
            isPlayable = filePath.isNotBlank() || contentUri.toString().isNotBlank(),
            isDownloaded = false,
            extra = emptyMap()
        )
    }

    private fun buildCommonSelection(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${MediaStore.MediaColumns.SIZE} > 0 AND ${MediaStore.MediaColumns.IS_PENDING}=0"
        } else {
            "${MediaStore.MediaColumns.SIZE} > 0"
        }
    }

    private fun buildVideoProjection(): Array<String> {
        val projection = mutableListOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection.add(MediaStore.Video.Media.RELATIVE_PATH)
            projection.add(MediaStore.Video.Media.OWNER_PACKAGE_NAME)
            projection.add(MediaStore.Video.Media.VOLUME_NAME)
        }

        return projection.distinct().toTypedArray()
    }

    private fun buildAudioProjection(): Array<String> {
        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection.add(MediaStore.Audio.Media.RELATIVE_PATH)
            projection.add(MediaStore.Audio.Media.OWNER_PACKAGE_NAME)
            projection.add(MediaStore.Audio.Media.VOLUME_NAME)
        }

        return projection.distinct().toTypedArray()
    }

    private fun buildResolutionLabel(
        width: Int,
        height: Int
    ): String {
        return if (width > 0 && height > 0) {
            "${width}x$height"
        } else {
            ""
        }
    }

    private fun buildQualityLabel(
        width: Int,
        height: Int
    ): String {
        return when {
            height >= 2160 -> "4K"
            height >= 1440 -> "1440p"
            height >= 1080 -> "1080p"
            height >= 720 -> "720p"
            height > 0 -> "${height}p"
            width > 0 -> "${width}px"
            else -> ""
        }
    }

    private fun formatDuration(durationMillis: Long): String {
        if (durationMillis <= 0L) return "00:00"

        val totalSeconds = durationMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    private fun formatSize(size: Long): String {
        if (size <= 0L) return "0 B"

        val kb = 1024.0
        val mb = kb * 1024.0
        val gb = mb * 1024.0

        return when {
            size >= gb -> String.format(Locale.US, "%.2f GB", size / gb)
            size >= mb -> String.format(Locale.US, "%.2f MB", size / mb)
            size >= kb -> String.format(Locale.US, "%.2f KB", size / kb)
            else -> "$size B"
        }
    }

    private fun formatDate(dateAddedSeconds: Long): String {
        if (dateAddedSeconds <= 0L) return ""

        return try {
            val millis = dateAddedSeconds * 1000L
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            formatter.format(Date(millis))
        } catch (_: Throwable) {
            ""
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

    private fun String.parentPath(): String {
        if (isBlank()) return ""

        return try {
            File(this).parent.orEmpty()
        } catch (_: Throwable) {
            ""
        }
    }

    private fun Cursor.getStringOrEmpty(columnName: String): String {
        val index = getColumnIndex(columnName)
        if (index == -1 || isNull(index)) return ""
        return getString(index).orEmpty()
    }

    private fun Cursor.getLongOrDefault(
        columnName: String,
        defaultValue: Long = 0L
    ): Long {
        val index = getColumnIndex(columnName)
        if (index == -1 || isNull(index)) return defaultValue
        return getLong(index)
    }

    private fun Cursor.getIntOrDefault(
        columnName: String,
        defaultValue: Int = 0
    ): Int {
        val index = getColumnIndex(columnName)
        if (index == -1 || isNull(index)) return defaultValue
        return getInt(index)
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        val index = getColumnIndex(columnName)
        if (index == -1 || isNull(index)) return null
        return getInt(index)
    }

    private object ContentResolverQueryArgs {
        const val SQL_SELECTION = "android:query-arg-sql-selection"
        const val SQL_SELECTION_ARGS = "android:query-arg-sql-selection-args"
        const val SORT_COLUMNS = "android:query-arg-sort-columns"
        const val SORT_DIRECTION = "android:query-arg-sort-direction"
        const val SORT_DIRECTION_DESCENDING = 1
        const val LIMIT = "android:query-arg-limit"
        const val OFFSET = "android:query-arg-offset"
    }

    private companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        private const val MAX_PAGE_SIZE = 100
        private const val MEDIA_CHANGE_DEBOUNCE_MS = 500L
    }
}