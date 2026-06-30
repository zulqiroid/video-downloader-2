package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.events

sealed interface AppLanguageEvents {

    data object BackClicked : AppLanguageEvents

    data class SearchQueryChanged(
        val value: String
    ) : AppLanguageEvents

    data class LanguageSelected(
        val languageCode: String
    ) : AppLanguageEvents

    data object ApplyLanguageClicked : AppLanguageEvents
}