package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states

import androidx.compose.runtime.Immutable

@Immutable
data class MediaPlayerGestureFeedbackState(
    val isVisible: Boolean = false,
    val type: MediaPlayerGestureFeedbackType = MediaPlayerGestureFeedbackType.Brightness,
    val percent: Int = 0,
)

enum class MediaPlayerGestureFeedbackType {
    Brightness,
    Volume
}