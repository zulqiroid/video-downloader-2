package com.video.downloader.domain.usecases.download

import com.video.downloader.domain.repository.download.DownloaderRepository
import javax.inject.Inject

data class DownloadUseCases @Inject constructor(
    val fetchDownloadInfo: FetchDownloadInfoUseCase
)

class FetchDownloadInfoUseCase @Inject constructor(
    private val repository: DownloaderRepository
) {
    suspend operator fun invoke(url: String) =
        repository.fetchDownloadInfo(url)
}