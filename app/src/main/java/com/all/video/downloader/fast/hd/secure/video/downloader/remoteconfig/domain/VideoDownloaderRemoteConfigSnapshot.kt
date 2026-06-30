package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain

import com.core.ads.domain.AdsCoreConfig

data class VideoDownloaderRemoteConfigSnapshot(
    val rawJson: String,
    val appConfig: VideoDownloaderRemoteConfig,
    val adsConfig: AdsCoreConfig,
    val source: RemoteConfigSource,
    val warnings: List<String> = emptyList(),
    val errorMessage: String? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val hasError: Boolean
        get() = errorMessage != null
}

enum class RemoteConfigSource {
    LOCAL_DEFAULT,
    FIREBASE_CACHED,
    FIREBASE_REMOTE_ACTIVATED,
    FALLBACK_AFTER_ERROR
}