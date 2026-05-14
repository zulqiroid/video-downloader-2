package com.video.downloader.presentation.screens.splash.states

import androidx.compose.runtime.Immutable


@Immutable
data class SplashStates(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,


    val leftColumnImages: List<SplashImageItem> = SplashImages.leftColumn,
    val middleColumnImages: List<SplashImageItem> = SplashImages.middleColumn,
    val rightColumnImages: List<SplashImageItem> = SplashImages.rightColumn,
)
