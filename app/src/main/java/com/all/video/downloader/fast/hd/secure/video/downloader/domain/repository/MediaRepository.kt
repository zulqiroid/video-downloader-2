package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun observeVideos(): Flow<List<MediaItem>>

    fun observeAudios(): Flow<List<MediaItem>>

    fun observeVideoChanges(): Flow<Unit>

    fun observeAudioChanges(): Flow<Unit>

    suspend fun getVideosPage(
        offset: Int,
        limit: Int
    ): List<MediaItem>

    suspend fun getAudiosPage(
        offset: Int,
        limit: Int
    ): List<MediaItem>
}