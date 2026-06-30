package com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.reels

import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels.ReelsApi
import com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels.mapper.toDomainReelCategories
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.ReelCategory
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.reels.ReelsRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReelsRepositoryImpl @Inject constructor(
    private val api: ReelsApi
) : ReelsRepository {

    override suspend fun fetchReelCategories(): List<ReelCategory> {
        debug {
            "fetchReelCategories() started"
        }

        val response = api.fetchCategories()
        val categories = response.toDomainReelCategories()

        debug {
            """
            fetchReelCategories() success
            categories=${categories.size}
            reels=${categories.sumOf { it.reels.size }}
            """.trimIndent()
        }

        return categories
    }

    private fun debug(message: () -> String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).d(message())
        }
    }

    private companion object {
        private const val TAG = "ReelsRepo"
    }
}