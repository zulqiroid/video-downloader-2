package com.video.downloader.presentation.screens.home.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.video.downloader.R
import com.video.downloader.presentation.theme.AppColors

sealed class HomeFeatures(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val backgroundColor: Color,
){
    data object VideoSplitter : HomeFeatures(
        title = R.string.video_splitter,
        icon = R.drawable.ic_outlined_seissor,
        backgroundColor = AppColors.HighlightGradientTop
    )
    data object ScreenCasting : HomeFeatures(
        title = R.string.screen_casting,
        icon = R.drawable.ic_tv,
        backgroundColor = AppColors.HighlightGradientBottom
    )
    data object VideoToAudio: HomeFeatures(
        title = R.string.video_to_mp3,
        icon = R.drawable.ic_music_node,
        backgroundColor = AppColors.HighlightGradientTop
    )
}

fun getHomeFeatures(): List<HomeFeatures> {
    return listOf(
        HomeFeatures.VideoSplitter,
        HomeFeatures.ScreenCasting,
        HomeFeatures.VideoToAudio
    )
}