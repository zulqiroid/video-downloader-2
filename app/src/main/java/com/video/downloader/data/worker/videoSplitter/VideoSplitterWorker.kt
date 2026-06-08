package com.video.downloader.data.worker.videoSplitter

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.video.downloader.data.media.videoSplitter.VideoSplitterEngine
import com.video.downloader.domain.models.videoSplitter.VideoSplitOutputPayload
import com.video.downloader.domain.models.videoSplitter.VideoSplitRange
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.coroutineContext

@HiltWorker
class VideoSplitterWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val splitterEngine: VideoSplitterEngine,
    private val notificationHelper: VideoSplitterNotificationHelper
) : CoroutineWorker(
    appContext = appContext,
    params = workerParameters
) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun doWork(): Result {
        val inputUri = inputData.getString(KEY_INPUT_URI).orEmpty()
        val fileName = inputData.getString(KEY_FILE_NAME).orEmpty()
        val rangesJson = inputData.getString(KEY_RANGES_JSON).orEmpty()

        if (inputUri.isBlank()) {
            return failure("Invalid video file.")
        }

        val safeFileName = fileName.ifBlank {
            "selected_video.mp4"
        }

        val ranges = runCatching {
            json.decodeFromString<List<VideoSplitRange>>(rangesJson)
        }.getOrElse {
            return failure("Invalid split range.")
        }

        if (ranges.isEmpty()) {
            return failure("Please select at least one split range.")
        }

        return try {
            setForeground(
                notificationHelper.createForegroundInfo(
                    context = applicationContext,
                    workId = id,
                    fileName = safeFileName,
                    progress = 0
                )
            )

            setProgress(
                progressData(
                    progress = 0,
                    fileName = safeFileName
                )
            )

            val clips = splitterEngine.split(
                inputUriString = inputUri,
                sourceFileName = safeFileName,
                ranges = ranges,
                onProgress = { progress ->
                    coroutineContext.ensureActive()

                    setProgress(
                        progressData(
                            progress = progress,
                            fileName = safeFileName
                        )
                    )

                    setForeground(
                        notificationHelper.createForegroundInfo(
                            context = applicationContext,
                            workId = id,
                            fileName = safeFileName,
                            progress = progress
                        )
                    )
                }
            )

            val resultJson = json.encodeToString(
                VideoSplitOutputPayload(
                    clips = clips
                )
            )

            setProgress(
                progressData(
                    progress = 100,
                    fileName = safeFileName,
                    resultJson = resultJson
                )
            )

            Result.success(
                Data.Builder()
                    .putString(KEY_FILE_NAME, safeFileName)
                    .putString(KEY_RESULT_JSON, resultJson)
                    .putInt(KEY_PROGRESS, 100)
                    .build()
            )
        } catch (throwable: Throwable) {
            failure(
                throwable.message ?: "Unable to split this video."
            )
        }
    }

    private fun failure(message: String): Result {
        return Result.failure(
            Data.Builder()
                .putString(KEY_ERROR, message)
                .build()
        )
    }

    private fun progressData(
        progress: Int,
        fileName: String,
        resultJson: String? = null
    ): Data {
        val builder = Data.Builder()
            .putInt(KEY_PROGRESS, progress)
            .putString(KEY_FILE_NAME, fileName)

        resultJson?.let {
            builder.putString(KEY_RESULT_JSON, it)
        }

        return builder.build()
    }

    companion object {
        const val KEY_INPUT_URI = "input_uri"
        const val KEY_FILE_NAME = "file_name"
        const val KEY_RANGES_JSON = "ranges_json"

        const val KEY_PROGRESS = "progress"
        const val KEY_RESULT_JSON = "result_json"
        const val KEY_ERROR = "error"
    }
}