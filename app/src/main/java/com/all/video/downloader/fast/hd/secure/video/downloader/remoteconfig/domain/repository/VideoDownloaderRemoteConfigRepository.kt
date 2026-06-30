package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository

import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.VideoDownloaderRemoteConfigSnapshot
import kotlinx.coroutines.flow.StateFlow

interface VideoDownloaderRemoteConfigRepository {

    val configState: StateFlow<VideoDownloaderRemoteConfigSnapshot>

    fun current(): VideoDownloaderRemoteConfigSnapshot

    /**
     * Applies Firebase settings + defaults and reads currently activated/cached config.
     * This should be called before fetchAndActivate().
     */
    suspend fun initialize(): VideoDownloaderRemoteConfigSnapshot

    /**
     * Fetches Firebase Remote Config, activates it, then parses the latest JSON.
     */
    suspend fun fetchAndActivate(): VideoDownloaderRemoteConfigSnapshot

}