package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.screen

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.view.WindowManager
import kotlin.math.roundToInt

class MediaPlayerBrightnessVolumeGestureController(
    private val context: Context,
    private val activity: Activity?,
) {
    private val audioManager: AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    fun currentBrightnessFraction(): Float {
        val windowBrightness = activity
            ?.window
            ?.attributes
            ?.screenBrightness
            ?.takeIf { value ->
                value != WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }

        if (windowBrightness != null) {
            return windowBrightness.coerceIn(
                minimumValue = MIN_BRIGHTNESS_FRACTION,
                maximumValue = MAX_FRACTION
            )
        }

        return runCatching {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            ) / MAX_SYSTEM_BRIGHTNESS.toFloat()
        }.getOrDefault(DEFAULT_BRIGHTNESS_FRACTION)
            .coerceIn(
                minimumValue = MIN_BRIGHTNESS_FRACTION,
                maximumValue = MAX_FRACTION
            )
    }

    fun setBrightnessFraction(
        fraction: Float,
    ): Int {
        val safeFraction = fraction.coerceIn(
            minimumValue = MIN_BRIGHTNESS_FRACTION,
            maximumValue = MAX_FRACTION
        )

        activity?.window?.let { window ->
            val params = window.attributes
            params.screenBrightness = safeFraction
            window.attributes = params
        }

        return (safeFraction * PERCENT_MULTIPLIER)
            .roundToInt()
            .coerceIn(MIN_PERCENT, MAX_PERCENT)
    }

    fun currentVolumeFraction(): Float {
        val manager = audioManager ?: return 0f
        val maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            .coerceAtLeast(1)
        val currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC)

        return (currentVolume / maxVolume.toFloat())
            .coerceIn(0f, MAX_FRACTION)
    }

    fun setVolumeFraction(
        fraction: Float,
    ): Int {
        val manager = audioManager ?: return 0

        val maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            .coerceAtLeast(1)

        val safeFraction = fraction.coerceIn(
            minimumValue = 0f,
            maximumValue = MAX_FRACTION
        )

        val targetVolume = (safeFraction * maxVolume)
            .roundToInt()
            .coerceIn(0, maxVolume)

        manager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            targetVolume,
            0
        )

        return ((targetVolume / maxVolume.toFloat()) * PERCENT_MULTIPLIER)
            .roundToInt()
            .coerceIn(MIN_PERCENT, MAX_PERCENT)
    }

    private companion object {
        private const val MIN_PERCENT = 0
        private const val MAX_PERCENT = 100
        private const val PERCENT_MULTIPLIER = 100
        private const val MAX_SYSTEM_BRIGHTNESS = 255
        private const val DEFAULT_BRIGHTNESS_FRACTION = 0.5f
        private const val MIN_BRIGHTNESS_FRACTION = 0.05f
        private const val MAX_FRACTION = 1f
    }
}