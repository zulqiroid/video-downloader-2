package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.background

enum class MediaPlayerForegroundPlaybackAction {
    PlayPause,
    Previous,
    Next,
    Stop
}

internal fun MediaPlayerForegroundPlaybackAction.toBroadcastAction(): String {
    return when (this) {
        MediaPlayerForegroundPlaybackAction.PlayPause -> ACTION_FOREGROUND_PLAY_PAUSE
        MediaPlayerForegroundPlaybackAction.Previous -> ACTION_FOREGROUND_PREVIOUS
        MediaPlayerForegroundPlaybackAction.Next -> ACTION_FOREGROUND_NEXT
        MediaPlayerForegroundPlaybackAction.Stop -> ACTION_FOREGROUND_STOP
    }
}

internal fun String.toForegroundPlaybackActionOrNull(): MediaPlayerForegroundPlaybackAction? {
    return when (this) {
        ACTION_FOREGROUND_PLAY_PAUSE -> MediaPlayerForegroundPlaybackAction.PlayPause
        ACTION_FOREGROUND_PREVIOUS -> MediaPlayerForegroundPlaybackAction.Previous
        ACTION_FOREGROUND_NEXT -> MediaPlayerForegroundPlaybackAction.Next
        ACTION_FOREGROUND_STOP -> MediaPlayerForegroundPlaybackAction.Stop
        else -> null
    }
}

internal fun MediaPlayerForegroundPlaybackAction.toRequestCode(): Int {
    return when (this) {
        MediaPlayerForegroundPlaybackAction.PlayPause -> REQUEST_CODE_PLAY_PAUSE
        MediaPlayerForegroundPlaybackAction.Previous -> REQUEST_CODE_PREVIOUS
        MediaPlayerForegroundPlaybackAction.Next -> REQUEST_CODE_NEXT
        MediaPlayerForegroundPlaybackAction.Stop -> REQUEST_CODE_STOP
    }
}

private const val ACTION_FOREGROUND_PLAY_PAUSE =
    "com.all.video.downloader.fast.hd.secure.video.downloader.action.FOREGROUND_PLAY_PAUSE"

private const val ACTION_FOREGROUND_PREVIOUS =
    "com.all.video.downloader.fast.hd.secure.video.downloader.action.FOREGROUND_PREVIOUS"

private const val ACTION_FOREGROUND_NEXT =
    "com.all.video.downloader.fast.hd.secure.video.downloader.action.FOREGROUND_NEXT"

private const val ACTION_FOREGROUND_STOP =
    "com.all.video.downloader.fast.hd.secure.video.downloader.action.FOREGROUND_STOP"

private const val REQUEST_CODE_PLAY_PAUSE = 9301
private const val REQUEST_CODE_PREVIOUS = 9302
private const val REQUEST_CODE_NEXT = 9303
private const val REQUEST_CODE_STOP = 9304