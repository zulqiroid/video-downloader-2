package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.screen

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import java.io.File
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerEqualizerState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSubtitleTrackState

class MediaPlayerManager(
    context: Context,
) {

    private val equalizerController = MediaPlayerEqualizerController()

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .build()

    val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .build()
        .apply {
            setAudioAttributes(
                audioAttributes,
                true
            )

            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_OFF
        }

    fun play(
        media: MediaFile,
        subtitleTrack: MediaPlayerSubtitleTrackState? = null,
        startPositionMs: Long = 0L,
        shouldPlay: Boolean = true,
    ) {
        val uri = Uri.fromFile(File(media.filePath))

        player.repeatMode = Player.REPEAT_MODE_OFF

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setSubtitleConfigurations(
                subtitleTrack
                    ?.takeIf { media.isVideo }
                    ?.toSubtitleConfiguration()
                    ?.let(::listOf)
                    .orEmpty()
            )
            .build()

        player.setMediaItem(
            mediaItem,
            startPositionMs.coerceAtLeast(0L)
        )
        player.prepare()

        if (shouldPlay) {
            player.play()
        } else {
            player.pause()
        }
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun seekTo(position: Long) {
        player.seekTo(position.coerceAtLeast(0L))
    }

    fun forward() {
        val duration = player.duration.takeIf { it > 0L } ?: Long.MAX_VALUE
        val targetPosition = (player.currentPosition + SEEK_STEP_MS).coerceAtMost(duration)

        player.seekTo(targetPosition)
    }

    fun rewind() {
        val targetPosition = (player.currentPosition - SEEK_STEP_MS).coerceAtLeast(0L)

        player.seekTo(targetPosition)
    }

    fun setVolume(volume: Float) {
        player.volume = volume.coerceIn(0f, 1f)
    }

    fun setPlaybackSpeed(speed: Float) {
        val safeSpeed = speed.coerceIn(
            minimumValue = MIN_PLAYBACK_SPEED,
            maximumValue = MAX_PLAYBACK_SPEED
        )

        player.playbackParameters = PlaybackParameters(safeSpeed)
    }

    private fun MediaPlayerSubtitleTrackState.toSubtitleConfiguration(): MediaItem.SubtitleConfiguration {
        return MediaItem.SubtitleConfiguration.Builder(Uri.parse(uriString))
            .setMimeType(mimeType)
            .setLabel(fileName)
            .setLanguage(languageLabel)
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()
    }

    @OptIn(UnstableApi::class)
    fun refreshEqualizerState(
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return equalizerController.refresh(
            audioSessionId = player.audioSessionId,
            preferredState = currentState
        )
    }

    @OptIn(UnstableApi::class)
    fun setEqualizerEnabled(
        enabled: Boolean,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return equalizerController.setEnabled(
            audioSessionId = player.audioSessionId,
            enabled = enabled,
            currentState = currentState
        )
    }

    @OptIn(UnstableApi::class)
    fun setEqualizerBandLevel(
        bandIndex: Int,
        level: Int,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return equalizerController.setBandLevel(
            audioSessionId = player.audioSessionId,
            bandIndex = bandIndex,
            level = level,
            currentState = currentState
        )
    }

    @OptIn(UnstableApi::class)
    fun useEqualizerPreset(
        presetIndex: Int,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return equalizerController.usePreset(
            audioSessionId = player.audioSessionId,
            presetIndex = presetIndex,
            currentState = currentState
        )
    }

    @OptIn(UnstableApi::class)
    fun setBassBoostStrength(
        strength: Int,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return equalizerController.setBassBoostStrength(
            audioSessionId = player.audioSessionId,
            strength = strength,
            currentState = currentState
        )
    }

    fun reset() {
        player.stop()
        player.clearMediaItems()
    }

    fun release() {
        equalizerController.release()
        player.release()
    }

    companion object {
        private const val SEEK_STEP_MS = 10_000L
        private const val MIN_PLAYBACK_SPEED = 0.25f
        private const val MAX_PLAYBACK_SPEED = 4f
    }
}