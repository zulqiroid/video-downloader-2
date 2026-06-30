package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states

import androidx.compose.runtime.Immutable

@Immutable
data class MediaPlayerSubtitleState(
    val tracks: List<MediaPlayerSubtitleTrackState> = emptyList(),
    val selectedTrackId: String? = null,
    val isEnabled: Boolean = false,
    val errorMessage: String? = null,
) {
    val hasTracks: Boolean
        get() = tracks.isNotEmpty()

    val selectedTrack: MediaPlayerSubtitleTrackState?
        get() {
            if (!isEnabled) return null
            return tracks.firstOrNull { track ->
                track.id == selectedTrackId
            }
        }
}

@Immutable
data class MediaPlayerSubtitleTrackState(
    val id: String,
    val uriString: String,
    val fileName: String,
    val mimeType: String,
    val languageLabel: String,
    val source: MediaPlayerSubtitleSource,
)

enum class MediaPlayerSubtitleSource {
    LocalFile,
    DocumentPicker
}