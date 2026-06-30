package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events

sealed interface ReelsEvents {

    data object LoadReels : ReelsEvents

    data class OnPageChanged(
        val index: Int
    ) : ReelsEvents

    data class OnLikeClicked(
        val reelId: String
    ) : ReelsEvents

    data class OnShareClicked(
        val reelId: String
    ) : ReelsEvents

    data class OnDownloadClicked(
        val reelId: String
    ) : ReelsEvents

    data object OnSettingCLicked : ReelsEvents
}