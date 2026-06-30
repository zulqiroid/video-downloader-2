package com.all.video.downloader.fast.hd.secure.video.downloader.data.media.audio

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.Mp3AudioConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoAudioMetadata
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import javax.inject.Inject

class VideoToMp3PresetResolver @Inject constructor() {

    fun resolve(
        selectedPreset: VideoToMp3Preset,
        metadata: VideoAudioMetadata
    ): Mp3AudioConfig {
        return when (selectedPreset) {
            VideoToMp3Preset.VOICE_LECTURE -> Mp3AudioConfig.VoiceLecture
            VideoToMp3Preset.MUSIC_SONG -> Mp3AudioConfig.MusicSong
            VideoToMp3Preset.HIGH_QUALITY -> Mp3AudioConfig.HighQuality
            VideoToMp3Preset.AUTO_RECOMMENDED -> resolveAuto(metadata)
        }
    }

    private fun resolveAuto(metadata: VideoAudioMetadata): Mp3AudioConfig {
        val lowerName = metadata.fileName.lowercase()

        val voiceKeywords = listOf(
            "lecture",
            "class",
            "podcast",
            "bayan",
            "speech",
            "interview",
            "meeting",
            "voice",
            "lesson",
            "audio note",
            "recording"
        )

        val looksLikeVoice = voiceKeywords.any { keyword ->
            lowerName.contains(keyword)
        }

        val isLongMonoAudio =
            metadata.durationMinutes >= 20L &&
                    metadata.channelCount == 1

        if (looksLikeVoice || isLongMonoAudio) {
            return Mp3AudioConfig.VoiceLecture
        }

        if (metadata.channelCount >= 2) {
            return Mp3AudioConfig.MusicSong
        }

        val originalBitrate = metadata.bitrate ?: 0

        if (originalBitrate > 160_000) {
            return Mp3AudioConfig.MusicSong
        }

        return Mp3AudioConfig.VoiceLecture
    }
}