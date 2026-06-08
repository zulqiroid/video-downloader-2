package com.video.downloader.data.repository.implementation.videoToMp3

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
 import androidx.work.workDataOf
import com.video.downloader.data.media.audio.Mp3MediaStoreWriter
import com.video.downloader.data.worker.videoToMp3.VideoToMp3Worker
import com.video.downloader.domain.models.videoToMp3.Mp3AudioConfig
import com.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Status
import com.video.downloader.domain.repository.videoToMp3.VideoToMp3Repository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoToMp3RepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val mp3MediaStoreWriter: Mp3MediaStoreWriter
) : VideoToMp3Repository {

    private val appContext = context.applicationContext
    private val workManager = WorkManager.getInstance(appContext)

    override fun observeConversions(): Flow<List<VideoToMp3ConversionItem>> {
        return workManager
            .getWorkInfosByTagFlow(WORK_TAG)
            .map { workInfos ->
                workInfos
                    .map { workInfo ->
                        workInfo.toConversionItem()
                    }
                    .sortedWith(
                        compareByDescending<VideoToMp3ConversionItem> {
                            it.status.priority
                        }.thenByDescending {
                            it.id
                        }
                    )
            }
    }

    override suspend fun startConversion(
        inputUri: String,
        fileName: String,
        preset: VideoToMp3Preset
    ): String {
        val uniqueId = System.currentTimeMillis().toString()

        val request = OneTimeWorkRequestBuilder<VideoToMp3Worker>()
            .setInputData(
                workDataOf(
                    VideoToMp3Worker.KEY_INPUT_URI to inputUri,
                    VideoToMp3Worker.KEY_FILE_NAME to fileName,
                    VideoToMp3Worker.KEY_PRESET to preset.name
                )
            )
            .addTag(WORK_TAG)
            .addTag(uniqueWorkName(uniqueId))
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName(uniqueId),
            ExistingWorkPolicy.KEEP,
            request
        )

        return request.id.toString()
    }

    override suspend fun cancelConversion(id: String) {
        runCatching {
            workManager.cancelWorkById(UUID.fromString(id))
        }
    }

    override suspend fun saveConvertedMp3(
        tempOutputPath: String,
        outputNameWithoutExtension: String
    ): String {
        return mp3MediaStoreWriter.saveTempMp3ToMusic(
            tempFilePath = tempOutputPath,
            outputNameWithoutExtension = outputNameWithoutExtension
        )
    }

    private fun WorkInfo.toConversionItem(): VideoToMp3ConversionItem {
        val inputUri = progress.getString(VideoToMp3Worker.KEY_INPUT_URI)
            ?: outputData.getString(VideoToMp3Worker.KEY_INPUT_URI)
            ?: ""

        val fileName = progress.getString(VideoToMp3Worker.KEY_FILE_NAME)
            ?: outputData.getString(VideoToMp3Worker.KEY_FILE_NAME)
            ?: "Video to MP3"

        val selectedPreset = runCatching {
            val rawPreset = progress.getString(VideoToMp3Worker.KEY_PRESET)
                ?: outputData.getString(VideoToMp3Worker.KEY_PRESET)
                ?: VideoToMp3Preset.AUTO_RECOMMENDED.name

            VideoToMp3Preset.valueOf(rawPreset)
        }.getOrDefault(VideoToMp3Preset.AUTO_RECOMMENDED)

        val progressValue = if (state == WorkInfo.State.SUCCEEDED) {
            100
        } else {
            progress.getInt(VideoToMp3Worker.KEY_PROGRESS, 0)
        }.coerceIn(0, 100)

        val tempOutputPath = progress.getString(VideoToMp3Worker.KEY_TEMP_OUTPUT_PATH)
            ?: outputData.getString(VideoToMp3Worker.KEY_TEMP_OUTPUT_PATH)

        val savedOutputUri = outputData.getString(VideoToMp3Worker.KEY_OUTPUT_URI)
            ?: progress.getString(VideoToMp3Worker.KEY_OUTPUT_URI)

        val bitrate = progress.getInt(
            VideoToMp3Worker.KEY_BITRATE,
            outputData.getInt(VideoToMp3Worker.KEY_BITRATE, 0)
        )

        val channelCount = progress.getInt(
            VideoToMp3Worker.KEY_CHANNEL_COUNT,
            outputData.getInt(VideoToMp3Worker.KEY_CHANNEL_COUNT, 0)
        )

        val resolvedPresetName = progress.getString(VideoToMp3Worker.KEY_RESOLVED_PRESET)
            ?: outputData.getString(VideoToMp3Worker.KEY_RESOLVED_PRESET)

        val resolvedDescription = outputData.getString(VideoToMp3Worker.KEY_RESOLVED_DESCRIPTION)
            ?: ""

        val resolvedConfig = if (
            resolvedPresetName != null &&
            bitrate > 0 &&
            channelCount > 0
        ) {
            Mp3AudioConfig(
                preset = selectedPreset,
                bitrateKbps = bitrate,
                channelCount = channelCount,
                displayName = resolvedPresetName,
                description = resolvedDescription
            )
        } else {
            null
        }

        val fileSize = outputData.getLong(
            VideoToMp3Worker.KEY_FILE_SIZE_BYTES,
            0L
        )

        val errorMessage = outputData.getString(VideoToMp3Worker.KEY_ERROR)

        return VideoToMp3ConversionItem(
            id = id.toString(),
            inputUri = inputUri,
            fileName = fileName,
            selectedPreset = selectedPreset,
            resolvedConfig = resolvedConfig,
            progress = progressValue,
            status = state.toVideoToMp3Status(),
            tempOutputPath = tempOutputPath,
            savedOutputUri = savedOutputUri,
            fileSizeBytes = fileSize,
            errorMessage = errorMessage
        )
    }

    private fun WorkInfo.State.toVideoToMp3Status(): VideoToMp3Status {
        return when (this) {
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.BLOCKED -> VideoToMp3Status.QUEUED

            WorkInfo.State.RUNNING -> VideoToMp3Status.CONVERTING

            WorkInfo.State.SUCCEEDED -> VideoToMp3Status.READY_TO_SAVE

            WorkInfo.State.FAILED -> VideoToMp3Status.FAILED

            WorkInfo.State.CANCELLED -> VideoToMp3Status.CANCELLED
        }
    }

    private val VideoToMp3Status.priority: Int
        get() {
            return when (this) {
                VideoToMp3Status.CONVERTING -> 6
                VideoToMp3Status.QUEUED -> 5
                VideoToMp3Status.READY_TO_SAVE -> 4
                VideoToMp3Status.SAVING -> 3
                VideoToMp3Status.SAVED -> 2
                VideoToMp3Status.FAILED -> 1
                VideoToMp3Status.CANCELLED -> 0
            }
        }

    private fun uniqueWorkName(id: String): String {
        return "$WORK_TAG-$id"
    }

    private companion object {
        private const val WORK_TAG = "video_to_mp3"
    }
}