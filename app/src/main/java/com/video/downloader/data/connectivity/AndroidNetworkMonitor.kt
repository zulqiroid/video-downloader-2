package com.video.downloader.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.video.downloader.domain.connectivity.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitor {

    override fun isInternetAvailable(): Boolean {
        return runCatching {
            val connectivityManager = context.getSystemService(
                ConnectivityManager::class.java
            ) ?: return false

            val activeNetwork = connectivityManager.activeNetwork ?: return false

            val capabilities = connectivityManager.getNetworkCapabilities(
                activeNetwork
            ) ?: return false

            val hasInternetCapability = capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )

            val hasValidatedInternet = capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )

            hasInternetCapability && hasValidatedInternet
        }.getOrDefault(false)
    }
}