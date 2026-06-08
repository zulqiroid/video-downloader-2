package com.video.downloader.domain.models

data class MediaItem(
    // Basic identity
    val id: Long,
    val mediaStoreId: Long? = null,
    val title: String = "",
    val displayName: String = "",
    val fileName: String = "",

    // File/path info
    val uri: String = "",
    val filePath: String = "",
    val relativePath: String = "",
    val parentPath: String = "",
    val extension: String = "",
    val mimeType: String = "",

    // Media type/source
    val mediaType: MediaType = MediaType.UNKNOWN,
    val mediaSource: MediaSource = MediaSource.UNKNOWN,

    // Size
    val sizeBytes: Long = 0L,
    val formattedSize: String = "",

    // Duration
    val durationMillis: Long = 0L,
    val formattedDuration: String = "",

    // Video metadata
    val width: Int = 0,
    val height: Int = 0,
    val resolution: String = "",
    val qualityLabel: String = "",
    val frameRate: Float? = null,
    val bitrate: Long? = null,
    val rotation: Int? = null,

    // Audio metadata
    val artist: String = "",
    val album: String = "",
    val albumArtist: String = "",
    val composer: String = "",
    val genre: String = "",
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val year: Int? = null,
    val audioBitrate: Long? = null,
    val sampleRate: Int? = null,
    val channelCount: Int? = null,

    // Dates
    val dateAddedSeconds: Long = 0L,
    val dateModifiedSeconds: Long = 0L,
    val dateTakenMillis: Long? = null,
    val formattedDate: String = "",

    // Download info
    val url: String = "",
    val progress: Int = 100,
    val downloadStatus: DownloadStatus? = null,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val speedBytesPerSecond: Long = 0L,
    val etaSeconds: Long = -1L,
    val formattedEta: String = "",
    val workId: String? = null,

    // Playback/UI state
    val isPlayable: Boolean = true,
    val isSelected: Boolean = false,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,

    // Extra/future metadata
    val bucketId: String = "",
    val bucketName: String = "",
    val ownerPackageName: String = "",
    val volumeName: String = "",
    val documentId: String = "",
    val instanceId: String = "",

    // Flexible extra metadata for future use
    val extra: Map<String, String> = emptyMap()
)

enum class MediaType {
    VIDEO,
    AUDIO,
    IMAGE,
    DOCUMENT,
    UNKNOWN
}

enum class MediaSource {
    DEVICE,
    DOWNLOAD,
    STREAM,
    REMOTE,
    UNKNOWN
}