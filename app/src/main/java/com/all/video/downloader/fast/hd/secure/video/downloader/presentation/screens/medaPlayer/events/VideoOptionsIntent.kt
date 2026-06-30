package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.RingtoneTargetType


sealed interface VideoOptionsIntent {

    data object OnAddToPlayingQueueClicked : VideoOptionsIntent

    data object OnPlaybackSpeedClicked : VideoOptionsIntent

    data class OnPlaybackSpeedSelected(
        val speed: Float
    ) : VideoOptionsIntent

    data object OnPlaybackSpeedResetClicked : VideoOptionsIntent

    data object OnPlaybackSpeedDialogDismissed : VideoOptionsIntent

    data object OnSetAsRingtoneClicked : VideoOptionsIntent

    data class OnRingtoneTargetSelected(
        val targetType: RingtoneTargetType
    ) : VideoOptionsIntent

    data object OnSetAsRingtoneConfirmClicked : VideoOptionsIntent

    data object OnSetAsRingtoneDismissed : VideoOptionsIntent

    data object OnWriteSettingsPermissionReturned : VideoOptionsIntent

    data object OnFileInfoClicked : VideoOptionsIntent

    data object OnFileInfoDismissed : VideoOptionsIntent

    data object OnShareClicked : VideoOptionsIntent

    data object OnRenameClicked : VideoOptionsIntent

    data class OnRenameValueChanged(
        val value: String
    ) : VideoOptionsIntent

    data object OnRenameDismissed : VideoOptionsIntent

    data object OnRenameConfirmClicked : VideoOptionsIntent

    data object OnRenamePermissionGranted : VideoOptionsIntent

    data object OnRenamePermissionDenied : VideoOptionsIntent

    data object OnDeleteClicked : VideoOptionsIntent

    data object OnDeleteDismissed : VideoOptionsIntent

    data object OnDeleteConfirmClicked : VideoOptionsIntent

    data object OnDeletePermissionGranted : VideoOptionsIntent

    data object OnDeletePermissionDenied : VideoOptionsIntent

    data object OnDismiss : VideoOptionsIntent


    data object OnEqualizerClicked : VideoOptionsIntent

    data class OnEqualizerEnabledChanged(
        val enabled: Boolean
    ) : VideoOptionsIntent

    data class OnEqualizerBandLevelChanged(
        val bandIndex: Int,
        val level: Int
    ) : VideoOptionsIntent

    data class OnEqualizerPresetSelected(
        val presetIndex: Int
    ) : VideoOptionsIntent

    data class OnBassBoostChanged(
        val strength: Int
    ) : VideoOptionsIntent

    data object OnEqualizerDialogDismissed : VideoOptionsIntent

    data object OnSubtitleClicked : VideoOptionsIntent

    data class OnSubtitleTrackSelected(
        val trackId: String
    ) : VideoOptionsIntent

    data object OnSubtitleDisabledClicked : VideoOptionsIntent

    data object OnSubtitleDialogDismissed : VideoOptionsIntent

    data object OnSubtitlePickerClicked : VideoOptionsIntent

    data class OnSubtitlePicked(
        val uriString: String
    ) : VideoOptionsIntent

    data object OnSubtitlePickerCancelled : VideoOptionsIntent
}