package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.background

import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile

class MediaPlayerForegroundPlaybackController(
    context: Context,
) {
    private val appContext = context.applicationContext
    private var activeNotificationKey: String? = null
    private var isForegroundActive: Boolean = false

    fun sync(
        media: MediaFile?,
        isPlaying: Boolean,
        isPlaybackEnded: Boolean,
        canPlayPrevious: Boolean,
        canPlayNext: Boolean,
    ) {
        val shouldKeepPersistentNotification =
            media != null &&
                    !media.isVideo &&
                    !isPlaybackEnded

        if (!shouldKeepPersistentNotification) {
            stop()
            return
        }

        val notificationKey = buildNotificationKey(
            media = media,
            isPlaying = isPlaying,
            canPlayPrevious = canPlayPrevious,
            canPlayNext = canPlayNext
        )

        if (isForegroundActive && activeNotificationKey == notificationKey) {
            return
        }

        activeNotificationKey = notificationKey

        val intent = MediaPlayerForegroundPlaybackService.startIntent(
            context = appContext,
            title = media.fileName,
            isPlaying = isPlaying,
            canPlayPrevious = canPlayPrevious,
            canPlayNext = canPlayNext
        )

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isForegroundActive) {
                ContextCompat.startForegroundService(appContext, intent)
            } else {
                appContext.startService(intent)
            }

            isForegroundActive = true
        }.onFailure {
            activeNotificationKey = null
            isForegroundActive = false
        }
    }

    fun stop() {
        if (!isForegroundActive && activeNotificationKey == null) return

        activeNotificationKey = null
        isForegroundActive = false

        runCatching {
            appContext.stopService(
                MediaPlayerForegroundPlaybackService.plainIntent(appContext)
            )
        }
    }

    private fun buildNotificationKey(
        media: MediaFile,
        isPlaying: Boolean,
        canPlayPrevious: Boolean,
        canPlayNext: Boolean,
    ): String {
        return buildString {
            append(media.filePath)
            append('|')
            append(media.fileName)
            append('|')
            append(isPlaying)
            append('|')
            append(canPlayPrevious)
            append('|')
            append(canPlayNext)
        }
    }
}