package com.all.video.downloader.fast.hd.secure.video.downloader.data.encoder.audio

object PcmAudioTools {

    fun bytesToShortArrayLe(
        bytes: ByteArray,
        length: Int
    ): ShortArray {
        val sampleCount = length / 2
        val output = ShortArray(sampleCount)

        var byteIndex = 0
        var sampleIndex = 0

        while (sampleIndex < sampleCount) {
            val low = bytes[byteIndex].toInt() and 0xFF
            val high = bytes[byteIndex + 1].toInt()
            output[sampleIndex] = ((high shl 8) or low).toShort()

            byteIndex += 2
            sampleIndex++
        }

        return output
    }

    fun convertChannels(
        input: ShortArray,
        inputChannels: Int,
        outputChannels: Int
    ): ShortArray {
        if (inputChannels == outputChannels) return input

        return when {
            inputChannels == 2 && outputChannels == 1 -> stereoToMono(input)
            inputChannels == 1 && outputChannels == 2 -> monoToStereo(input)
            else -> input
        }
    }

    private fun stereoToMono(input: ShortArray): ShortArray {
        val frames = input.size / 2
        val output = ShortArray(frames)

        var sourceIndex = 0
        var targetIndex = 0

        while (targetIndex < frames) {
            val left = input[sourceIndex].toInt()
            val right = input[sourceIndex + 1].toInt()

            output[targetIndex] = ((left + right) / 2).toShort()

            sourceIndex += 2
            targetIndex++
        }

        return output
    }

    private fun monoToStereo(input: ShortArray): ShortArray {
        val output = ShortArray(input.size * 2)

        var sourceIndex = 0
        var targetIndex = 0

        while (sourceIndex < input.size) {
            val sample = input[sourceIndex]

            output[targetIndex] = sample
            output[targetIndex + 1] = sample

            sourceIndex++
            targetIndex += 2
        }

        return output
    }
}