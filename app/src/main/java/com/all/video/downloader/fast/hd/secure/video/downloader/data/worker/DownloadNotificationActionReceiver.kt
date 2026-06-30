package com.all.video.downloader.fast.hd.secure.video.downloader.data.worker

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.DownloadProgressStore
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.VideoDownloadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DownloadNotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: VideoDownloadRepository

    @Inject
    lateinit var progressStore: DownloadProgressStore

    @Inject
    lateinit var notificationHelper: VideoDownloadNotificationHelper

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                handleAction(
                    context = context.applicationContext,
                    intent = intent
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun handleAction(
        context: Context,
        intent: Intent
    ) {
        val downloadId = intent.getLongExtra(
            DownloadNotificationContract.EXTRA_DOWNLOAD_ID,
            INVALID_DOWNLOAD_ID
        )

        if (downloadId == INVALID_DOWNLOAD_ID) {
            return
        }

        when (intent.action) {
            DownloadNotificationContract.ACTION_PAUSE_DOWNLOAD -> {
                repository.pauseDownload(downloadId)

                /*
                 * WorkManager foreground notification remove kar sakta hai.
                 * Is liye thora delay de kar normal paused notification show karte hain.
                 */
                delay(PAUSED_NOTIFICATION_DELAY_MS)

                val item = progressStore.get(downloadId) ?: return

                notificationHelper.showDownloadPaused(
                    context = context,
                    downloadId = item.id,
                    fileName = item.fileName,
                    progress = item.progress
                )
            }

            DownloadNotificationContract.ACTION_RESUME_DOWNLOAD -> {
                notificationHelper.cancelPausedNotification(
                    context = context,
                    downloadId = downloadId
                )

                repository.resumeDownload(downloadId)
            }

            DownloadNotificationContract.ACTION_CANCEL_DOWNLOAD -> {
                repository.cancelDownload(downloadId)

                notificationHelper.cancelDownloadNotifications(
                    context = context,
                    downloadId = downloadId
                )
            }

            DownloadNotificationContract.ACTION_RETRY_DOWNLOAD -> {
                notificationHelper.cancelDownloadNotifications(
                    context = context,
                    downloadId = downloadId
                )

                repository.resumeDownload(downloadId)
            }
        }
    }

    private companion object {
        private const val INVALID_DOWNLOAD_ID = -1L
        private const val PAUSED_NOTIFICATION_DELAY_MS = 450L
    }
}