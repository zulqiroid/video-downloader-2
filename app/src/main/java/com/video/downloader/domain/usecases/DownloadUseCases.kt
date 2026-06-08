package com.video.downloader.domain.usecases

import com.video.downloader.domain.mapper.toMediaItems
 import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.repository.VideoDownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class DownloadUseCases @Inject constructor(
    val startDownload: StartDownloadUseCase,
    val observeDownloads: ObserveDownloadsUseCase,
    val pauseDownload: PauseDownloadUseCase,
    val resumeDownload: ResumeDownloadUseCase,
    val cancelDownload: CancelDownloadUseCase,
    val getDownloadedFiles: GetDownloadedFilesUseCase
)

class StartDownloadUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(url: String): Long {
        return repository.startDownload(url)
    }
}

class ObserveDownloadsUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return repository.observeDownloads()
            .map { downloads ->
                downloads.toMediaItems()
            }
    }
}

class PauseDownloadUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.pauseDownload(id)
    }
}

class ResumeDownloadUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.resumeDownload(id)
    }
}

class CancelDownloadUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.cancelDownload(id)
    }
}

class GetDownloadedFilesUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(): List<MediaItem> {
        return repository.getAllDownloadedFiles()
            .toMediaItems()
    }
}