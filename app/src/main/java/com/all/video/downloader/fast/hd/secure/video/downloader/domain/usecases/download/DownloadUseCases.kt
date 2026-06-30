package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.download

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.download.DownloaderRepository
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