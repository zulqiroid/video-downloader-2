package com.all.video.downloader.fast.hd.secure.video.downloader.data.feedback

import android.content.Context
import android.os.Build
import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackDiagnosticsProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun buildDiagnosticsBlock(
        selectedLanguageName: String,
        notificationsEnabled: Boolean,
        downloadLocationLabel: String
    ): String {
        return buildString {
            appendLine("---- App Information ----")
            appendLine("App Name: Video Downloader")
            appendLine("Package: ${BuildConfig.APPLICATION_ID}")
            appendLine("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("Build Type: ${BuildConfig.BUILD_TYPE}")
            appendLine()
            appendLine("---- Device Information ----")
            appendLine("Manufacturer: ${Build.MANUFACTURER.orUnknown()}")
            appendLine("Brand: ${Build.BRAND.orUnknown()}")
            appendLine("Model: ${Build.MODEL.orUnknown()}")
            appendLine("Device: ${Build.DEVICE.orUnknown()}")
            appendLine("Android Version: ${Build.VERSION.RELEASE.orUnknown()} / API ${Build.VERSION.SDK_INT}")
            appendLine()
            appendLine("---- User Settings ----")
            appendLine("Selected Language: $selectedLanguageName")
            appendLine("Notifications Enabled: $notificationsEnabled")
            appendLine("Download Location: $downloadLocationLabel")
            appendLine()
            appendLine("---- Environment ----")
            appendLine("Locale: ${Locale.getDefault()}")
            appendLine("Time Zone: ${TimeZone.getDefault().id}")
            appendLine("Submitted At: ${currentTimestamp()}")
        }
    }

    private fun currentTimestamp(): String {
        val formatter = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss z",
            Locale.US
        )

        return formatter.format(Date())
    }

    private fun String?.orUnknown(): String {
        return this
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: "Unknown"
    }
}