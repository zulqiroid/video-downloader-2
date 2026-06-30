package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.videoToMp3

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import kotlinx.coroutines.flow.Flow

interface VideoToMp3Repository {

    fun observeConversions(): Flow<List<VideoToMp3ConversionItem>>

    suspend fun startConversion(
        inputUri: String,
        fileName: String,
        preset: VideoToMp3Preset
    ): String

    suspend fun cancelConversion(id: String)

    suspend fun saveConvertedMp3(
        tempOutputPath: String,
        outputNameWithoutExtension: String
    ): String
}