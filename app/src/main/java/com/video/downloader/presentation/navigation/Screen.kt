package com.video.downloader.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed interface Screen : NavKey{

    @Serializable
    data object Splash : Screen

    @Serializable
    data object Main : Screen

}