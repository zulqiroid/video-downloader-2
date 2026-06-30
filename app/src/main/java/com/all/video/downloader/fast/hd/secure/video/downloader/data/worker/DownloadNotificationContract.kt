package com.all.video.downloader.fast.hd.secure.video.downloader.data.worker

import android.content.Intent

object DownloadNotificationContract {

    const val ACTION_OPEN_DOWNLOAD_PROGRESS =
        "com.all.video.downloader.fast.hd.secure.video.downloader.action.OPEN_DOWNLOAD_PROGRESS"

    const val ACTION_PAUSE_DOWNLOAD =
        "com.all.video.downloader.fast.hd.secure.video.downloader.action.PAUSE_DOWNLOAD"

    const val ACTION_RESUME_DOWNLOAD =
        "com.all.video.downloader.fast.hd.secure.video.downloader.action.RESUME_DOWNLOAD"

    const val ACTION_CANCEL_DOWNLOAD =
        "com.all.video.downloader.fast.hd.secure.video.downloader.action.CANCEL_DOWNLOAD"

    const val EXTRA_DOWNLOAD_ID = "extra_download_id"
    const val EXTRA_OPEN_DOWNLOAD_PROGRESS = "extra_open_download_progress"

    const val ACTION_RETRY_DOWNLOAD =
        "com.all.video.downloader.fast.hd.secure.video.downloader.action.RETRY_DOWNLOAD"

    fun isOpenDownloadProgressIntent(intent: Intent?): Boolean {
        if (intent == null) return false

        return intent.action == ACTION_OPEN_DOWNLOAD_PROGRESS ||
                intent.getBooleanExtra(EXTRA_OPEN_DOWNLOAD_PROGRESS, false)
    }
}