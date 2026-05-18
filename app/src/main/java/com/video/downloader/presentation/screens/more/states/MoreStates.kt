package com.video.downloader.presentation.screens.more.states

import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.appLanguage.states.AppLanguageUiModel
import com.video.downloader.presentation.screens.appLanguage.states.defaultAppLanguages

data class MoreStates(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLanguage: AppLanguageUiModel = defaultAppLanguages.first(),
    val isNotificationEnabled: Boolean = false,
)
