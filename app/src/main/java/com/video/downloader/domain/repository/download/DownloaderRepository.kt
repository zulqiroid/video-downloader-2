package com.video.downloader.domain.repository.download

import com.video.downloader.domain.download.DownloadFetchResult

interface DownloaderRepository {

    suspend fun fetchDownloadInfo(
        inputUrl: String
    ): DownloadFetchResult
}