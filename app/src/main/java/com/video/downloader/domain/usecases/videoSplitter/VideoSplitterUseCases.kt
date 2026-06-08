package com.video.downloader.domain.usecases.videoSplitter

import com.video.downloader.domain.models.videoSplitter.VideoSplitClip
import com.video.downloader.domain.models.videoSplitter.VideoSplitRange
import com.video.downloader.domain.repository.videoSplitter.VideoSplitterRepository
import javax.inject.Inject

data class VideoSplitterUseCases @Inject constructor(
    val startSplit: StartVideoSplitUseCase,
    val observeSplitJobs: ObserveVideoSplitJobsUseCase,
    val saveClipToGallery: SaveVideoSplitClipToGalleryUseCase,
    val cancelSplit: CancelVideoSplitUseCase
)

class StartVideoSplitUseCase @Inject constructor(
    private val repository: VideoSplitterRepository
) {
    suspend operator fun invoke(
        inputUri: String,
        fileName: String,
        ranges: List<VideoSplitRange>
    ): String {
        return repository.startSplit(
            inputUri = inputUri,
            fileName = fileName,
            ranges = ranges
        )
    }
}

class ObserveVideoSplitJobsUseCase @Inject constructor(
    private val repository: VideoSplitterRepository
) {
    operator fun invoke() = repository.observeSplitJobs()
}

class SaveVideoSplitClipToGalleryUseCase @Inject constructor(
    private val repository: VideoSplitterRepository
) {
    suspend operator fun invoke(
        clip: VideoSplitClip
    ): String {
        return repository.saveClipToGallery(clip)
    }
}

class CancelVideoSplitUseCase @Inject constructor(
    private val repository: VideoSplitterRepository
) {
    suspend operator fun invoke(id: String) {
        repository.cancelSplit(id)
    }
}