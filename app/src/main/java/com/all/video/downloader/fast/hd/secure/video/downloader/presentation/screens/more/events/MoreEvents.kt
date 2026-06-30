package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.settings.DownloadLocationType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.FeedbackCategory
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.SettingsItem

sealed interface MoreEvents {

    data object BackClicked : MoreEvents

    data class OnSettingItemClicked(
        val item: SettingsItem
    ) : MoreEvents

    data class OnNotificationCheckedChange(
        val isChecked: Boolean
    ) : MoreEvents

    data class OnSdCardAvailabilityChanged(
        val isAvailable: Boolean
    ) : MoreEvents

    data object RateNowClicked : MoreEvents
    data object RateLaterClicked : MoreEvents
    data object DismissRateDialog : MoreEvents

    data object DismissDownloadLocationDialog : MoreEvents

    data class OnDownloadLocationOptionSelected(
        val type: DownloadLocationType
    ) : MoreEvents

    data class OnCustomFolderPicked(
        val treeUri: String
    ) : MoreEvents

    data object SaveDownloadLocationClicked : MoreEvents

    data object DismissFeedbackDialog : MoreEvents

    data class OnFeedbackCategorySelected(
        val category: FeedbackCategory
    ) : MoreEvents

    data class OnFeedbackMessageChanged(
        val message: String
    ) : MoreEvents

    data object SubmitFeedbackClicked : MoreEvents
}