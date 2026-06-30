package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.settings.DownloadLocationType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.states.AppLanguageUiModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.states.defaultAppLanguages

data class MoreStates(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedLanguage: AppLanguageUiModel = defaultAppLanguages.first(),

    val isNotificationEnabled: Boolean = true,

    val isSdCardAvailable: Boolean = false,

    val selectedDownloadLocationType: DownloadLocationType = DownloadLocationType.INTERNAL_STORAGE,
    val selectedCustomFolderUri: String? = null,

    val pendingDownloadLocationType: DownloadLocationType = DownloadLocationType.INTERNAL_STORAGE,
    val pendingCustomFolderUri: String? = null,

    val showRateDialog: Boolean = false,
    val showDownloadLocationDialog: Boolean = false,

    val showFeedbackDialog: Boolean = false,
    val selectedFeedbackCategory: FeedbackCategory = FeedbackCategory.APP_CRASHES,
    val feedbackMessage: String = "",
    val feedbackError: String? = null,
    val isSubmittingFeedback: Boolean = false
) {
    val downloadLocationLabel: String
        get() = when (selectedDownloadLocationType) {
            DownloadLocationType.INTERNAL_STORAGE -> "Internal Storage"
            DownloadLocationType.SD_CARD -> "SD Card"
            DownloadLocationType.CUSTOM_FOLDER -> "Custom Folder"
        }
}