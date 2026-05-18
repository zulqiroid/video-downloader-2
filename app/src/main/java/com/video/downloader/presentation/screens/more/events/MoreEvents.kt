package com.video.downloader.presentation.screens.more.events

import com.video.downloader.presentation.screens.more.states.SettingsItem

sealed interface MoreEvents {
    object BackClicked : MoreEvents
    data class OnSettingItemClicked(val item: SettingsItem) : MoreEvents

    data class OnNotificationCheckedChange(val isChecked: Boolean) : MoreEvents

}