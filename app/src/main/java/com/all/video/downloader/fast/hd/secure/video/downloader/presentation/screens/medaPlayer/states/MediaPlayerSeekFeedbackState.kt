package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states

import androidx.compose.runtime.Immutable

@Immutable
data class MediaPlayerSeekFeedbackState(
    val isVisible: Boolean = false,
    val direction: MediaPlayerSeekFeedbackDirection = MediaPlayerSeekFeedbackDirection.Forward,
    val seconds: Int = DEFAULT_SEEK_FEEDBACK_SECONDS,
) {
    companion object {
        const val DEFAULT_SEEK_FEEDBACK_SECONDS = 10
    }
}

enum class MediaPlayerSeekFeedbackDirection {
    Rewind,
    Forward
}