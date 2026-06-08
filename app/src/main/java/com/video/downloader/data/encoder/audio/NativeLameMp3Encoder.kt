package com.video.downloader.data.encoder.audio

import javax.inject.Inject

class NativeLameMp3Encoder @Inject constructor() : LameMp3Encoder {

    private var isOpen = false

    override fun open(
        sampleRate: Int,
        channelCount: Int,
        bitrateKbps: Int
    ) {
        nativeInit(
            sampleRate = sampleRate,
            channelCount = channelCount,
            bitrateKbps = bitrateKbps
        )
        isOpen = true
    }

    override fun encode(
        pcm: ShortArray,
        samplesPerChannel: Int
    ): ByteArray {
        check(isOpen) {
            "LAME encoder is not open"
        }

        if (pcm.isEmpty() || samplesPerChannel <= 0) {
            return ByteArray(0)
        }

        return nativeEncode(
            pcm = pcm,
            samplesPerChannel = samplesPerChannel
        )
    }

    override fun flush(): ByteArray {
        check(isOpen) {
            "LAME encoder is not open"
        }

        return nativeFlush()
    }

    override fun close() {
        if (isOpen) {
            nativeClose()
            isOpen = false
        }
    }

    private external fun nativeInit(
        sampleRate: Int,
        channelCount: Int,
        bitrateKbps: Int
    )

    private external fun nativeEncode(
        pcm: ShortArray,
        samplesPerChannel: Int
    ): ByteArray

    private external fun nativeFlush(): ByteArray

    private external fun nativeClose()

    companion object {
        init {
            System.loadLibrary("lame_mp3_encoder")
        }
    }
}