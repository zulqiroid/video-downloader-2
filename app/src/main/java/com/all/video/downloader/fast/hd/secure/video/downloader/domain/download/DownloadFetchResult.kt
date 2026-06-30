package com.all.video.downloader.fast.hd.secure.video.downloader.domain.download

data class DownloadFetchResult(
    val title: String,
    val platform: String,
    val thumbnailUrl: String?,
    val downloadables: List<DownloadableMedia>
) {
    val bestDownloadable: DownloadableMedia?
        get() {
            return downloadables.firstOrNull { item ->
                item.quality.contains("Without Watermark", ignoreCase = true)
            } ?: downloadables.firstOrNull { item ->
                item.quality.contains("HD", ignoreCase = true) ||
                        item.quality.contains("720", ignoreCase = true)
            } ?: downloadables.firstOrNull()
        }
}

data class DownloadableMedia(
    val quality: String,
    val url: String
)