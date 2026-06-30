package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.reels

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.ReelCategory

interface ReelsRepository {

    suspend fun fetchReelCategories(): List<ReelCategory>
}