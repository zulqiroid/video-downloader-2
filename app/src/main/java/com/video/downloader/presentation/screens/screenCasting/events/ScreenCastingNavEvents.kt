package com.video.downloader.presentation.screens.screenCasting.events

sealed interface ScreenCastingNavEvents {

    data object NavigateBack : ScreenCastingNavEvents

    data object OpenSystemCastSettings : ScreenCastingNavEvents

    data object OpenWifiSettings : ScreenCastingNavEvents
}