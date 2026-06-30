package com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.videoSplitter

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.all.video.downloader.fast.hd.secure.video.downloader.data.media.videoSplitter.VideoSplitMediaStoreWriter
import com.all.video.downloader.fast.hd.secure.video.downloader.data.worker.videoSplitter.VideoSplitterWorker
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitClip
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitOutputPayload
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitRange
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitterJobItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitterJobStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.videoSplitter.VideoSplitterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoSplitterRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val mediaStoreWriter: VideoSplitMediaStoreWriter
) : VideoSplitterRepository {

    private val appContext = context.applicationContext
    private val workManager = WorkManager.getInstance(appContext)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun observeSplitJobs(): Flow<List<VideoSplitterJobItem>> {
        return workManager
            .getWorkInfosByTagFlow(WORK_TAG)
            .map { workInfos ->
                workInfos
                    .map { info ->
                        info.toJobItem()
                    }
                    .sortedByDescending { item ->
                        item.id
                    }
            }
    }

    override suspend fun startSplit(
        inputUri: String,
        fileName: String,
        ranges: List<VideoSplitRange>
    ): String {
        val uniqueId = System.currentTimeMillis().toString()

        val request = OneTimeWorkRequestBuilder<VideoSplitterWorker>()
            .setInputData(
                workDataOf(
                    VideoSplitterWorker.KEY_INPUT_URI to inputUri,
                    VideoSplitterWorker.KEY_FILE_NAME to fileName,
                    VideoSplitterWorker.KEY_RANGES_JSON to json.encodeToString(ranges)
                )
            )
            .addTag(WORK_TAG)
            .addTag(uniqueWorkName(uniqueId))
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName(uniqueId),
            ExistingWorkPolicy.REPLACE,
            request
        )

        return request.id.toString()
    }

    override suspend fun saveClipToGallery(
        clip: VideoSplitClip
    ): String {
        clip.savedUri?.let {
            return it
        }

        val tempFile = File(clip.filePath)

        val savedUri = mediaStoreWriter.saveClipToMovies(
            tempFile = tempFile,
            fileName = clip.fileName
        )

        return savedUri.toString()
    }

    override suspend fun cancelSplit(id: String) {
        runCatching {
            workManager.cancelWorkById(
                java.util.UUID.fromString(id)
            )
        }
    }

    private fun WorkInfo.toJobItem(): VideoSplitterJobItem {
        val fileName = progress.getString(VideoSplitterWorker.KEY_FILE_NAME)
            ?: outputData.getString(VideoSplitterWorker.KEY_FILE_NAME)
            ?: "Video Splitter"

        val progressValue = progress.getInt(
            VideoSplitterWorker.KEY_PROGRESS,
            if (state == WorkInfo.State.SUCCEEDED) 100 else 0
        )

        val resultJson = outputData.getString(VideoSplitterWorker.KEY_RESULT_JSON)
            ?: progress.getString(VideoSplitterWorker.KEY_RESULT_JSON)

        val clips = resultJson
            ?.let { raw ->
                runCatching {
                    json.decodeFromString<VideoSplitOutputPayload>(raw).clips
                }.getOrDefault(emptyList())
            }
            .orEmpty()

        val error = outputData.getString(VideoSplitterWorker.KEY_ERROR)

        return VideoSplitterJobItem(
            id = id.toString(),
            fileName = fileName,
            progress = progressValue,
            status = state.toVideoSplitterStatus(),
            clips = clips,
            errorMessage = error
        )
    }

    private fun WorkInfo.State.toVideoSplitterStatus(): VideoSplitterJobStatus {
        return when (this) {
            WorkInfo.State.ENQUEUED -> VideoSplitterJobStatus.QUEUED
            WorkInfo.State.RUNNING -> VideoSplitterJobStatus.SPLITTING
            WorkInfo.State.SUCCEEDED -> VideoSplitterJobStatus.SUCCESS
            WorkInfo.State.FAILED -> VideoSplitterJobStatus.FAILED
            WorkInfo.State.CANCELLED -> VideoSplitterJobStatus.CANCELLED
            WorkInfo.State.BLOCKED -> VideoSplitterJobStatus.QUEUED
        }
    }

    private fun uniqueWorkName(id: String): String {
        return "$WORK_TAG-$id"
    }

    private companion object {
        private const val WORK_TAG = "video_splitter"
    }
}