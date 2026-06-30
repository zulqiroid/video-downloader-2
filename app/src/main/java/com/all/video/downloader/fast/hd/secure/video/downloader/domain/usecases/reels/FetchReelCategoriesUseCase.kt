package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.reels

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.reels.ReelsRepository
import javax.inject.Inject

class FetchReelCategoriesUseCase @Inject constructor(
    private val repository: ReelsRepository
) {
    suspend operator fun invoke() = repository.fetchReelCategories()
}

data class ReelsUseCases @Inject constructor(
    val fetchReelCategories: FetchReelCategoriesUseCase
)