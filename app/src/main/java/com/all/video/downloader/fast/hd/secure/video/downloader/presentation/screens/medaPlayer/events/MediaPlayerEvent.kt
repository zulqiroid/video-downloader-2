package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.background.MediaPlayerForegroundPlaybackAction
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerGestureFeedbackType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSeekFeedbackDirection


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

    data class OnRotateClicked(
        val isCurrentlyLandscape: Boolean
    ) : MediaPlayerEvent()

    data object OnPictureInPictureClicked : MediaPlayerEvent()

    data class OnPictureInPictureModeChanged(
        val isInPictureInPictureMode: Boolean
    ) : MediaPlayerEvent()

    data object OnPictureInPictureEnterFailed : MediaPlayerEvent()

    data object OnUserLeaveHint : MediaPlayerEvent()

    data class OnPictureInPictureAction(
        val action: MediaPlayerPictureInPictureAction
    ) : MediaPlayerEvent()

    data class OnGestureFeedbackChanged(
        val type: MediaPlayerGestureFeedbackType,
        val percent: Int
    ) : MediaPlayerEvent()

    data object OnGestureFeedbackFinished : MediaPlayerEvent()

    data class OnSeekFeedbackRequested(
        val direction: MediaPlayerSeekFeedbackDirection
    ) : MediaPlayerEvent()

    data class OnForegroundPlaybackAction(
        val action: MediaPlayerForegroundPlaybackAction
    ) : MediaPlayerEvent()

 }