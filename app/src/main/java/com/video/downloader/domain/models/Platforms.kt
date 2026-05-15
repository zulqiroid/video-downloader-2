package com.video.downloader.domain.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.video.downloader.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Platforms(
    val id: String,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
) {

    @Serializable
    data object TokiTok : Platforms(
        id = "tiktok",
        title = R.string.tokitok,
        icon = R.drawable.ic_tokitok
    )

    @Serializable
    data object Fesbuk : Platforms(
        id = "facebook",
        title = R.string.fesbuk,
        icon = R.drawable.ic_fesbuk
    )

    @Serializable
    data object Intgame : Platforms(
        id = "instagram",
        title = R.string.intgame,
        icon = R.drawable.ic_intgame
    )

    @Serializable
    data object Likeir : Platforms(
        id = "likee",
        title = R.string.likeir,
        icon = R.drawable.ic_likeir
    )

    @Serializable
    data object Dailymutin : Platforms(
        id = "dailymotion",
        title = R.string.dailymutin,
        icon = R.drawable.ic_dailymutin
    )

    @Serializable
    data object Thireds : Platforms(
        id = "threads",
        title = R.string.thireds,
        icon = R.drawable.ic_thireds
    )

    @Serializable
    data object Pintrust : Platforms(
        id = "pinterest",
        title = R.string.pinitrust,
        icon = R.drawable.ic_pinitrust
    )

    @Serializable
    data object Twetzer : Platforms(
        id = "twitter",
        title = R.string.twetzer,
        icon = R.drawable.ic_twetzer
    )

    companion object{
        val all = listOf(
            TokiTok,
            Fesbuk,
            Intgame,
            Likeir,
            Dailymutin,
            Thireds,
            Pintrust,
            Twetzer
        )
    }
}