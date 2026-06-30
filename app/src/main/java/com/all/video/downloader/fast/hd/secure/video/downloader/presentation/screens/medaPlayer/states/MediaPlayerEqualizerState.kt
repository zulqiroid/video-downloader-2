package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states

import androidx.compose.runtime.Immutable

@Immutable
data class MediaPlayerEqualizerState(
    val isSupported: Boolean = false,
    val isEnabled: Boolean = false,
    val bands: List<MediaPlayerEqualizerBandState> = emptyList(),
    val presets: List<MediaPlayerEqualizerPresetState> = emptyList(),
    val selectedPresetIndex: Int? = null,
    val minBandLevel: Int = DEFAULT_MIN_BAND_LEVEL,
    val maxBandLevel: Int = DEFAULT_MAX_BAND_LEVEL,
    val bassBoostSupported: Boolean = false,
    val bassBoostStrength: Int = DEFAULT_BASS_BOOST_STRENGTH,
    val errorMessage: String? = null,
) {
    val hasBands: Boolean
        get() = bands.isNotEmpty()

    val hasPresets: Boolean
        get() = presets.isNotEmpty()

    companion object {
        const val DEFAULT_MIN_BAND_LEVEL = -1500
        const val DEFAULT_MAX_BAND_LEVEL = 1500
        const val DEFAULT_BASS_BOOST_STRENGTH = 0
        const val MAX_BASS_BOOST_STRENGTH = 1000
    }
}

@Immutable
data class MediaPlayerEqualizerBandState(
    val index: Int,
    val centerFrequencyHz: Int,
    val frequencyLabel: String,
    val level: Int,
)

@Immutable
data class MediaPlayerEqualizerPresetState(
    val index: Int,
    val name: String,
)