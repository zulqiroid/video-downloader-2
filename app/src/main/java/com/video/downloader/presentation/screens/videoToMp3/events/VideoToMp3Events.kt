package com.video.downloader.presentation.screens.videoToMp3.events

import android.net.Uri
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Preset

sealed interface VideoToMp3Events {
    data object BackClicked : VideoToMp3Events
    data object UploadVideoClicked : VideoToMp3Events
    data class VideoSelected(
        val uri: Uri,
        val fileName: String
    ) : VideoToMp3Events

    data class PresetSelected(
        val preset: VideoToMp3Preset
    ) : VideoToMp3Events

    data object ConvertClicked : VideoToMp3Events
    data class CancelClicked(val id: String) : VideoToMp3Events
}