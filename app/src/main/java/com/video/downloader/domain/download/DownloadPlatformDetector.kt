package com.video.downloader.domain.download

 import javax.inject.Inject

class DownloadPlatformDetector @Inject constructor() {

    fun detect(url: String): DownloadPlatform? {
        val lowerUrl = url.trim().lowercase()

        return when {
            lowerUrl.contains("facebook.com") ||
                    lowerUrl.contains("fb.watch") ||
                    lowerUrl.contains("fb.com") -> DownloadPlatform.Facebook

            lowerUrl.contains("instagram.com") ||
                    lowerUrl.contains("instagr.am") -> DownloadPlatform.Instagram

            lowerUrl.contains("tiktok.com") ||
                    lowerUrl.contains("vm.tiktok.com") ||
                    lowerUrl.contains("vt.tiktok.com") -> DownloadPlatform.TikTok

            lowerUrl.contains("threads.net") -> DownloadPlatform.Threads

            lowerUrl.contains("snackvideo.com") ||
                    lowerUrl.contains("sck.io") -> DownloadPlatform.Snack

            lowerUrl.contains("likee.video") ||
                    lowerUrl.contains("likee.com") -> DownloadPlatform.Likee

            else -> null
        }
    }
}