package com.video.downloader.presentation.navigation

import androidx.navigation3.runtime.NavKey
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

    @Serializable
    data class PlatformDetail(
        val platform: Platforms
    ) : Screen

    @Serializable
    data object More : Screen
}