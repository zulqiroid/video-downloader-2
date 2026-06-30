package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAudiosUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return repository.observeAudios()
    }
}

class ObserveAudioChangesUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    operator fun invoke(): Flow<Unit> {
        return repository.observeAudioChanges()
    }
}

class GetAudiosPageUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(
        offset: Int,
        limit: Int
    ): List<MediaItem> {
        return repository.getAudiosPage(
            offset = offset,
            limit = limit
        )
    }
}