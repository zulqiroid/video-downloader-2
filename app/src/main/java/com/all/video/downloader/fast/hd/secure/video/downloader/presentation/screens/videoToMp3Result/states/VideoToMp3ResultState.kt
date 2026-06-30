package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem

data class VideoToMp3ResultState(
    val conversionId: String? = null,
    val item: VideoToMp3ConversionItem? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val savedOutputUri: String? = null,
    val errorMessage: String? = null
)