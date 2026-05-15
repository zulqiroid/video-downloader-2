package com.video.downloader.presentation.screens.home.states

import androidx.compose.runtime.Immutable
import com.video.downloader.domain.models.Platforms

@Immutable
data class HomeStates(
    val isLoading : Boolean = false,
    val error : String? = null,

    val videoUrl: String = "",
    val platforms: List<Platforms> = Platforms.all,
    val features: List<HomeFeatures> = getHomeFeatures()
)
