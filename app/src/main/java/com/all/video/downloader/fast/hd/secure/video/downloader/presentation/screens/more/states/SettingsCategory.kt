package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states

import androidx.annotation.StringRes
 import com.all.video.downloader.fast.hd.secure.video.downloader.R

data class SettingsCategory(
    @StringRes val title: Int,
    val items: List<SettingsItem>
)

val settingsCategories = listOf(

    SettingsCategory(
        title = R.string.general,
        items = listOf(
            SettingsItem.ShareApp,
            SettingsItem.RateApp,
            SettingsItem.Language,
            SettingsItem.Feedback,
            SettingsItem.PrivacyPolicy,
        )
    ),
    SettingsCategory(
        title = R.string.downloads_storage,
        items = listOf(
            SettingsItem.DownloadLocation,
            SettingsItem.Notification,
        )
    ),
    SettingsCategory(
        title = R.string.support,
        items = listOf(
            SettingsItem.DownloadGuide,
        )
    ),

)