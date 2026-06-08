package com.video.downloader.presentation.screens.screenCasting.events

sealed interface ScreenCastingEvents {

    data object BackClicked : ScreenCastingEvents

    data class LocalNetworkStateChanged(
        val connected: Boolean
    ) : ScreenCastingEvents

    data object StartCastingClicked : ScreenCastingEvents

    data object StartCastingDialogDismissed : ScreenCastingEvents

    data object ConfirmStartCastingClicked : ScreenCastingEvents

    data object NetworkWarningDismissed : ScreenCastingEvents

    data object ContinueWithoutNetworkClicked : ScreenCastingEvents

    data object OpenWifiSettingsClicked : ScreenCastingEvents

    data object ReturnedFromSystemCastPanel : ScreenCastingEvents

    data object CastingStartedConfirmed : ScreenCastingEvents

    data object CastingNotStartedConfirmed : ScreenCastingEvents

    data object StartReturnConfirmationDismissed : ScreenCastingEvents

    data object StopCastingClicked : ScreenCastingEvents

    data object CastingStillActiveConfirmed : ScreenCastingEvents

    data object CastingStoppedConfirmed : ScreenCastingEvents

    data object ManageReturnConfirmationDismissed : ScreenCastingEvents

    data object MarkCastingStoppedClicked : ScreenCastingEvents

    data object TroubleshootingClicked : ScreenCastingEvents

    data object TroubleshootingDismissed : ScreenCastingEvents

    data object DismissErrorClicked : ScreenCastingEvents
}