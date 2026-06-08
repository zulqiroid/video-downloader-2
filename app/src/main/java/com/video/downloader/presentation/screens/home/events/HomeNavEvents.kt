package com.video.downloader.presentation.screens.home.events

import com.video.downloader.domain.models.Platforms

sealed interface HomeNavEvents {

    object NavigateToDownloadGuide : HomeNavEvents

    data class NavigateToPlatformDetail(
        val platform: Platforms
    ) : HomeNavEvents

    object NavigateToMore : HomeNavEvents

    object NavigateToWatchReels : HomeNavEvents

    data object NavigateToVideoToMp3 : HomeNavEvents

    data object NavigateToVideoSplitter : HomeNavEvents

    data object NavigateToScreenCasting : HomeNavEvents
}