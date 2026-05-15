package com.video.downloader.presentation.screens.home.states

import androidx.compose.runtime.Immutable

@Immutable
data class HomeStates(
    val isLoading : Boolean = false,
    val error : String? = null,

    val videoUrl: String = "",
    val platforms: List<HomePlatformUiModel> = defaultHomePlatforms,
    val features: List<HomeFeatures> = getHomeFeatures()
)
