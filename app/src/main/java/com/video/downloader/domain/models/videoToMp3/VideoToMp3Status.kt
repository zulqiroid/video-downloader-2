package com.video.downloader.domain.models.videoToMp3

enum class VideoToMp3Status {
    QUEUED,
    CONVERTING,
    READY_TO_SAVE,
    SAVING,
    SAVED,
    FAILED,
    CANCELLED
}