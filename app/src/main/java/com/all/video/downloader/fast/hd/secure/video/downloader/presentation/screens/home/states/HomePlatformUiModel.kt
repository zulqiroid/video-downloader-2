package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.all.video.downloader.fast.hd.secure.video.downloader.R

@Immutable
data class HomePlatformUiModel(
    val id: String,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
)


val defaultHomePlatforms = listOf(
    HomePlatformUiModel(
        id = "tiktok",
        title = R.string.tokitok,
        icon = R.drawable.ic_tokitok
    ),
    HomePlatformUiModel(
        id = "facebook",
        title = R.string.fesbuk,
        icon = R.drawable.ic_fesbuk
    ),
    HomePlatformUiModel(
        id = "instagram",
        title = R.string.intgame,
        icon = R.drawable.ic_intgame
    ),
    HomePlatformUiModel(
        id = "likee",
        title = R.string.likeir,
        icon = R.drawable.ic_likeir
    ),
    HomePlatformUiModel(
        id = "dailymotion",
        title = R.string.dailymutin,
        icon = R.drawable.ic_dailymutin
    ),
    HomePlatformUiModel(
        id = "threads",
        title = R.string.thireds,
        icon = R.drawable.ic_thireds
    ),
    HomePlatformUiModel(
        id = "pinterest",
        title = R.string.pinitrust,
        icon = R.drawable.ic_pinitrust
    ),
    HomePlatformUiModel(
        id = "twitter",
        title = R.string.twetzer,
        icon = R.drawable.ic_twetzer
    )
)