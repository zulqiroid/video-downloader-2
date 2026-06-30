package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitClip

data class VideoSplitterStates(
    val selectedVideoUri: String? = null,
    val selectedVideoName: String = "",
    val selectedVideoSizeBytes: Long = 0L,
    val durationMs: Long = 0L,

    val rangeStartMs: Long = 0L,
    val rangeEndMs: Long = 0L,

    val isLoading: Boolean = false,
    val isSplitting: Boolean = false,
    val splitProgress: Int = 0,
    val currentJobId: String? = null,

    val showResultScreen: Boolean = false,
    val resultClips: List<VideoSplitClip> = emptyList(),

    val isSavingClip: Boolean = false,
    val savingClipId: String? = null,
    val saveSuccessMessage: String? = null,

    val errorMessage: String? = null
) {
    val hasVideo: Boolean
        get() = !selectedVideoUri.isNullOrBlank()

    val canSplit: Boolean
        get() = hasVideo &&
                durationMs > 0L &&
                rangeEndMs > rangeStartMs &&
                !isSplitting

    val selectedRangeDurationMs: Long
        get() = (rangeEndMs - rangeStartMs).coerceAtLeast(0L)
}