package com.video.downloader.domain.usecases

import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAudiosUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return repository.observeAudios()
    }
}