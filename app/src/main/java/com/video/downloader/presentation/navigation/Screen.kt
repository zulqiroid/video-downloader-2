package com.video.downloader.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.video.downloader.domain.models.MediaFile
import com.video.downloader.domain.models.Platforms
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object Splash : Screen

    @Serializable
    data object AppLanguage : Screen

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Main : Screen

    @Serializable
    data object DownloadGuide : Screen
    data class MediaPlayer(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : Screen

    @Serializable
    data class PlatformDetail(
        val platform: Platforms
    ) : Screen

    @Serializable
    data object More : Screen
    @Serializable
    data object VideoToMp3 : Screen
    @Serializable
    data class VideoToMp3Result(
        val conversionId: String
    ) : Screen


    @Serializable
    data object VideoSplitter : Screen

    @Serializable
    data object ScreenCasting : Screen
}