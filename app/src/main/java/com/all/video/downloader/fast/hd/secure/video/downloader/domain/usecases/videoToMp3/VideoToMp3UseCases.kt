package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.videoToMp3

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.videoToMp3.VideoToMp3Repository
import javax.inject.Inject

data class VideoToMp3UseCases @Inject constructor(
    val startConversion: StartVideoToMp3ConversionUseCase,
    val observeConversions: ObserveVideoToMp3ConversionsUseCase,
    val cancelConversion: CancelVideoToMp3ConversionUseCase,
    val saveConvertedMp3: SaveConvertedMp3UseCase
)

class StartVideoToMp3ConversionUseCase @Inject constructor(
    private val repository: VideoToMp3Repository
) {
    suspend operator fun invoke(
        inputUri: String,
        fileName: String,
        preset: VideoToMp3Preset
    ): String {
        return repository.startConversion(
            inputUri = inputUri,
            fileName = fileName,
            preset = preset
        )
    }
}

class ObserveVideoToMp3ConversionsUseCase @Inject constructor(
    private val repository: VideoToMp3Repository
) {
    operator fun invoke() = repository.observeConversions()
}

class CancelVideoToMp3ConversionUseCase @Inject constructor(
    private val repository: VideoToMp3Repository
) {
    suspend operator fun invoke(id: String) {
        repository.cancelConversion(id)
    }
}

class SaveConvertedMp3UseCase @Inject constructor(
    private val repository: VideoToMp3Repository
) {
    suspend operator fun invoke(
        tempOutputPath: String,
        outputNameWithoutExtension: String
    ): String {
        return repository.saveConvertedMp3(
            tempOutputPath = tempOutputPath,
            outputNameWithoutExtension = outputNameWithoutExtension
        )
    }
}