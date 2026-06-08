package com.video.downloader.domain.repository

import com.video.downloader.domain.models.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun observeVideos(): Flow<List<MediaItem>>

    fun observeAudios(): Flow<List<MediaItem>>
}