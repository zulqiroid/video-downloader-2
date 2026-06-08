package com.video.downloader.presentation.screens.videoSplitter.events

import android.net.Uri

sealed interface VideoSplitterEvents {

    data object BackClicked : VideoSplitterEvents

    data object UploadVideoClicked : VideoSplitterEvents

    data class VideoSelected(
        val uri: Uri,
        val fileName: String,
        val durationMs: Long,
        val sizeBytes: Long
    ) : VideoSplitterEvents

    data class RangeChanged(
        val startMs: Long,
        val endMs: Long
    ) : VideoSplitterEvents

    data object SplitVideoClicked : VideoSplitterEvents

    data class CancelSplitClicked(
        val id: String
    ) : VideoSplitterEvents

    data class SaveClipClicked(
        val clipId: String
    ) : VideoSplitterEvents

    data object SavePermissionDenied : VideoSplitterEvents

    data object ResultBackClicked : VideoSplitterEvents
}