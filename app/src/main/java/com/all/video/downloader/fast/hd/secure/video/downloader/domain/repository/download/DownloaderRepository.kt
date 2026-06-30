package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.download

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.download.DownloadFetchResult

interface DownloaderRepository {

    suspend fun fetchDownloadInfo(
        inputUrl: String
    ): DownloadFetchResult
}