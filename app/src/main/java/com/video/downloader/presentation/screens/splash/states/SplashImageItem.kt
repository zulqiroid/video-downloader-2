package com.video.downloader.presentation.screens.splash.states

import androidx.annotation.DrawableRes
import com.video.downloader.R

data class SplashImageItem(
    @DrawableRes val imageRes: Int
)

object SplashImages {

    val leftColumn: List<SplashImageItem> = listOf(
        SplashImageItem(R.drawable.splash_col_1_1),
        SplashImageItem(R.drawable.splash_col_1_2),
        SplashImageItem(R.drawable.splash_col_1_3),
        SplashImageItem(R.drawable.splash_col_1_4),
     )

    val middleColumn: List<SplashImageItem> = listOf(
        SplashImageItem(R.drawable.splash_col_2_1),
        SplashImageItem(R.drawable.splash_col_2_2),
        SplashImageItem(R.drawable.splash_col_2_3),
        SplashImageItem(R.drawable.splash_col_2_4),
     )

    val rightColumn: List<SplashImageItem> = listOf(
        SplashImageItem(R.drawable.splash_col_3_1),
        SplashImageItem(R.drawable.splash_col_3_2),
        SplashImageItem(R.drawable.splash_col_3_3),
        SplashImageItem(R.drawable.splash_col_3_4),
     )
}