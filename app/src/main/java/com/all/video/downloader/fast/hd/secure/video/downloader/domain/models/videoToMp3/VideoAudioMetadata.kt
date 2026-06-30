package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3

data class VideoAudioMetadata(
    val fileName: String,
    val durationUs: Long,
    val audioMime: String,
    val sampleRate: Int,
    val channelCount: Int,
    val bitrate: Int?
) {
    val durationMinutes: Long
        get() = durationUs / 1_000_000L / 60L
}