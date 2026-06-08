package com.video.downloader.data.worker.videoToMp3

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.video.downloader.R
import javax.inject.Inject

class VideoToMp3NotificationHelper @Inject constructor() {

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Video to MP3",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows video to MP3 conversion progress"
        }

        manager.createNotificationChannel(channel)
    }

    fun createForegroundInfo(
        context: Context,
        workId: java.util.UUID,
        fileName: String,
        progress: Int
    ): ForegroundInfo {
        createChannel(context)

        val cancelIntent = WorkManager
            .getInstance(context)
            .createCancelPendingIntent(workId)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_node_filled)
            .setContentTitle("Converting to MP3")
            .setContentText("$fileName • $progress%")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress.coerceIn(0, 100), false)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Cancel",
                cancelIntent
            )
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "video_to_mp3_channel"
        private const val NOTIFICATION_ID = 3022
    }
}