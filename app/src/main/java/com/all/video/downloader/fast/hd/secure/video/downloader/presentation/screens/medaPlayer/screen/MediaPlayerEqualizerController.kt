package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.screen

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerEqualizerBandState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerEqualizerPresetState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerEqualizerState
import java.util.Locale

@UnstableApi
@Suppress("DEPRECATION")
class MediaPlayerEqualizerController {

    private var currentAudioSessionId: Int = C.AUDIO_SESSION_ID_UNSET
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    fun refresh(
        audioSessionId: Int,
        preferredState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return ensureAttached(
            audioSessionId = audioSessionId,
            preferredState = preferredState
        ).fold(
            onSuccess = {
                buildState(errorMessage = null)
            },
            onFailure = { throwable ->
                releaseEffects()

                preferredState.copy(
                    isSupported = false,
                    isEnabled = false,
                    errorMessage = throwable.toUserMessage()
                )
            }
        )
    }

    fun setEnabled(
        audioSessionId: Int,
        enabled: Boolean,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return ensureAttached(
            audioSessionId = audioSessionId,
            preferredState = currentState
        ).fold(
            onSuccess = {
                runCatching {
                    equalizer?.enabled = enabled
                    bassBoost?.enabled = enabled && currentState.bassBoostStrength > 0
                }

                buildState(errorMessage = null).copy(
                    isEnabled = enabled
                )
            },
            onFailure = { throwable ->
                releaseEffects()

                currentState.copy(
                    isSupported = false,
                    isEnabled = false,
                    errorMessage = throwable.toUserMessage()
                )
            }
        )
    }

    fun setBandLevel(
        audioSessionId: Int,
        bandIndex: Int,
        level: Int,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return ensureAttached(
            audioSessionId = audioSessionId,
            preferredState = currentState
        ).fold(
            onSuccess = {
                val effect = equalizer ?: return currentState.copy(
                    isSupported = false,
                    errorMessage = "Equalizer is not available on this device."
                )

                val bandCount = effect.numberOfBands.toInt()
                if (bandCount <= 0) {
                    return currentState.copy(
                        isSupported = false,
                        errorMessage = "Equalizer bands are not available on this device."
                    )
                }

                val safeBandIndex = bandIndex.coerceIn(
                    minimumValue = 0,
                    maximumValue = bandCount - 1
                )

                val range = effect.bandLevelRange
                val minLevel = range.getOrNull(0)?.toInt()
                    ?: MediaPlayerEqualizerState.DEFAULT_MIN_BAND_LEVEL
                val maxLevel = range.getOrNull(1)?.toInt()
                    ?: MediaPlayerEqualizerState.DEFAULT_MAX_BAND_LEVEL

                val safeLevel = level.coerceIn(
                    minimumValue = minLevel,
                    maximumValue = maxLevel
                )

                runCatching {
                    effect.setBandLevel(
                        safeBandIndex.toShort(),
                        safeLevel.toShort()
                    )
                }

                buildState(errorMessage = null).copy(
                    selectedPresetIndex = null
                )
            },
            onFailure = { throwable ->
                currentState.copy(
                    errorMessage = throwable.toUserMessage()
                )
            }
        )
    }

    fun usePreset(
        audioSessionId: Int,
        presetIndex: Int,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return ensureAttached(
            audioSessionId = audioSessionId,
            preferredState = currentState
        ).fold(
            onSuccess = {
                val effect = equalizer ?: return currentState.copy(
                    isSupported = false,
                    errorMessage = "Equalizer is not available on this device."
                )

                val presetCount = effect.numberOfPresets.toInt()
                if (presetCount <= 0) {
                    return currentState.copy(
                        errorMessage = "Equalizer presets are not available on this device."
                    )
                }

                val safePresetIndex = presetIndex.coerceIn(
                    minimumValue = 0,
                    maximumValue = presetCount - 1
                )

                runCatching {
                    effect.usePreset(safePresetIndex.toShort())
                }

                buildState(errorMessage = null).copy(
                    selectedPresetIndex = safePresetIndex
                )
            },
            onFailure = { throwable ->
                currentState.copy(
                    errorMessage = throwable.toUserMessage()
                )
            }
        )
    }

    fun setBassBoostStrength(
        audioSessionId: Int,
        strength: Int,
        currentState: MediaPlayerEqualizerState,
    ): MediaPlayerEqualizerState {
        return ensureAttached(
            audioSessionId = audioSessionId,
            preferredState = currentState
        ).fold(
            onSuccess = {
                val safeStrength = strength.coerceIn(
                    minimumValue = MediaPlayerEqualizerState.DEFAULT_BASS_BOOST_STRENGTH,
                    maximumValue = MediaPlayerEqualizerState.MAX_BASS_BOOST_STRENGTH
                )

                runCatching {
                    bassBoost?.setStrength(safeStrength.toShort())
                    bassBoost?.enabled = currentState.isEnabled && safeStrength > 0
                }

                buildState(errorMessage = null).copy(
                    bassBoostStrength = safeStrength
                )
            },
            onFailure = { throwable ->
                currentState.copy(
                    errorMessage = throwable.toUserMessage()
                )
            }
        )
    }

    fun release() {
        releaseEffects()
    }

    private fun ensureAttached(
        audioSessionId: Int,
        preferredState: MediaPlayerEqualizerState,
    ): Result<Unit> {
        if (audioSessionId == C.AUDIO_SESSION_ID_UNSET || audioSessionId == INVALID_AUDIO_SESSION_ID) {
            return Result.failure(
                IllegalStateException("Equalizer is waiting for player audio session.")
            )
        }

        if (currentAudioSessionId == audioSessionId && equalizer != null) {
            return Result.success(Unit)
        }

        releaseEffects()

        currentAudioSessionId = audioSessionId

        return runCatching {
            equalizer = Equalizer(
                EQUALIZER_PRIORITY,
                audioSessionId
            )

            bassBoost = runCatching {
                BassBoost(
                    BASS_BOOST_PRIORITY,
                    audioSessionId
                )
            }.getOrNull()

            restorePreferredState(preferredState)
        }
    }

    private fun restorePreferredState(
        preferredState: MediaPlayerEqualizerState,
    ) {
        val effect = equalizer ?: return

        runCatching {
            val presetCount = effect.numberOfPresets.toInt()

            if (
                preferredState.selectedPresetIndex != null &&
                presetCount > 0 &&
                preferredState.selectedPresetIndex in 0 until presetCount
            ) {
                effect.usePreset(preferredState.selectedPresetIndex.toShort())
            } else {
                restoreBandLevels(
                    effect = effect,
                    bands = preferredState.bands
                )
            }

            effect.enabled = preferredState.isEnabled
        }

        runCatching {
            val safeBassStrength = preferredState.bassBoostStrength.coerceIn(
                minimumValue = MediaPlayerEqualizerState.DEFAULT_BASS_BOOST_STRENGTH,
                maximumValue = MediaPlayerEqualizerState.MAX_BASS_BOOST_STRENGTH
            )

            bassBoost?.setStrength(safeBassStrength.toShort())
            bassBoost?.enabled = preferredState.isEnabled && safeBassStrength > 0
        }
    }

    private fun restoreBandLevels(
        effect: Equalizer,
        bands: List<MediaPlayerEqualizerBandState>,
    ) {
        if (bands.isEmpty()) return

        val bandCount = effect.numberOfBands.toInt()
        if (bandCount <= 0) return

        val range = effect.bandLevelRange
        val minLevel = range.getOrNull(0)?.toInt()
            ?: MediaPlayerEqualizerState.DEFAULT_MIN_BAND_LEVEL
        val maxLevel = range.getOrNull(1)?.toInt()
            ?: MediaPlayerEqualizerState.DEFAULT_MAX_BAND_LEVEL

        bands.forEach { band ->
            if (band.index !in 0 until bandCount) return@forEach

            val safeLevel = band.level.coerceIn(
                minimumValue = minLevel,
                maximumValue = maxLevel
            )

            runCatching {
                effect.setBandLevel(
                    band.index.toShort(),
                    safeLevel.toShort()
                )
            }
        }
    }

    private fun buildState(
        errorMessage: String?,
    ): MediaPlayerEqualizerState {
        val effect = equalizer ?: return MediaPlayerEqualizerState(
            isSupported = false,
            isEnabled = false,
            errorMessage = errorMessage ?: "Equalizer is not available on this device."
        )

        val range = runCatching {
            effect.bandLevelRange
        }.getOrNull()

        val minLevel = range?.getOrNull(0)?.toInt()
            ?: MediaPlayerEqualizerState.DEFAULT_MIN_BAND_LEVEL

        val maxLevel = range?.getOrNull(1)?.toInt()
            ?: MediaPlayerEqualizerState.DEFAULT_MAX_BAND_LEVEL

        val bands = buildBands(
            effect = effect
        )

        val presets = buildPresets(
            effect = effect
        )

        val selectedPresetIndex = runCatching {
            effect.currentPreset.toInt()
        }.getOrDefault(NO_PRESET_INDEX)
            .takeIf { presetIndex ->
                presetIndex >= 0 && presetIndex < presets.size
            }

        val bassStrength = bassBoost?.let { boost ->
            runCatching {
                boost.roundedStrength.toInt()
            }.getOrDefault(MediaPlayerEqualizerState.DEFAULT_BASS_BOOST_STRENGTH)
        } ?: MediaPlayerEqualizerState.DEFAULT_BASS_BOOST_STRENGTH

        return MediaPlayerEqualizerState(
            isSupported = true,
            isEnabled = runCatching { effect.enabled }.getOrDefault(false),
            bands = bands,
            presets = presets,
            selectedPresetIndex = selectedPresetIndex,
            minBandLevel = minLevel,
            maxBandLevel = maxLevel,
            bassBoostSupported = bassBoost != null,
            bassBoostStrength = bassStrength,
            errorMessage = errorMessage
        )
    }

    private fun buildBands(
        effect: Equalizer,
    ): List<MediaPlayerEqualizerBandState> {
        val bandCount = runCatching {
            effect.numberOfBands.toInt()
        }.getOrDefault(0)

        if (bandCount <= 0) return emptyList()

        return List(bandCount) { index ->
            val band = index.toShort()

            val centerFrequencyHz = runCatching {
                effect.getCenterFreq(band) / MILL_HERTZ_IN_HERTZ
            }.getOrDefault(0)

            val level = runCatching {
                effect.getBandLevel(band).toInt()
            }.getOrDefault(0)

            MediaPlayerEqualizerBandState(
                index = index,
                centerFrequencyHz = centerFrequencyHz,
                frequencyLabel = centerFrequencyHz.toFrequencyLabel(),
                level = level
            )
        }
    }

    private fun buildPresets(
        effect: Equalizer,
    ): List<MediaPlayerEqualizerPresetState> {
        val presetCount = runCatching {
            effect.numberOfPresets.toInt()
        }.getOrDefault(0)

        if (presetCount <= 0) return emptyList()

        return List(presetCount) { index ->
            val name = runCatching {
                effect.getPresetName(index.toShort())
            }.getOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: "Preset ${index + 1}"

            MediaPlayerEqualizerPresetState(
                index = index,
                name = name.replaceFirstChar { char ->
                    if (char.isLowerCase()) {
                        char.titlecase(Locale.getDefault())
                    } else {
                        char.toString()
                    }
                }
            )
        }
    }

    private fun releaseEffects() {
        runCatching {
            bassBoost?.release()
        }

        runCatching {
            equalizer?.release()
        }

        bassBoost = null
        equalizer = null
        currentAudioSessionId = C.AUDIO_SESSION_ID_UNSET
    }

    private fun Int.toFrequencyLabel(): String {
        if (this <= 0) return "--"

        return if (this >= HERTZ_IN_KILOHERTZ) {
            if (this % HERTZ_IN_KILOHERTZ == 0) {
                "${this / HERTZ_IN_KILOHERTZ} kHz"
            } else {
                String.format(
                    Locale.US,
                    "%.1f kHz",
                    this / HERTZ_IN_KILOHERTZ.toFloat()
                )
            }
        } else {
            "$this Hz"
        }
    }

    private fun Throwable.toUserMessage(): String {
        return message?.takeIf { it.isNotBlank() }
            ?: "Equalizer is not supported on this device."
    }

    private companion object {
        private const val EQUALIZER_PRIORITY = 0
        private const val BASS_BOOST_PRIORITY = 0
        private const val INVALID_AUDIO_SESSION_ID = 0
        private const val NO_PRESET_INDEX = -1
        private const val MILL_HERTZ_IN_HERTZ = 1000
        private const val HERTZ_IN_KILOHERTZ = 1000
    }
}