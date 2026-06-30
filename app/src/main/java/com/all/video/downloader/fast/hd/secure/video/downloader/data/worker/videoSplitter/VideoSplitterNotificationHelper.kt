package com.all.video.downloader.fast.hd.secure.video.downloader.data.worker.videoSplitter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import javax.inject.Inject

class VideoSplitterNotificationHelper @Inject constructor() {

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Video Splitter",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows video splitting progress"
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
            .setSmallIcon(R.drawable.ic_vd_cam_filled)
            .setContentTitle("Splitting video")
            .setContentText("$fileName • ${progress.coerceIn(0, 100)}%")
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
        const val CHANNEL_ID = "video_splitter_channel"
        private const val NOTIFICATION_ID = 4022
    }
}