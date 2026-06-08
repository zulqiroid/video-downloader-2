package com.video.downloader.presentation.screens.reels.events

sealed interface ReelsEvents {

    object LoadReels : ReelsEvents

    data class OnPageChanged(
        val index: Int
    ) : ReelsEvents

    data class OnLikeClicked(
        val reelId: String
    ) : ReelsEvents

    data object OnSettingCLicked:  ReelsEvents
}