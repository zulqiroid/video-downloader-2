package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.mapper.toMediaItems
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStartRequest
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.VideoDownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class DownloadUseCases @Inject constructor(
    val startDownload: StartDownloadUseCase,
    val observeDownloads: ObserveDownloadsUseCase,
    val observeDownloadedFileChanges: ObserveDownloadedFileChangesUseCase,
    val getDownloadedFilesPage: GetDownloadedFilesPageUseCase,
    val pauseDownload: PauseDownloadUseCase,
    val resumeDownload: ResumeDownloadUseCase,
    val cancelDownload: CancelDownloadUseCase,
    val getDownloadedFiles: GetDownloadedFilesUseCase
)

class StartDownloadUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(request: DownloadStartRequest): Long {
        return repository.startDownload(request)
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

class ObserveDownloadedFileChangesUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    operator fun invoke(): Flow<Unit> {
        return repository.observeDownloadedFileChanges()
    }
}

class GetDownloadedFilesPageUseCase @Inject constructor(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(
        offset: Int,
        limit: Int
    ): List<MediaItem> {
        return repository.getDownloadedFilesPage(
            offset = offset,
            limit = limit
        ).toMediaItems()
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