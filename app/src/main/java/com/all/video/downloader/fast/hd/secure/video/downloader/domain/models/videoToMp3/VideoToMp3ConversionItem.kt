package com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3

data class VideoToMp3ConversionItem(
    val id: String,
    val inputUri: String,
    val fileName: String,
    val selectedPreset: VideoToMp3Preset,
    val resolvedConfig: Mp3AudioConfig? = null,
    val progress: Int = 0,
    val status: VideoToMp3Status = VideoToMp3Status.QUEUED,

    /**
     * Worker ka temporary converted MP3.
     * Ye app private storage me hoga.
     */
    val tempOutputPath: String? = null,

    /**
     * Final saved file Uri.
     * Ye sirf user ke save button press karne ke baad milega.
     */
    val savedOutputUri: String? = null,

    val fileSizeBytes: Long = 0L,
    val errorMessage: String? = null
)