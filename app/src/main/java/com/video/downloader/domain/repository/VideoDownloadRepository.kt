package com.video.downloader.domain.repository

import com.video.downloader.domain.models.DownloadItem
import kotlinx.coroutines.flow.Flow

interface VideoDownloadRepository {

    fun observeDownloads(): Flow<List<DownloadItem>>

    suspend fun startDownload(url: String): Long

    suspend fun pauseDownload(id: Long)

    suspend fun resumeDownload(id: Long)

    suspend fun cancelDownload(id: Long)

    suspend fun getAllDownloadedFiles(): List<DownloadItem>
}