package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadItem
import kotlinx.coroutines.flow.Flow

interface DownloadProgressStore {

    fun observeDownloads(): Flow<List<DownloadItem>>

    suspend fun upsert(item: DownloadItem)

    suspend fun remove(id: Long)

    suspend fun get(id: Long): DownloadItem?

    suspend fun getAll(): List<DownloadItem>

    suspend fun clear()
}