package com.video.downloader.domain.models
data class ReelCategory(
    val id: Int,
    val name: String,
    val thumbnail: String,
    val reels: List<Reel>
)

data class Reel(
    val id: String,
    val videoUrl: String
)
