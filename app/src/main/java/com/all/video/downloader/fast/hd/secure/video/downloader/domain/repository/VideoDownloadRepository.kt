package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStartRequest
import kotlinx.coroutines.flow.Flow

interface VideoDownloadRepository {

    fun observeDownloads(): Flow<List<DownloadItem>>

    fun observeDownloadedFileChanges(): Flow<Unit>

    suspend fun getDownloadedFilesPage(
        offset: Int,
        limit: Int
    ): List<DownloadItem>

    suspend fun startDownload(request: DownloadStartRequest): Long

    suspend fun pauseDownload(id: Long)

    suspend fun resumeDownload(id: Long)

    suspend fun cancelDownload(id: Long)

    suspend fun getAllDownloadedFiles(): List<DownloadItem>
}