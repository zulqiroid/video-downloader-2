package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events

import android.app.PendingIntent
import android.net.Uri
 import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile

sealed interface MediaPlayerNavEvent {

    data class RequestMediaWritePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent? = null
    ) : MediaPlayerNavEvent

    data class RequestMediaDeletePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent? = null
    ) : MediaPlayerNavEvent

    data object RequestWriteSettingsPermission : MediaPlayerNavEvent

    data object RequestSubtitlePicker : MediaPlayerNavEvent

    data object RequestEnterPictureInPicture : MediaPlayerNavEvent

    data class ShareMediaFile(
        val mediaFile: MediaFile
    ) : MediaPlayerNavEvent

    data object CloseMediaPlayer : MediaPlayerNavEvent
}