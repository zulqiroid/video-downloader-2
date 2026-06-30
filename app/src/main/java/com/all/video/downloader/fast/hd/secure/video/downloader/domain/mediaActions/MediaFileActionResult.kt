package com.all.video.downloader.fast.hd.secure.video.downloader.domain.mediaActions

import android.content.IntentSender

sealed interface MediaFileActionResult<out T> {

    data class Success<T>(
        val data: T
    ) : MediaFileActionResult<T>

    data class RequiresUserApproval(
        val intentSender: IntentSender,
        val message: String
    ) : MediaFileActionResult<Nothing>

    data class Failure(
        val message: String,
        val throwable: Throwable? = null
    ) : MediaFileActionResult<Nothing>
}