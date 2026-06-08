package com.video.downloader.presentation.screens.medaPlayer.events

import com.video.downloader.domain.models.MediaFile


sealed class MediaPlayerEvent {

    data class Load(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : MediaPlayerEvent()

    data class OnPageChanged(val index: Int) : MediaPlayerEvent()

    data object OnPlayPauseClicked : MediaPlayerEvent()

    data object OnNextClicked : MediaPlayerEvent()

    data object OnPreviousClicked : MediaPlayerEvent()

    data object OnForwardClicked : MediaPlayerEvent()

    data object OnRewindClicked : MediaPlayerEvent()

    data class OnSeek(val position: Long) : MediaPlayerEvent()

    data class OnVolumeChanged(val volume: Float) : MediaPlayerEvent()

    data object OnMuteToggleClicked : MediaPlayerEvent()

    object OnBackPressed : MediaPlayerEvent()

    object OnThreeDotsClick: MediaPlayerEvent()

    data object OnNativeAdPageVisible : MediaPlayerEvent()

    data object OnShuffleClicked : MediaPlayerEvent()
    data object OnAudioRepeatClicked : MediaPlayerEvent()

 }