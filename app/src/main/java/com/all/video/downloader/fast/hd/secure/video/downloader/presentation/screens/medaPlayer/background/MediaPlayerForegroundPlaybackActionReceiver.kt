package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaPlayerForegroundPlaybackActionReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        val action = intent
            ?.action
            ?.toForegroundPlaybackActionOrNull()
            ?: return

        MediaPlayerForegroundPlaybackCommandBus.dispatch(action)
    }
}