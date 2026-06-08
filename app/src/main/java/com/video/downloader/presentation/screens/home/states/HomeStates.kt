package com.video.downloader.presentation.screens.home.states

import androidx.compose.runtime.Immutable
import com.video.downloader.domain.models.Platforms

@Immutable
data class HomeStates(
    val isDownloadLoading : Boolean = false,
    val error : String? = null,

    val videoUrl: String = "",
    val platforms: List<Platforms> = Platforms.all,
    val features: List<HomeFeatures> = getHomeFeatures(),


    val isCheckingInternet: Boolean = false,
    val showNoInternetDialog: Boolean = false,

    val isFetchingVideo: Boolean = false,
    val showVideoFetchFailedDialog: Boolean = false,
){
    val isDownloadActionLoading: Boolean
        get() = isDownloadLoading || isCheckingInternet || isFetchingVideo
}
