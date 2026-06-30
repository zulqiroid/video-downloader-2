package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.background

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object MediaPlayerForegroundPlaybackCommandBus {

    private val _actions = MutableSharedFlow<MediaPlayerForegroundPlaybackAction>(
        extraBufferCapacity = ACTION_BUFFER_CAPACITY
    )

    val actions = _actions.asSharedFlow()

    fun dispatch(
        action: MediaPlayerForegroundPlaybackAction,
    ) {
        _actions.tryEmit(action)
    }

    private const val ACTION_BUFFER_CAPACITY = 8
}