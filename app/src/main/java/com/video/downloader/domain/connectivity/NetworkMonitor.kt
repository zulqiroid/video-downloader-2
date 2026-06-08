package com.video.downloader.domain.connectivity

interface NetworkMonitor {

    fun isInternetAvailable(): Boolean
}