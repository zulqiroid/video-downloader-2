package com.video.downloader.presentation.screens.medaPlayer.screen

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.video.downloader.domain.models.MediaFile
import java.io.File

class MediaPlayerManager(
    context: Context
) {

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

    fun play(media: MediaFile) {
        val uri = Uri.fromFile(File(media.filePath))

        player.repeatMode = if (media.isVideo) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }

        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
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

    fun reset() {
        player.stop()
        player.clearMediaItems()
    }

    fun release() {
        player.release()
    }

    companion object {
        private const val SEEK_STEP_MS = 10_000L
        private const val MIN_PLAYBACK_SPEED = 0.25f
        private const val MAX_PLAYBACK_SPEED = 4f
    }
}