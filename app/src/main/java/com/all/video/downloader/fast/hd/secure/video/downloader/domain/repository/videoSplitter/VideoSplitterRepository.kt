package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.videoSplitter

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitClip
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitRange
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitterJobItem
import kotlinx.coroutines.flow.Flow

interface VideoSplitterRepository {

    fun observeSplitJobs(): Flow<List<VideoSplitterJobItem>>

    suspend fun startSplit(
        inputUri: String,
        fileName: String,
        ranges: List<VideoSplitRange>
    ): String

    suspend fun saveClipToGallery(
        clip: VideoSplitClip
    ): String

    suspend fun cancelSplit(id: String)
}