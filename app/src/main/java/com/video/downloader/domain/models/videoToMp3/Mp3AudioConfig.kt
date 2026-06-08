package com.video.downloader.domain.models.videoToMp3

data class Mp3AudioConfig(
    val preset: VideoToMp3Preset,
    val bitrateKbps: Int,
    val channelCount: Int,
    val displayName: String,
    val description: String
) {
    val isMono: Boolean
        get() = channelCount == 1

    companion object {
        val VoiceLecture = Mp3AudioConfig(
            preset = VideoToMp3Preset.VOICE_LECTURE,
            bitrateKbps = 96,
            channelCount = 1,
            displayName = "Voice / Lecture",
            description = "96kbps • Mono • Best for lectures and voice"
        )

        val MusicSong = Mp3AudioConfig(
            preset = VideoToMp3Preset.MUSIC_SONG,
            bitrateKbps = 128,
            channelCount = 2,
            displayName = "Music / Song",
            description = "128kbps • Stereo • Balanced quality"
        )

        val HighQuality = Mp3AudioConfig(
            preset = VideoToMp3Preset.HIGH_QUALITY,
            bitrateKbps = 192,
            channelCount = 2,
            displayName = "High Quality",
            description = "192kbps • Stereo • Larger file"
        )
    }
}