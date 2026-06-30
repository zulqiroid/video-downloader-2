package com.all.video.downloader.fast.hd.secure.video.downloader.domain.download

enum class DownloadPlatform(
    val endpointPath: String,
    val displayName: String
) {
    Facebook(
        endpointPath = "fbdownloader",
        displayName = "Facebook"
    ),
    Instagram(
        endpointPath = "instadownloader",
        displayName = "Instagram"
    ),
    TikTok(
        endpointPath = "ttdownloader",
        displayName = "TikTok"
    ),
    Threads(
        endpointPath = "thdownloader",
        displayName = "Threads"
    ),
    Snack(
        endpointPath = "snackdownloader",
        displayName = "Snack"
    ),
    Likee(
        endpointPath = "likeedownl",
        displayName = "Likee"
    )
}