package com.video.downloader.presentation.screens.reels.states

data class ReelsStates(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val reels: List<ReelUi> = emptyList(),
    val currentIndex: Int = 0,
     val likedReelIds: Set<String> = emptySet()
)

data class ReelUi(
    val id: String,
    val videoUrl: String,
    val username: String,
    val caption: String,
    val isLiked: Boolean = false,
    val likeCount: Int = 105
)