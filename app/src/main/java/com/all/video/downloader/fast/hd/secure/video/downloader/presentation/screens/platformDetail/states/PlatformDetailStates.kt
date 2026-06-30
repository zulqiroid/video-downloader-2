package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.platformDetail.states

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.Platforms

@Immutable
data class PlatformDetailStates(
    val platform: Platforms = Platforms.TokiTok,
     val videoUrl: String = "",
    val trendingVideos: List<TrendingVideoUiModel> = defaultTrendingVideos,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Immutable
data class TrendingVideoUiModel(
    val id: String,
    val title: String,
    val views: String,
    val thumbnailColors: List<Color>
)

val defaultTrendingVideos = listOf(
    TrendingVideoUiModel(
        id = "dance_challenge",
        title = "Dance Challenge 2024",
        views = "2.4M",
        thumbnailColors = listOf(
            Color(0xFF0B0B28),
            Color(0xFFD9008F)
        )
    ),
    TrendingVideoUiModel(
        id = "spicy_ramen",
        title = "Spicy Ramen Street Fo",
        views = "845K",
        thumbnailColors = listOf(
            Color(0xFF27323A),
            Color(0xFFFF8A1F)
        )
    ),
    TrendingVideoUiModel(
        id = "mountain_peak",
        title = "Mountain Peak Vlog",
        views = "1.1M",
        thumbnailColors = listOf(
            Color(0xFFFF9A3D),
            Color(0xFF122743)
        )
    )
)