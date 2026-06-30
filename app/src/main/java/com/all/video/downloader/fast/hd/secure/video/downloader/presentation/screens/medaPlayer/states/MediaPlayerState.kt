package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.RingtoneTargetType

data class MediaPlayerState(
    val mediaList: List<MediaFile> = emptyList(),
    val currentIndex: Int = 0,

    val screenBackgroundColor: Long = SCREEN_BACKGROUND_BLACK,
    val orientationMode: MediaPlayerOrientationMode = MediaPlayerOrientationMode.Portrait,

    val showEqualizerDialog: Boolean = false,
    val equalizerState: MediaPlayerEqualizerState = MediaPlayerEqualizerState(),

    val showSubtitleDialog: Boolean = false,
    val subtitleState: MediaPlayerSubtitleState = MediaPlayerSubtitleState(),

    val isInPictureInPictureMode: Boolean = false,
    val pictureInPictureError: String? = null,

    val gestureFeedbackState: MediaPlayerGestureFeedbackState = MediaPlayerGestureFeedbackState(),
    val seekFeedbackState: MediaPlayerSeekFeedbackState = MediaPlayerSeekFeedbackState(),

    val isPlaybackEnded: Boolean = false,

    val isPlaying: Boolean = true,
    val position: Long = 0L,
    val duration: Long = 0L,

    val volume: Float = 1f,
    val isMuted: Boolean = false,

    val playbackSpeed: Float = DEFAULT_PLAYBACK_SPEED,
    val showPlaybackSpeedDialog: Boolean = false,

    val showFileInfoDialog: Boolean = false,
    val fileInfoMediaItem: MediaFile? = null,

    val showRenameFileDialog: Boolean = false,
    val renameMediaItem: MediaFile? = null,
    val renameDraftName: String = "",
    val renameError: String? = null,
    val isRenamingFile: Boolean = false,

    val showDeleteFileDialog: Boolean = false,
    val deleteMediaItem: MediaFile? = null,
    val isDeletingFile: Boolean = false,
    val deleteFileError: String? = null,

    val showSetAsRingtoneDialog: Boolean = false,
    val ringtoneMediaItem: MediaFile? = null,
    val selectedRingtoneTargetType: RingtoneTargetType = RingtoneTargetType.DefaultRingtone,
    val isSettingRingtone: Boolean = false,
    val setRingtoneError: String? = null,

    val isLoading: Boolean = false,

    val showBottomSheet: Boolean = false,

    val isPremiumUser: Boolean = false,


    val isShuffleEnabled: Boolean = false,
    val shuffleQueue: List<Int> = emptyList(),
    val shuffleQueuePosition: Int = 0,
    val isAudioRepeatEnabled: Boolean = false,
) {
    companion object {
        const val DEFAULT_PLAYBACK_SPEED = 1f

        const val SCREEN_BACKGROUND_BLACK: Long = 0xFF000000
        const val SCREEN_BACKGROUND_WHITE: Long = 0xFFFFFFFF
    }
}