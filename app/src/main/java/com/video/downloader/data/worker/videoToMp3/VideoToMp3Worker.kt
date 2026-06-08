package com.video.downloader.data.worker.videoToMp3

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.video.downloader.data.encoder.audio.VideoToMp3ConversionEngine
import com.video.downloader.data.media.audio.VideoAudioMetadataReader
import com.video.downloader.data.media.audio.VideoToMp3PresetResolver
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ensureActive
import java.io.File
import kotlin.coroutines.coroutineContext

@HiltWorker
class VideoToMp3Worker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val metadataReader: VideoAudioMetadataReader,
    private val presetResolver: VideoToMp3PresetResolver,
    private val conversionEngine: VideoToMp3ConversionEngine,
    private val notificationHelper: VideoToMp3NotificationHelper
) : CoroutineWorker(
    appContext = appContext,
    params = workerParameters
) {

    override suspend fun doWork(): Result {
        val inputUri = inputData.getString(KEY_INPUT_URI).orEmpty()
        val fileName = inputData.getString(KEY_FILE_NAME).orEmpty()

        val presetName = inputData.getString(KEY_PRESET)
            ?: VideoToMp3Preset.AUTO_RECOMMENDED.name

        val selectedPreset = runCatching {
            VideoToMp3Preset.valueOf(presetName)
        }.getOrDefault(VideoToMp3Preset.AUTO_RECOMMENDED)

        if (inputUri.isBlank()) {
            return failure("Invalid video file.")
        }

        val safeFileName = fileName.ifBlank {
            "selected_video_${System.currentTimeMillis()}.mp4"
        }

        val outputBaseName = safeFileName
            .substringBeforeLast(".")
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .ifBlank { "converted_audio_${System.currentTimeMillis()}" }

        val tempFolder = File(
            applicationContext.filesDir,
            TEMP_FOLDER_NAME
        ).apply {
            if (!exists()) mkdirs()
        }

        val tempOutputFile = File(
            tempFolder,
            "${outputBaseName}_${System.currentTimeMillis()}.mp3"
        )

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
                    inputUri = inputUri,
                    fileName = safeFileName,
                    selectedPreset = selectedPreset
                )
            )

            val metadata = metadataReader.read(inputUri)

            val config = presetResolver.resolve(
                selectedPreset = selectedPreset,
                metadata = metadata
            )

            conversionEngine.convertToTempFile(
                inputUriString = inputUri,
                tempOutputFile = tempOutputFile,
                config = config,
                onProgress = { progress ->
                    coroutineContext.ensureActive()

                    val safeProgress = progress.coerceIn(0, 100)

                    setProgress(
                        progressData(
                            progress = safeProgress,
                            inputUri = inputUri,
                            fileName = safeFileName,
                            selectedPreset = selectedPreset,
                            tempOutputPath = tempOutputFile.absolutePath,
                            resolvedPreset = config.displayName,
                            bitrateKbps = config.bitrateKbps,
                            channelCount = config.channelCount
                        )
                    )

                    setForeground(
                        notificationHelper.createForegroundInfo(
                            context = applicationContext,
                            workId = id,
                            fileName = safeFileName,
                            progress = safeProgress
                        )
                    )
                }
            )

            val finalSize = tempOutputFile.length()

            if (!tempOutputFile.exists() || finalSize <= 0L) {
                return failure("MP3 conversion failed. Output file is empty.")
            }

            Result.success(
                Data.Builder()
                    .putString(KEY_INPUT_URI, inputUri)
                    .putString(KEY_FILE_NAME, safeFileName)
                    .putString(KEY_PRESET, selectedPreset.name)
                    .putString(KEY_TEMP_OUTPUT_PATH, tempOutputFile.absolutePath)
                    .putString(KEY_RESOLVED_PRESET, config.displayName)
                    .putString(KEY_RESOLVED_DESCRIPTION, config.description)
                    .putInt(KEY_BITRATE, config.bitrateKbps)
                    .putInt(KEY_CHANNEL_COUNT, config.channelCount)
                    .putLong(KEY_FILE_SIZE_BYTES, finalSize)
                    .putInt(KEY_PROGRESS, 100)
                    .build()
            )
        } catch (throwable: Throwable) {
            tempOutputFile.delete()

            failure(
                throwable.message ?: "Unable to convert this video to MP3."
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
        inputUri: String? = null,
        fileName: String? = null,
        selectedPreset: VideoToMp3Preset? = null,
        tempOutputPath: String? = null,
        resolvedPreset: String? = null,
        bitrateKbps: Int? = null,
        channelCount: Int? = null
    ): Data {
        val builder = Data.Builder()
            .putInt(KEY_PROGRESS, progress)

        inputUri?.let {
            builder.putString(KEY_INPUT_URI, it)
        }

        fileName?.let {
            builder.putString(KEY_FILE_NAME, it)
        }

        selectedPreset?.let {
            builder.putString(KEY_PRESET, it.name)
        }

        tempOutputPath?.let {
            builder.putString(KEY_TEMP_OUTPUT_PATH, it)
        }

        resolvedPreset?.let {
            builder.putString(KEY_RESOLVED_PRESET, it)
        }

        bitrateKbps?.let {
            builder.putInt(KEY_BITRATE, it)
        }

        channelCount?.let {
            builder.putInt(KEY_CHANNEL_COUNT, it)
        }

        return builder.build()
    }

    companion object {
        const val KEY_INPUT_URI = "input_uri"
        const val KEY_FILE_NAME = "file_name"
        const val KEY_PRESET = "preset"

        const val KEY_PROGRESS = "progress"
        const val KEY_TEMP_OUTPUT_PATH = "temp_output_path"
        const val KEY_OUTPUT_URI = "output_uri"
        const val KEY_ERROR = "error"

        const val KEY_RESOLVED_PRESET = "resolved_preset"
        const val KEY_RESOLVED_DESCRIPTION = "resolved_description"
        const val KEY_BITRATE = "bitrate"
        const val KEY_CHANNEL_COUNT = "channel_count"
        const val KEY_FILE_SIZE_BYTES = "file_size_bytes"

        private const val TEMP_FOLDER_NAME = "video_to_mp3_temp"
    }
}