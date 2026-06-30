package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states

import android.content.ContentUris
import android.provider.MediaStore
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.PlayerUiItem

fun PlayerUiItem.toMediaFile(isVideo: Boolean): MediaFile {
        val uri = if (isVideo) {
            ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id
            )
        } else {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )
        }

        return MediaFile(
            id = id,
            filePath = filePath,
            fileName = title,
            isVideo = isVideo,
            contentUri = uri.toString()
        )
    }