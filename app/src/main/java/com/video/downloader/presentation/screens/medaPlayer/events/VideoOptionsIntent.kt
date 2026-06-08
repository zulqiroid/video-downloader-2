package com.video.downloader.presentation.screens.medaPlayer.events

import com.video.downloader.domain.models.RingtoneTargetType


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
}