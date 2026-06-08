package com.video.downloader.data.repository.implementation

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.video.downloader.di.IoDispatcher
import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.models.MediaSource
import com.video.downloader.domain.models.MediaType
import com.video.downloader.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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

    override fun observeVideos(): Flow<List<MediaItem>> {
        return observeMediaStore(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            query = ::queryVideos
        )
    }

    override fun observeAudios(): Flow<List<MediaItem>> {
        return observeMediaStore(
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            query = ::queryAudios
        )
    }

    private fun observeMediaStore(
        uri: Uri,
        query: suspend () -> List<MediaItem>
    ): Flow<List<MediaItem>> {
        return callbackFlow {
            fun refresh() {
                launch(ioDispatcher) {
                    trySend(query())
                }
            }

            val observer = object : ContentObserver(
                Handler(Looper.getMainLooper())
            ) {
                override fun onChange(selfChange: Boolean) {
                    refresh()
                }

                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    refresh()
                }
            }

            context.contentResolver.registerContentObserver(
                uri,
                true,
                observer
            )

            refresh()

            awaitClose {
                context.contentResolver.unregisterContentObserver(observer)
            }
        }
            .conflate()
            .distinctUntilChanged()
    }

    private suspend fun queryVideos(): List<MediaItem> {
        return withContext(ioDispatcher) {
            val list = mutableListOf<MediaItem>()

            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            context.contentResolver.query(
                uri,
                buildVideoProjection(),
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->

                while (cursor.moveToNext()) {
                    val id = cursor.getLongOrDefault(MediaStore.Video.Media._ID)
                    val contentUri = ContentUris.withAppendedId(uri, id)

                    val displayName = cursor.getStringOrEmpty(MediaStore.Video.Media.DISPLAY_NAME)
                    val filePath = cursor.getStringOrEmpty(MediaStore.Video.Media.DATA)
                    val sizeBytes = cursor.getLongOrDefault(MediaStore.Video.Media.SIZE)
                    val durationMillis = cursor.getLongOrDefault(MediaStore.Video.Media.DURATION)
                    val width = cursor.getIntOrDefault(MediaStore.Video.Media.WIDTH)
                    val height = cursor.getIntOrDefault(MediaStore.Video.Media.HEIGHT)
                    val mimeType = cursor.getStringOrEmpty(MediaStore.Video.Media.MIME_TYPE)
                    val dateAddedSeconds = cursor.getLongOrDefault(MediaStore.Video.Media.DATE_ADDED)
                    val dateModifiedSeconds = cursor.getLongOrDefault(MediaStore.Video.Media.DATE_MODIFIED)
                    val bucketId = cursor.getStringOrEmpty(MediaStore.Video.Media.BUCKET_ID)
                    val bucketName = cursor.getStringOrEmpty(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

                    val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getStringOrEmpty(MediaStore.Video.Media.RELATIVE_PATH)
                    } else {
                        ""
                    }

                    val ownerPackageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getStringOrEmpty(MediaStore.Video.Media.OWNER_PACKAGE_NAME)
                    } else {
                        ""
                    }

                    val volumeName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getStringOrEmpty(MediaStore.Video.Media.VOLUME_NAME)
                    } else {
                        ""
                    }

                    val retrieverMetadata = readMetadata(contentUri)

                    val finalDuration = durationMillis.takeIf { it > 0L }
                        ?: retrieverMetadata.durationMillis

                    val finalWidth = width.takeIf { it > 0 }
                        ?: retrieverMetadata.width

                    val finalHeight = height.takeIf { it > 0 }
                        ?: retrieverMetadata.height

                    val finalMimeType = mimeType.ifBlank {
                        retrieverMetadata.mimeType
                    }

                    val finalBitrate = retrieverMetadata.bitrate

                    list.add(
                        MediaItem(
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
                            mimeType = finalMimeType,

                            mediaType = MediaType.VIDEO,
                            mediaSource = MediaSource.DEVICE,

                            sizeBytes = sizeBytes,
                            formattedSize = formatSize(sizeBytes),

                            durationMillis = finalDuration,
                            formattedDuration = formatDuration(finalDuration),

                            width = finalWidth,
                            height = finalHeight,
                            resolution = buildResolutionLabel(finalWidth, finalHeight),
                            qualityLabel = buildQualityLabel(finalWidth, finalHeight),
                            frameRate = retrieverMetadata.frameRate,
                            bitrate = finalBitrate,
                            rotation = retrieverMetadata.rotation,

                            dateAddedSeconds = dateAddedSeconds,
                            dateModifiedSeconds = dateModifiedSeconds,
                            formattedDate = formatDate(dateAddedSeconds),

                            bucketId = bucketId,
                            bucketName = bucketName,
                            ownerPackageName = ownerPackageName,
                            volumeName = volumeName,

                            isPlayable = filePath.isNotBlank() || contentUri.toString().isNotBlank(),
                            isDownloaded = false,

                            extra = mapOf(
                                "hasAudio" to retrieverMetadata.hasAudio.toString(),
                                "hasVideo" to retrieverMetadata.hasVideo.toString()
                            )
                        )
                    )
                }
            }

            list
        }
    }

    private suspend fun queryAudios(): List<MediaItem> {
        return withContext(ioDispatcher) {
            val list = mutableListOf<MediaItem>()

            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            context.contentResolver.query(
                uri,
                buildAudioProjection(),
                null,
                null,
                "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            )?.use { cursor ->

                while (cursor.moveToNext()) {
                    val id = cursor.getLongOrDefault(MediaStore.Audio.Media._ID)
                    val contentUri = ContentUris.withAppendedId(uri, id)

                    val displayName = cursor.getStringOrEmpty(MediaStore.Audio.Media.DISPLAY_NAME)
                    val filePath = cursor.getStringOrEmpty(MediaStore.Audio.Media.DATA)
                    val sizeBytes = cursor.getLongOrDefault(MediaStore.Audio.Media.SIZE)
                    val durationMillis = cursor.getLongOrDefault(MediaStore.Audio.Media.DURATION)
                    val mimeType = cursor.getStringOrEmpty(MediaStore.Audio.Media.MIME_TYPE)
                    val dateAddedSeconds = cursor.getLongOrDefault(MediaStore.Audio.Media.DATE_ADDED)
                    val dateModifiedSeconds = cursor.getLongOrDefault(MediaStore.Audio.Media.DATE_MODIFIED)

                    val artist = cursor.getStringOrEmpty(MediaStore.Audio.Media.ARTIST)
                    val album = cursor.getStringOrEmpty(MediaStore.Audio.Media.ALBUM)
                    val albumArtist = cursor.getStringOrEmpty(MediaStore.Audio.Media.ALBUM_ARTIST)
                    val composer = cursor.getStringOrEmpty(MediaStore.Audio.Media.COMPOSER)
                    val trackNumber = cursor.getIntOrNull(MediaStore.Audio.Media.TRACK)
                    val year = cursor.getIntOrNull(MediaStore.Audio.Media.YEAR)

                    val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getStringOrEmpty(MediaStore.Audio.Media.RELATIVE_PATH)
                    } else {
                        ""
                    }

                    val ownerPackageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getStringOrEmpty(MediaStore.Audio.Media.OWNER_PACKAGE_NAME)
                    } else {
                        ""
                    }

                    val volumeName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getStringOrEmpty(MediaStore.Audio.Media.VOLUME_NAME)
                    } else {
                        ""
                    }

                    val retrieverMetadata = readMetadata(contentUri)

                    val finalDuration = durationMillis.takeIf { it > 0L }
                        ?: retrieverMetadata.durationMillis

                    val finalMimeType = mimeType.ifBlank {
                        retrieverMetadata.mimeType
                    }

                    list.add(
                        MediaItem(
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
                            mimeType = finalMimeType,

                            mediaType = MediaType.AUDIO,
                            mediaSource = MediaSource.DEVICE,

                            sizeBytes = sizeBytes,
                            formattedSize = formatSize(sizeBytes),

                            durationMillis = finalDuration,
                            formattedDuration = formatDuration(finalDuration),

                            artist = artist,
                            album = album,
                            albumArtist = albumArtist,
                            composer = composer,
                            trackNumber = trackNumber,
                            year = year,
                            audioBitrate = retrieverMetadata.bitrate,
                            sampleRate = retrieverMetadata.sampleRate,
                            channelCount = retrieverMetadata.channelCount,

                            dateAddedSeconds = dateAddedSeconds,
                            dateModifiedSeconds = dateModifiedSeconds,
                            formattedDate = formatDate(dateAddedSeconds),

                            ownerPackageName = ownerPackageName,
                            volumeName = volumeName,

                            isPlayable = filePath.isNotBlank() || contentUri.toString().isNotBlank(),
                            isDownloaded = false,

                            extra = mapOf(
                                "hasAudio" to retrieverMetadata.hasAudio.toString(),
                                "hasVideo" to retrieverMetadata.hasVideo.toString()
                            )
                        )
                    )
                }
            }

            list
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

    private fun readMetadata(uri: Uri): RetrieverMetadata {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(context, uri)

            RetrieverMetadata(
                durationMillis = retriever.extractLong(MediaMetadataRetriever.METADATA_KEY_DURATION),
                width = retriever.extractInt(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),
                height = retriever.extractInt(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT),
                rotation = retriever.extractIntOrNull(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION),
                bitrate = retriever.extractLongOrNull(MediaMetadataRetriever.METADATA_KEY_BITRATE),
                mimeType = retriever.extractString(MediaMetadataRetriever.METADATA_KEY_MIMETYPE),
                hasAudio = retriever.extractString(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO).isNotBlank(),
                hasVideo = retriever.extractString(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO).isNotBlank(),
                sampleRate = retriever.extractIntOrNull(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE),
                channelCount = retriever.extractIntOrNull(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS),
                frameRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    retriever.extractFloatOrNull(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
                } else {
                    null
                }
            )
        } catch (_: Throwable) {
            RetrieverMetadata()
        } finally {
            try {
                retriever.release()
            } catch (_: Throwable) {
            }
        }
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

    private fun MediaMetadataRetriever.extractString(keyCode: Int): String {
        return extractMetadata(keyCode).orEmpty()
    }

    private fun MediaMetadataRetriever.extractLong(keyCode: Int): Long {
        return extractMetadata(keyCode)?.toLongOrNull() ?: 0L
    }

    private fun MediaMetadataRetriever.extractLongOrNull(keyCode: Int): Long? {
        return extractMetadata(keyCode)?.toLongOrNull()
    }

    private fun MediaMetadataRetriever.extractInt(keyCode: Int): Int {
        return extractMetadata(keyCode)?.toIntOrNull() ?: 0
    }

    private fun MediaMetadataRetriever.extractIntOrNull(keyCode: Int): Int? {
        return extractMetadata(keyCode)?.toIntOrNull()
    }

    private fun MediaMetadataRetriever.extractFloatOrNull(keyCode: Int): Float? {
        return extractMetadata(keyCode)?.toFloatOrNull()
    }

    private data class RetrieverMetadata(
        val durationMillis: Long = 0L,
        val width: Int = 0,
        val height: Int = 0,
        val rotation: Int? = null,
        val bitrate: Long? = null,
        val mimeType: String = "",
        val hasAudio: Boolean = false,
        val hasVideo: Boolean = false,
        val sampleRate: Int? = null,
        val channelCount: Int? = null,
        val frameRate: Float? = null
    )
}