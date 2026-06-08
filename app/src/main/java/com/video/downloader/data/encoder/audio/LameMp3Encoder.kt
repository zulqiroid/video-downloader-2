package com.video.downloader.data.encoder.audio

interface LameMp3Encoder {

    fun open(
        sampleRate: Int,
        channelCount: Int,
        bitrateKbps: Int
    )

    fun encode(
        pcm: ShortArray,
        samplesPerChannel: Int
    ): ByteArray

    fun flush(): ByteArray

    fun close()
}