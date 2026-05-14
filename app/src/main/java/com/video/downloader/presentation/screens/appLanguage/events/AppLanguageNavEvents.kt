package com.video.downloader.presentation.screens.appLanguage.events

sealed interface AppLanguageNavEvents {

    data object NavigateBack : AppLanguageNavEvents

    data object NavigateToMain : AppLanguageNavEvents
}