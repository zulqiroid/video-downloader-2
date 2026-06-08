package com.video.downloader.presentation.screens.home.events

import com.video.downloader.domain.models.Platforms
import com.video.downloader.presentation.screens.home.states.HomeFeatures

sealed interface HomeEvents {

    data class VideoUrlChanged(
        val value: String
    ) : HomeEvents

    data class PasteClicked(
        val clipboardText: String
    ) : HomeEvents

    data object DownloadClicked : HomeEvents

    data object NoInternetRetryClicked : HomeEvents
    data object NoInternetCancelClicked : HomeEvents

    data object VideoFetchRetryClicked : HomeEvents
    data object VideoFetchCancelClicked : HomeEvents

    data object WatchTrendingReelsClicked : HomeEvents

    data class PlatformClicked(
        val platform: Platforms
    ) : HomeEvents

    data class FeatureClicked(
        val feature: HomeFeatures
    ) : HomeEvents

    data object HowToDownloadVideosClicked : HomeEvents

    data object SettingClicked : HomeEvents


}