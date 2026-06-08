package com.video.downloader.presentation.screens.videoToMp3.states

import com.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Status
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Preset

data class VideoToMp3States(
    val selectedVideoUri: String? = null,
    val selectedVideoName: String = "",
    val selectedPreset: VideoToMp3Preset = VideoToMp3Preset.AUTO_RECOMMENDED,
    val isSubmitting: Boolean = false,
    val conversions: List<VideoToMp3ConversionItem> = emptyList(),
    val errorMessage: String? = null
) {
    val hasVideo: Boolean
        get() = !selectedVideoUri.isNullOrBlank()

    val activeConversion: VideoToMp3ConversionItem?
        get() = conversions.firstOrNull { item ->
            item.status == VideoToMp3Status.QUEUED ||
                    item.status == VideoToMp3Status.CONVERTING
        }
}