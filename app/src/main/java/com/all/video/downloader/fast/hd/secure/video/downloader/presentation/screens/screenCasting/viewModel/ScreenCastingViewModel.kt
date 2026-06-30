package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.events.ScreenCastingEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.events.ScreenCastingNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.states.ScreenCastingPanelPurpose
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.states.ScreenCastingStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.states.ScreenCastingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreenCastingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ScreenCastingStates())
    val state = _state.asStateFlow()

    private val _navEvents = Channel<ScreenCastingNavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    fun onEvent(event: ScreenCastingEvents) {
        when (event) {
            ScreenCastingEvents.BackClicked -> {
                navigateBack()
            }

            is ScreenCastingEvents.LocalNetworkStateChanged -> {
                _state.update {
                    it.copy(
                        isLocalNetworkConnected = event.connected,
                        hasCheckedNetwork = true
                    )
                }
            }

            ScreenCastingEvents.StartCastingClicked -> {
                onStartCastingClicked()
            }

            ScreenCastingEvents.StartCastingDialogDismissed -> {
                _state.update {
                    it.copy(showStartCastingDialog = false)
                }
            }

            ScreenCastingEvents.ConfirmStartCastingClicked -> {
                onConfirmStartCastingClicked()
            }

            ScreenCastingEvents.NetworkWarningDismissed -> {
                _state.update {
                    it.copy(showNetworkWarningDialog = false)
                }
            }

            ScreenCastingEvents.ContinueWithoutNetworkClicked -> {
                _state.update {
                    it.copy(showNetworkWarningDialog = false)
                }

                openSystemCastPanel(
                    purpose = ScreenCastingPanelPurpose.StartCasting
                )
            }

            ScreenCastingEvents.OpenWifiSettingsClicked -> {
                openWifiSettings()
            }

            ScreenCastingEvents.ReturnedFromSystemCastPanel -> {
                onReturnedFromSystemCastPanel()
            }

            ScreenCastingEvents.CastingStartedConfirmed -> {
                _state.update {
                    it.copy(
                        castStatus = ScreenCastingStatus.CastingActive,
                        showStartReturnConfirmationDialog = false,
                        awaitingSystemCastReturn = false,
                        errorMessage = null
                    )
                }
            }

            ScreenCastingEvents.CastingNotStartedConfirmed -> {
                _state.update {
                    it.copy(
                        castStatus = ScreenCastingStatus.CastingInactive,
                        showStartReturnConfirmationDialog = false,
                        awaitingSystemCastReturn = false,
                        errorMessage = null
                    )
                }
            }

            ScreenCastingEvents.StartReturnConfirmationDismissed -> {
                _state.update {
                    it.copy(
                        showStartReturnConfirmationDialog = false,
                        castStatus = if (it.castStatus == ScreenCastingStatus.SystemPanelOpened) {
                            ScreenCastingStatus.CastingInactive
                        } else {
                            it.castStatus
                        }
                    )
                }
            }

            ScreenCastingEvents.StopCastingClicked -> {
                openSystemCastPanel(
                    purpose = ScreenCastingPanelPurpose.ManageCasting
                )
            }

            ScreenCastingEvents.CastingStillActiveConfirmed -> {
                _state.update {
                    it.copy(
                        castStatus = ScreenCastingStatus.CastingActive,
                        showManageReturnConfirmationDialog = false,
                        awaitingSystemCastReturn = false,
                        errorMessage = null
                    )
                }
            }

            ScreenCastingEvents.CastingStoppedConfirmed -> {
                _state.update {
                    it.copy(
                        castStatus = ScreenCastingStatus.CastingInactive,
                        showManageReturnConfirmationDialog = false,
                        awaitingSystemCastReturn = false,
                        errorMessage = null
                    )
                }
            }

            ScreenCastingEvents.ManageReturnConfirmationDismissed -> {
                _state.update {
                    it.copy(
                        showManageReturnConfirmationDialog = false,
                        castStatus = ScreenCastingStatus.CastingActive
                    )
                }
            }

            ScreenCastingEvents.DismissErrorClicked -> {
                _state.update {
                    it.copy(errorMessage = null)
                }
            }

            ScreenCastingEvents.MarkCastingStoppedClicked -> {
                _state.update {
                    it.copy(
                        castStatus = ScreenCastingStatus.CastingInactive,
                        showManageReturnConfirmationDialog = false,
                        showStartReturnConfirmationDialog = false,
                        errorMessage = null
                    )
                }
            }

            ScreenCastingEvents.TroubleshootingClicked -> {
                _state.update {
                    it.copy(showTroubleshootingDialog = true)
                }
            }

            ScreenCastingEvents.TroubleshootingDismissed -> {
                _state.update {
                    it.copy(showTroubleshootingDialog = false)
                }
            }
        }
    }

    fun onSystemCastPanelUnavailable() {
        _state.update {
            it.copy(
                castStatus = ScreenCastingStatus.Unavailable,
                awaitingSystemCastReturn = false,
                showStartCastingDialog = false,
                showNetworkWarningDialog = false,
                showStartReturnConfirmationDialog = false,
                showManageReturnConfirmationDialog = false,
                showTroubleshootingDialog = false,
                errorMessage = "Unable to open screen casting settings on this device."
            )
        }
    }

    private fun onStartCastingClicked() {
        val currentStatus = _state.value.castStatus

        if (currentStatus == ScreenCastingStatus.CastingActive) {
            openSystemCastPanel(
                purpose = ScreenCastingPanelPurpose.ManageCasting
            )
            return
        }

        _state.update {
            it.copy(
                showStartCastingDialog = true,
                errorMessage = null
            )
        }
    }

    private fun onConfirmStartCastingClicked() {
        val current = _state.value

        if (!current.isLocalNetworkConnected) {
            _state.update {
                it.copy(
                    showStartCastingDialog = false,
                    showNetworkWarningDialog = true
                )
            }
            return
        }

        openSystemCastPanel(
            purpose = ScreenCastingPanelPurpose.StartCasting
        )
    }

    private fun openSystemCastPanel(
        purpose: ScreenCastingPanelPurpose
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    castStatus = ScreenCastingStatus.OpeningSystemPanel,
                    awaitingSystemCastReturn = true,
                    castPanelPurpose = purpose,
                    showStartCastingDialog = false,
                    showNetworkWarningDialog = false,
                    showStartReturnConfirmationDialog = false,
                    showManageReturnConfirmationDialog = false,
                    errorMessage = null
                )
            }

            _navEvents.send(ScreenCastingNavEvents.OpenSystemCastSettings)
        }
    }

    private fun openWifiSettings() {
        viewModelScope.launch {
            _navEvents.send(ScreenCastingNavEvents.OpenWifiSettings)
        }
    }

    private fun onReturnedFromSystemCastPanel() {
        _state.update { current ->
            if (!current.awaitingSystemCastReturn) {
                current
            } else {
                when (current.castPanelPurpose) {
                    ScreenCastingPanelPurpose.StartCasting -> {
                        current.copy(
                            castStatus = ScreenCastingStatus.SystemPanelOpened,
                            awaitingSystemCastReturn = false,
                            lastOpenedSystemPanel = true,
                            showStartCastingDialog = false,
                            showNetworkWarningDialog = false,
                            showStartReturnConfirmationDialog = true,
                            showManageReturnConfirmationDialog = false,
                            errorMessage = null
                        )
                    }

                    ScreenCastingPanelPurpose.ManageCasting -> {
                        current.copy(
                            castStatus = ScreenCastingStatus.CastingActive,
                            awaitingSystemCastReturn = false,
                            lastOpenedSystemPanel = true,
                            showStartCastingDialog = false,
                            showNetworkWarningDialog = false,
                            showStartReturnConfirmationDialog = false,
                            showManageReturnConfirmationDialog = true,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _navEvents.send(ScreenCastingNavEvents.NavigateBack)
        }
    }
}