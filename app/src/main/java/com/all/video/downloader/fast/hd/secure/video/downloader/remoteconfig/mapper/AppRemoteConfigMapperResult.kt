package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.mapper

import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.VideoDownloaderRemoteConfig

sealed interface AppRemoteConfigMapperResult {

    data class Success(
        val config: VideoDownloaderRemoteConfig,
        val warnings: List<String>
    ) : AppRemoteConfigMapperResult

    data class Failure(
        val fallbackConfig: VideoDownloaderRemoteConfig,
        val errorMessage: String
    ) : AppRemoteConfigMapperResult
}