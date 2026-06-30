package com.all.video.downloader.fast.hd.secure.video.downloader.data.encoder.audio

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import com.all.video.downloader.fast.hd.secure.video.downloader.di.IoDispatcher
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.Mp3AudioConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class VideoToMp3ConversionEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lameEncoder: LameMp3Encoder,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
){

    suspend fun convertToTempFile(
        inputUriString: String,
        tempOutputFile: File,
        config: Mp3AudioConfig,
        onProgress: suspend (Int) -> Unit
    ) = withContext(ioDispatcher) {
        val extractor = MediaExtractor()
        var decoder: MediaCodec? = null

        try {
            if (tempOutputFile.exists()) {
                tempOutputFile.delete()
            }

            tempOutputFile.parentFile?.mkdirs()

            extractor.setDataSource(
                context,
                Uri.parse(inputUriString),
                null
            )

            val audioTrackIndex = findAudioTrack(extractor)

            if (audioTrackIndex == -1) {
                error("This video does not contain an audio track.")
            }

            extractor.selectTrack(audioTrackIndex)

            val inputFormat = extractor.getTrackFormat(audioTrackIndex)

            val mime = inputFormat.getString(MediaFormat.KEY_MIME)
                ?: error("Unsupported audio format.")

            val sampleRate = inputFormat.getIntegerOrDefault(
                MediaFormat.KEY_SAMPLE_RATE,
                44_100
            )

            val inputChannelCount = inputFormat.getIntegerOrDefault(
                MediaFormat.KEY_CHANNEL_COUNT,
                2
            ).coerceAtLeast(1)

            val durationUs = inputFormat.getLongOrDefault(
                MediaFormat.KEY_DURATION,
                0L
            )

            decoder = MediaCodec.createDecoderByType(mime)
            decoder.configure(inputFormat, null, null, 0)
            decoder.start()

            lameEncoder.open(
                sampleRate = sampleRate,
                channelCount = config.channelCount,
                bitrateKbps = config.bitrateKbps
            )

            FileOutputStream(tempOutputFile).use { outputStream ->
                decodeAndEncode(
                    extractor = extractor,
                    decoder = decoder,
                    inputChannelCount = inputChannelCount,
                    outputChannelCount = config.channelCount,
                    durationUs = durationUs,
                    outputStream = outputStream,
                    onProgress = onProgress
                )

                val flushedBytes = lameEncoder.flush()

                if (flushedBytes.isNotEmpty()) {
                    outputStream.write(flushedBytes)
                }
            }

            onProgress(100)
        } catch (throwable: Throwable) {
            tempOutputFile.delete()
            throw throwable
        } finally {
            runCatching { decoder?.stop() }
            runCatching { decoder?.release() }
            runCatching { extractor.release() }
            runCatching { lameEncoder.close() }
        }
    }

    private suspend fun decodeAndEncode(
        extractor: MediaExtractor,
        decoder: MediaCodec,
        inputChannelCount: Int,
        outputChannelCount: Int,
        durationUs: Long,
        outputStream: FileOutputStream,
        onProgress: suspend (Int) -> Unit
    ) {
        val bufferInfo = MediaCodec.BufferInfo()

        var inputDone = false
        var outputDone = false
        var lastProgress = -1

        while (!outputDone) {
            coroutineContext.ensureActive()

            if (!inputDone) {
                val inputBufferIndex = decoder.dequeueInputBuffer(DEQUEUE_TIMEOUT_US)

                if (inputBufferIndex >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputBufferIndex)

                    if (inputBuffer == null) {
                        decoder.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            0,
                            0L,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        inputDone = true
                    } else {
                        inputBuffer.clear()

                        val sampleSize = extractor.readSampleData(
                            inputBuffer,
                            0
                        )

                        if (sampleSize < 0) {
                            decoder.queueInputBuffer(
                                inputBufferIndex,
                                0,
                                0,
                                0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            inputDone = true
                        } else {
                            decoder.queueInputBuffer(
                                inputBufferIndex,
                                0,
                                sampleSize,
                                extractor.sampleTime,
                                0
                            )
                            extractor.advance()
                        }
                    }
                }
            }

            val outputBufferIndex = decoder.dequeueOutputBuffer(
                bufferInfo,
                DEQUEUE_TIMEOUT_US
            )

            when {
                outputBufferIndex >= 0 -> {
                    val outputBuffer = decoder.getOutputBuffer(outputBufferIndex)

                    if (
                        outputBuffer != null &&
                        bufferInfo.size > 0 &&
                        bufferInfo.presentationTimeUs >= 0L
                    ) {
                        val pcm = extractPcm16(
                            buffer = outputBuffer,
                            offset = bufferInfo.offset,
                            size = bufferInfo.size,
                            inputChannelCount = inputChannelCount,
                            outputChannelCount = outputChannelCount
                        )

                        if (pcm.isNotEmpty()) {
                            val samplesPerChannel = pcm.size / outputChannelCount

                            val encodedBytes = lameEncoder.encode(
                                pcm = pcm,
                                samplesPerChannel = samplesPerChannel
                            )

                            if (encodedBytes.isNotEmpty()) {
                                outputStream.write(encodedBytes)
                            }
                        }

                        if (durationUs > 0L) {
                            val progress = ((bufferInfo.presentationTimeUs * 95L) / durationUs)
                                .toInt()
                                .coerceIn(1, 95)

                            if (progress != lastProgress) {
                                lastProgress = progress
                                onProgress(progress)
                            }
                        }
                    }

                    outputDone =
                        bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0

                    decoder.releaseOutputBuffer(outputBufferIndex, false)
                }

                outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    val outputFormat = decoder.outputFormat

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (outputFormat.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
                            val pcmEncoding = outputFormat.getInteger(
                                MediaFormat.KEY_PCM_ENCODING
                            )

                            if (pcmEncoding != AudioFormat.ENCODING_PCM_16BIT) {
                                error("Unsupported decoded PCM format.")
                            }
                        }
                    }
                }

                outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> Unit
            }
        }
    }

    private fun extractPcm16(
        buffer: ByteBuffer,
        offset: Int,
        size: Int,
        inputChannelCount: Int,
        outputChannelCount: Int
    ): ShortArray {
        val safeInputChannels = inputChannelCount.coerceAtLeast(1)
        val safeOutputChannels = outputChannelCount.coerceIn(1, 2)

        val duplicate = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN)
        duplicate.position(offset)
        duplicate.limit(offset + size)

        val shortBuffer = duplicate
            .slice()
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()

        val inputSamples = ShortArray(shortBuffer.remaining())
        shortBuffer.get(inputSamples)

        if (inputSamples.isEmpty()) return ShortArray(0)

        val frameCount = inputSamples.size / safeInputChannels

        if (frameCount <= 0) return ShortArray(0)

        if (safeInputChannels == safeOutputChannels) {
            return inputSamples
        }

        val outputSamples = ShortArray(frameCount * safeOutputChannels)

        for (frame in 0 until frameCount) {
            val inputBase = frame * safeInputChannels

            if (safeOutputChannels == 1) {
                var sum = 0

                for (channel in 0 until safeInputChannels) {
                    sum += inputSamples[inputBase + channel]
                }

                outputSamples[frame] = (sum / safeInputChannels).toShort()
            } else {
                val monoSample = inputSamples[inputBase]

                outputSamples[frame * 2] = monoSample
                outputSamples[frame * 2 + 1] = monoSample
            }
        }

        return outputSamples
    }

    private fun findAudioTrack(extractor: MediaExtractor): Int {
        for (index in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME).orEmpty()

            if (mime.startsWith("audio/")) {
                return index
            }
        }

        return -1
    }

    private fun MediaFormat.getIntegerOrDefault(
        key: String,
        defaultValue: Int
    ): Int {
        return if (containsKey(key)) getInteger(key) else defaultValue
    }

    private fun MediaFormat.getLongOrDefault(
        key: String,
        defaultValue: Long
    ): Long {
        return if (containsKey(key)) getLong(key) else defaultValue
    }

    private companion object {
        private const val DEQUEUE_TIMEOUT_US = 10_000L
    }
}