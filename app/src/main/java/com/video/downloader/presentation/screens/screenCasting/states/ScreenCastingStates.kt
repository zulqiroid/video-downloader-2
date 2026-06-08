package com.video.downloader.presentation.screens.screenCasting.states

import androidx.compose.runtime.Immutable

@Immutable
data class ScreenCastingStates(
    val castStatus: ScreenCastingStatus = ScreenCastingStatus.Ready,

    val isLocalNetworkConnected: Boolean = false,
    val hasCheckedNetwork: Boolean = false,

    val showStartCastingDialog: Boolean = false,
    val showNetworkWarningDialog: Boolean = false,
    val showStartReturnConfirmationDialog: Boolean = false,
    val showManageReturnConfirmationDialog: Boolean = false,
    val showTroubleshootingDialog: Boolean = false,

    val awaitingSystemCastReturn: Boolean = false,
    val castPanelPurpose: ScreenCastingPanelPurpose = ScreenCastingPanelPurpose.StartCasting,

    val lastOpenedSystemPanel: Boolean = false,
    val errorMessage: String? = null
) {
    val statusTitle: String
        get() {
            return when (castStatus) {
                ScreenCastingStatus.Ready -> "Ready to Cast"
                ScreenCastingStatus.OpeningSystemPanel -> "Opening Cast Panel"
                ScreenCastingStatus.SystemPanelOpened -> "Cast Panel Opened"
                ScreenCastingStatus.CastingActive -> "Casting Active"
                ScreenCastingStatus.CastingInactive -> "Casting Inactive"
                ScreenCastingStatus.Unavailable -> "Casting Not Available"
            }
        }

    val statusDescription: String
        get() {
            return when (castStatus) {
                ScreenCastingStatus.Ready -> {
                    "Start Android screen casting and select your TV from the system panel."
                }

                ScreenCastingStatus.OpeningSystemPanel -> {
                    "Please wait while the casting panel is opening..."
                }

                ScreenCastingStatus.SystemPanelOpened -> {
                    "Confirm whether screen casting started after selecting your TV."
                }

                ScreenCastingStatus.CastingActive -> {
                    "Your screen casting session is marked as active. Android system is handling the mirroring."
                }

                ScreenCastingStatus.CastingInactive -> {
                    "Casting is not active. You can open the cast panel again and select a supported TV."
                }

                ScreenCastingStatus.Unavailable -> {
                    "This device does not support opening the system casting panel."
                }
            }
        }

    val networkTitle: String
        get() {
            return when {
                !hasCheckedNetwork -> "Checking Network"
                isLocalNetworkConnected -> "Local Network Ready"
                else -> "Wi-Fi Not Detected"
            }
        }

    val networkDescription: String
        get() {
            return when {
                !hasCheckedNetwork -> {
                    "Checking whether your phone is connected to a local network..."
                }

                isLocalNetworkConnected -> {
                    "Your phone is connected to a local network. Make sure your TV is on the same Wi-Fi."
                }

                else -> {
                    "Connect your phone and TV to the same Wi-Fi for better casting discovery."
                }
            }
        }

    val primaryButtonText: String
        get() {
            return when (castStatus) {
                ScreenCastingStatus.CastingActive -> "Manage / Stop Casting"
                ScreenCastingStatus.OpeningSystemPanel -> "Opening..."
                else -> "Start Screen Casting"
            }
        }

    val isPrimaryButtonLoading: Boolean
        get() = castStatus == ScreenCastingStatus.OpeningSystemPanel

    val isCastingActive: Boolean
        get() = castStatus == ScreenCastingStatus.CastingActive
}

enum class ScreenCastingStatus {
    Ready,
    OpeningSystemPanel,
    SystemPanelOpened,
    CastingActive,
    CastingInactive,
    Unavailable
}

enum class ScreenCastingPanelPurpose {
    StartCasting,
    ManageCasting
}