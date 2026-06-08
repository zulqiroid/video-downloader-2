package com.video.downloader.domain.models.videoSplitter

import kotlinx.serialization.Serializable

@Serializable
data class VideoSplitRange(
    val id: String,
    val startMs: Long,
    val endMs: Long
) {
    val durationMs: Long
        get() = (endMs - startMs).coerceAtLeast(0L)
}

@Serializable
data class VideoSplitClip(
    val id: String,
    val fileName: String,
    val filePath: String,
    val startMs: Long,
    val endMs: Long,
    val sizeBytes: Long,
    val savedUri: String? = null
) {
    val isSaved: Boolean
        get() = !savedUri.isNullOrBlank()
}

@Serializable
data class VideoSplitOutputPayload(
    val clips: List<VideoSplitClip>
)

enum class VideoSplitterJobStatus {
    QUEUED,
    SPLITTING,
    SUCCESS,
    FAILED,
    CANCELLED
}

data class VideoSplitterJobItem(
    val id: String,
    val fileName: String,
    val progress: Int,
    val status: VideoSplitterJobStatus,
    val clips: List<VideoSplitClip> = emptyList(),
    val errorMessage: String? = null
)