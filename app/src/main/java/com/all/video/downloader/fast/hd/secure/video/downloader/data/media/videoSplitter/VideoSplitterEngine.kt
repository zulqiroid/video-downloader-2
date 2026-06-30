package com.all.video.downloader.fast.hd.secure.video.downloader.data.media.videoSplitter

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import com.all.video.downloader.fast.hd.secure.video.downloader.di.IoDispatcher
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitClip
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitRange
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.math.max

class VideoSplitterEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun split(
        inputUriString: String,
        sourceFileName: String,
        ranges: List<VideoSplitRange>,
        onProgress: suspend (Int) -> Unit
    ): List<VideoSplitClip> {
        return withContext(ioDispatcher) {
            require(inputUriString.isNotBlank()) {
                "Invalid video file."
            }

            require(ranges.isNotEmpty()) {
                "Please select at least one split range."
            }

            val inputUri = Uri.parse(inputUriString)
            val safeSourceName = sanitizeFileName(sourceFileName)
                .ifBlank { "video_${System.currentTimeMillis()}.mp4" }

            val outputDirectory = File(
                context.cacheDir,
                "video_splitter/jobs/${System.currentTimeMillis()}"
            ).apply {
                mkdirs()
            }

            val clips = mutableListOf<VideoSplitClip>()

            ranges.forEachIndexed { index, range ->
                coroutineContext.ensureActive()

                require(range.endMs > range.startMs) {
                    "Please select a valid split range."
                }

                val outputFileName = buildOutputFileName(
                    sourceFileName = safeSourceName,
                    index = index
                )

                val outputFile = File(
                    outputDirectory,
                    outputFileName
                )

                val clip = copyRangeToMp4(
                    inputUri = inputUri,
                    outputFile = outputFile,
                    range = range,
                    clipIndex = index,
                    totalClips = ranges.size,
                    onProgress = onProgress
                )

                clips += clip.copy(
                    fileName = outputFileName
                )
            }

            onProgress(100)

            clips
        }
    }

    private suspend fun copyRangeToMp4(
        inputUri: Uri,
        outputFile: File,
        range: VideoSplitRange,
        clipIndex: Int,
        totalClips: Int,
        onProgress: suspend (Int) -> Unit
    ): VideoSplitClip {
        val extractor = MediaExtractor()
        var muxer: MediaMuxer? = null
        var muxerStarted = false
        var wroteSamples = false

        try {
            extractor.setDataSource(context, inputUri, null)

            muxer = MediaMuxer(
                outputFile.absolutePath,
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
            )

            setOrientationHintIfAvailable(
                uri = inputUri,
                muxer = muxer
            )

            val trackIndexMap = mutableMapOf<Int, Int>()
            var maxInputSize = DEFAULT_BUFFER_SIZE

            for (trackIndex in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(trackIndex)
                val mime = format.getString(MediaFormat.KEY_MIME).orEmpty()

                val shouldCopyTrack =
                    mime.startsWith("video/") ||
                            mime.startsWith("audio/")

                if (!shouldCopyTrack) continue

                val muxerTrackIndex = muxer.addTrack(format)
                trackIndexMap[trackIndex] = muxerTrackIndex

                if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                    maxInputSize = max(
                        maxInputSize,
                        format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
                    )
                }

                extractor.selectTrack(trackIndex)
            }

            require(trackIndexMap.isNotEmpty()) {
                "This file does not contain supported video or audio tracks."
            }

            muxer.start()
            muxerStarted = true

            val startUs = range.startMs.coerceAtLeast(0L) * 1000L
            val endUs = range.endMs.coerceAtLeast(range.startMs + 1L) * 1000L
            val durationUs = (endUs - startUs).coerceAtLeast(1L)

            extractor.seekTo(
                startUs,
                MediaExtractor.SEEK_TO_PREVIOUS_SYNC
            )

            val buffer = ByteBuffer.allocateDirect(maxInputSize)
            val bufferInfo = android.media.MediaCodec.BufferInfo()

            var basePresentationTimeUs: Long? = null
            var lastProgress = -1

            while (true) {
                coroutineContext.ensureActive()

                val sampleTrackIndex = extractor.sampleTrackIndex

                if (sampleTrackIndex < 0) {
                    break
                }

                val sampleTimeUs = extractor.sampleTime

                if (sampleTimeUs < 0L || sampleTimeUs > endUs) {
                    break
                }

                val muxerTrackIndex = trackIndexMap[sampleTrackIndex]

                if (muxerTrackIndex == null) {
                    extractor.advance()
                    continue
                }

                buffer.clear()

                val sampleSize = extractor.readSampleData(
                    buffer,
                    0
                )

                if (sampleSize < 0) {
                    break
                }

                if (basePresentationTimeUs == null) {
                    basePresentationTimeUs = sampleTimeUs
                }

                bufferInfo.set(
                    0,
                    sampleSize,
                    (sampleTimeUs - basePresentationTimeUs!!).coerceAtLeast(0L),
                    extractor.sampleFlags
                )

                buffer.position(0)
                buffer.limit(sampleSize)

                muxer.writeSampleData(
                    muxerTrackIndex,
                    buffer,
                    bufferInfo
                )

                wroteSamples = true

                val clipProgress = (((sampleTimeUs - startUs).coerceAtLeast(0L) * 100L) / durationUs)
                    .toInt()
                    .coerceIn(0, 100)

                val globalProgress = calculateGlobalProgress(
                    clipIndex = clipIndex,
                    totalClips = totalClips,
                    clipProgress = clipProgress
                )

                if (globalProgress != lastProgress && globalProgress % 2 == 0) {
                    lastProgress = globalProgress
                    onProgress(globalProgress)
                }

                extractor.advance()
            }

            require(wroteSamples) {
                "Selected range did not contain playable video samples."
            }

            muxer.stop()

            return VideoSplitClip(
                id = UUID.randomUUID().toString(),
                fileName = outputFile.name,
                filePath = outputFile.absolutePath,
                startMs = range.startMs,
                endMs = range.endMs,
                sizeBytes = outputFile.length()
            )
        } catch (throwable: Throwable) {
            outputFile.delete()
            throw throwable
        } finally {
            extractor.release()

            runCatching {
                if (muxerStarted && !wroteSamples) {
                    muxer?.stop()
                }
            }

            runCatching {
                muxer?.release()
            }
        }
    }

    private fun setOrientationHintIfAvailable(
        uri: Uri,
        muxer: MediaMuxer
    ) {
        runCatching {
            val retriever = MediaMetadataRetriever()

            try {
                retriever.setDataSource(context, uri)

                val rotation = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
                )?.toIntOrNull() ?: 0

                if (rotation == 90 || rotation == 180 || rotation == 270) {
                    muxer.setOrientationHint(rotation)
                }
            } finally {
                retriever.release()
            }
        }
    }

    private fun calculateGlobalProgress(
        clipIndex: Int,
        totalClips: Int,
        clipProgress: Int
    ): Int {
        val safeTotal = totalClips.coerceAtLeast(1)
        val base = (clipIndex * 100) / safeTotal
        val span = 100 / safeTotal

        return (base + ((clipProgress * span) / 100))
            .coerceIn(0, 99)
    }

    private fun buildOutputFileName(
        sourceFileName: String,
        index: Int
    ): String {
        val baseName = sourceFileName
            .substringBeforeLast(".")
            .ifBlank { "Clip" }

        return "${baseName}_Part${index + 1}.mp4"
    }

    private fun sanitizeFileName(name: String): String {
        return name
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .trim()
    }

    private companion object {
        private const val DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024
    }
}