package com.video.downloader.presentation.screens.more.states

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.graphics.vector.ImageVector
import com.video.downloader.R

sealed class SettingsItem(
    val icon: Int,
    @StringRes val title: Int,

    val detail: Int? = null,
    val description: Int? = null,
    val openIcon: ImageVector? = null,
 ) {

    data object Language : SettingsItem(
        icon = R.drawable.ic_globe_filled,
        title = R.string.language,
        openIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
    )

    data object RateApp : SettingsItem(
        icon = R.drawable.ic_star_filled,
        title = R.string.rate_us,
        openIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
    )

    data object ShareApp : SettingsItem(
        icon = R.drawable.ic_share_filled,
        title = R.string.share_app,
        openIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
    )

    data object DownloadLocation : SettingsItem(
        icon = R.drawable.ic_folder_filled,
        title = R.string.download_location,
        description = R.string.downloads_videosaver,
        openIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
    )
    data object Notification : SettingsItem(
        icon = R.drawable.ic_bell_filled,
        title = R.string.notifications,
      )

    data object DownloadGuide : SettingsItem(
        icon = R.drawable.ic_info_filled,
        title = R.string.download_guide,
        openIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
    )


}