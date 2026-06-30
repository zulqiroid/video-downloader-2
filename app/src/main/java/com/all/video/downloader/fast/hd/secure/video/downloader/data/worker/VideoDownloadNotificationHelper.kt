package com.all.video.downloader.fast.hd.secure.video.downloader.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ForegroundInfo
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.core.MainActivity
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

class VideoDownloadNotificationHelper @Inject constructor() {

    fun createProgressForegroundInfo(
        context: Context,
        workId: java.util.UUID,
        downloadId: Long,
        fileName: String,
        progress: Int,
        speedBytesPerSec: Long,
        etaSeconds: Long,
    ): ForegroundInfo {
        createChannels(context)

        val safeProgress = progress.coerceIn(0, 100)

        val notification = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download_filled)
            .setContentTitle("Downloading video")
            .setContentText(buildProgressText(fileName, safeProgress, speedBytesPerSec))
            .setSubText(
                if (etaSeconds > 0L) {
                    "ETA ${etaSeconds.toReadableEta()}"
                } else {
                    "Please wait"
                }
            )
            .setContentIntent(openDownloadProgressPendingIntent(context, downloadId))
            .addAction(
                R.drawable.ic_download_filled,
                "Pause",
                actionPendingIntent(
                    context = context,
                    action = DownloadNotificationContract.ACTION_PAUSE_DOWNLOAD,
                    downloadId = downloadId,
                    requestSalt = PAUSE_REQUEST_SALT
                )
            )
            .addAction(
                R.drawable.ic_info_filled,
                "Cancel",
                actionPendingIntent(
                    context = context,
                    action = DownloadNotificationContract.ACTION_CANCEL_DOWNLOAD,
                    downloadId = downloadId,
                    requestSalt = CANCEL_REQUEST_SALT
                )
            )
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setProgress(
                100,
                safeProgress,
                safeProgress <= 0
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                progressNotificationId(downloadId),
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                progressNotificationId(downloadId),
                notification
            )
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showDownloadPaused(
        context: Context,
        downloadId: Long,
        fileName: String,
        progress: Int,
    ) {
        if (!canPostNotifications(context)) return

        createChannels(context)

        cancelProgressNotification(
            context = context,
            downloadId = downloadId
        )

        val safeProgress = progress.coerceIn(0, 100)

        val notification = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Download paused")
            .setContentText("$fileName • $safeProgress%")
            .setContentIntent(openDownloadProgressPendingIntent(context, downloadId))
            .addAction(
                R.drawable.ic_play,
                "Continue",
                actionPendingIntent(
                    context = context,
                    action = DownloadNotificationContract.ACTION_RESUME_DOWNLOAD,
                    downloadId = downloadId,
                    requestSalt = RESUME_REQUEST_SALT
                )
            )
            .addAction(
                R.drawable.ic_info_filled,
                "Cancel",
                actionPendingIntent(
                    context = context,
                    action = DownloadNotificationContract.ACTION_CANCEL_DOWNLOAD,
                    downloadId = downloadId,
                    requestSalt = CANCEL_REQUEST_SALT
                )
            )
            .setOngoing(false)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setProgress(100, safeProgress, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()

        NotificationManagerCompat.from(context)
            .notify(pausedNotificationId(downloadId), notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showDownloadComplete(
        context: Context,
        downloadId: Long,
        fileName: String,
    ) {
        if (!canPostNotifications(context)) return

        createChannels(context)

        cancelProgressNotification(context, downloadId)
        cancelPausedNotification(context, downloadId)

        val notification = NotificationCompat.Builder(context, RESULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Download complete")
            .setContentText(fileName)
            .setContentIntent(openDownloadProgressPendingIntent(context, downloadId))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()

        NotificationManagerCompat.from(context)
            .notify(resultNotificationId(downloadId), notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showDownloadFailed(
        context: Context,
        downloadId: Long,
        fileName: String,
    ) {
        if (!canPostNotifications(context)) return

        createChannels(context)

        cancelProgressNotification(context, downloadId)
        cancelPausedNotification(context, downloadId)

        val notification = NotificationCompat.Builder(context, RESULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_info_filled)
            .setContentTitle("Download failed")
            .setContentText(fileName)
            .setContentIntent(openDownloadProgressPendingIntent(context, downloadId))
            .addAction(
                R.drawable.ic_play,
                "Retry",
                actionPendingIntent(
                    context = context,
                    action = DownloadNotificationContract.ACTION_RETRY_DOWNLOAD,
                    downloadId = downloadId,
                    requestSalt = RETRY_REQUEST_SALT
                )
            )
            .addAction(
                R.drawable.ic_info_filled,
                "Cancel",
                actionPendingIntent(
                    context = context,
                    action = DownloadNotificationContract.ACTION_CANCEL_DOWNLOAD,
                    downloadId = downloadId,
                    requestSalt = CANCEL_REQUEST_SALT
                )
            )
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_ERROR)
            .build()

        NotificationManagerCompat.from(context)
            .notify(resultNotificationId(downloadId), notification)
    }

    fun cancelProgressNotification(
        context: Context,
        downloadId: Long,
    ) {
        NotificationManagerCompat.from(context)
            .cancel(progressNotificationId(downloadId))
    }

    fun cancelPausedNotification(
        context: Context,
        downloadId: Long,
    ) {
        NotificationManagerCompat.from(context)
            .cancel(pausedNotificationId(downloadId))
    }

    fun cancelDownloadNotifications(
        context: Context,
        downloadId: Long,
    ) {
        val manager = NotificationManagerCompat.from(context)

        manager.cancel(progressNotificationId(downloadId))
        manager.cancel(pausedNotificationId(downloadId))
        manager.cancel(resultNotificationId(downloadId))
    }

    private fun openDownloadProgressPendingIntent(
        context: Context,
        downloadId: Long,
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = DownloadNotificationContract.ACTION_OPEN_DOWNLOAD_PROGRESS
            putExtra(DownloadNotificationContract.EXTRA_DOWNLOAD_ID, downloadId)
            putExtra(DownloadNotificationContract.EXTRA_OPEN_DOWNLOAD_PROGRESS, true)

            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(
            context,
            requestCode(downloadId, OPEN_REQUEST_SALT),
            intent,
            pendingIntentFlags()
        )
    }

    private fun actionPendingIntent(
        context: Context,
        action: String,
        downloadId: Long,
        requestSalt: Int,
    ): PendingIntent {
        val intent = Intent(context, DownloadNotificationActionReceiver::class.java).apply {
            this.action = action
            putExtra(DownloadNotificationContract.EXTRA_DOWNLOAD_ID, downloadId)
        }

        return PendingIntent.getBroadcast(
            context,
            requestCode(downloadId, requestSalt),
            intent,
            pendingIntentFlags()
        )
    }

    private fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val downloadChannel = NotificationChannel(
            DOWNLOAD_CHANNEL_ID,
            "Video downloads",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows video download progress"
            setShowBadge(false)
        }

        val resultChannel = NotificationChannel(
            RESULT_CHANNEL_ID,
            "Download results",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Shows completed or failed download status"
            setShowBadge(true)
        }

        manager.createNotificationChannel(downloadChannel)
        manager.createNotificationChannel(resultChannel)
    }

    private fun canPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pendingIntentFlags(): Int {
        return PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    }

    private fun requestCode(
        downloadId: Long,
        salt: Int,
    ): Int {
        return salt + abs(downloadId.hashCode() % NOTIFICATION_ID_RANGE)
    }

    private fun progressNotificationId(downloadId: Long): Int {
        return BASE_PROGRESS_NOTIFICATION_ID + abs(downloadId.hashCode() % NOTIFICATION_ID_RANGE)
    }

    private fun pausedNotificationId(downloadId: Long): Int {
        return BASE_PAUSED_NOTIFICATION_ID + abs(downloadId.hashCode() % NOTIFICATION_ID_RANGE)
    }

    private fun resultNotificationId(downloadId: Long): Int {
        return BASE_RESULT_NOTIFICATION_ID + abs(downloadId.hashCode() % NOTIFICATION_ID_RANGE)
    }

    private fun buildProgressText(
        fileName: String,
        progress: Int,
        speedBytesPerSec: Long,
    ): String {
        return buildString {
            append(fileName)
            append(" • ")
            append(progress)
            append("%")

            if (speedBytesPerSec > 0L) {
                append(" • ")
                append(speedBytesPerSec.toReadableSpeed())
            }
        }
    }

    private fun Long.toReadableSpeed(): String {
        if (this <= 0L) return "0 KB/s"

        val kb = 1024.0
        val mb = kb * 1024.0

        return if (this >= mb) {
            String.format(Locale.US, "%.1f MB/s", this / mb)
        } else {
            String.format(Locale.US, "%.1f KB/s", this / kb)
        }
    }

    private fun Long.toReadableEta(): String {
        if (this <= 0L) return "calculating"

        val minutes = this / 60
        val seconds = this % 60

        return if (minutes > 0) {
            "${minutes}m ${seconds}s"
        } else {
            "${seconds}s"
        }
    }

    private companion object {
        private const val DOWNLOAD_CHANNEL_ID = "video_download_progress_channel"
        private const val RESULT_CHANNEL_ID = "video_download_result_channel"

        private const val BASE_PROGRESS_NOTIFICATION_ID = 5100
        private const val BASE_RESULT_NOTIFICATION_ID = 15100
        private const val BASE_PAUSED_NOTIFICATION_ID = 25100

        private const val NOTIFICATION_ID_RANGE = 10_000

        private const val OPEN_REQUEST_SALT = 31_000
        private const val PAUSE_REQUEST_SALT = 41_000
        private const val RESUME_REQUEST_SALT = 51_000
        private const val CANCEL_REQUEST_SALT = 61_000
        private const val RETRY_REQUEST_SALT = 71_000
    }
}