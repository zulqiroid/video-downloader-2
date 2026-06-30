package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models

data class ReelCategory(
    val id: Int,
    val name: String,
    val thumbnail: String,
    val categoryIndex: Int = Int.MAX_VALUE,
    val reels: List<Reel>
)

data class Reel(
    val id: String,
    val videoUrl: String,
    val categoryId: Int? = null,
    val categoryName: String = "Reels",
    val isAd: Boolean = false,
    val isPremium: Boolean = false,
    val type: String = "video",
    val createdAt: String? = null
)