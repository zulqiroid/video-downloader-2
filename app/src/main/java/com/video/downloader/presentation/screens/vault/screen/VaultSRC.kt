package com.video.downloader.presentation.screens.vault.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.screens.vault.componants.VaultDeleteConfirmDialog
import com.video.downloader.presentation.screens.vault.componants.VaultDeleteSuccessDialog
import com.video.downloader.presentation.screens.vault.componants.VaultDeletingFileDialog
import com.video.downloader.presentation.screens.vault.componants.VaultFirstTimeDialog
import com.video.downloader.presentation.screens.vault.componants.VaultHomeContent
import com.video.downloader.presentation.screens.vault.componants.VaultIncorrectPinDialog
import com.video.downloader.presentation.screens.vault.componants.VaultOriginalDeleteInfoDialog
import com.video.downloader.presentation.screens.vault.componants.VaultPinSetupContent
import com.video.downloader.presentation.screens.vault.componants.VaultPreparingPlaybackDialog
import com.video.downloader.presentation.screens.vault.componants.VaultRestoreConfirmDialog
import com.video.downloader.presentation.screens.vault.componants.VaultRestoreSuccessDialog
import com.video.downloader.presentation.screens.vault.componants.VaultRestoringFileDialog
import com.video.downloader.presentation.screens.vault.componants.VaultSecuringFileDialog
 import com.video.downloader.presentation.screens.vault.componants.VaultUnlockContent
import com.video.downloader.presentation.screens.vault.events.VaultEvents
import com.video.downloader.presentation.screens.vault.states.VaultStates
import com.video.downloader.presentation.theme.AppColors

@Composable
fun VaultSRC(
    state: VaultStates,
    onEvent: (VaultEvents) -> Unit,
    modifier: Modifier = Modifier,
    mainPaddingValues: PaddingValues,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.Background
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = mainPaddingValues.calculateBottomPadding() + 10.dp
                )
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = AppColors.HighlightGradientTop,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.showPinSetupScreen -> {
                    VaultPinSetupContent(
                        state = state,
                        onEvent = onEvent
                    )
                }

                state.showUnlockScreen -> {
                    VaultUnlockContent(
                        state = state,
                        onEvent = onEvent
                    )
                }

                state.isVaultConfigured && state.isVaultUnlocked -> {
                    VaultHomeContent(
                        items = state.vaultItems,
                        onAddClick = {
                            onEvent(VaultEvents.AddVaultFileClicked)
                        },
                        onItemClick = { item ->
                            onEvent(VaultEvents.VaultItemClicked(item.id))
                        },
                        onRestoreClick = { item ->
                            onEvent(VaultEvents.RestoreVaultFileClicked(item.id))
                        },
                        onDeleteClick = { item ->
                            onEvent(VaultEvents.DeleteVaultFileClicked(item.id))
                        }
                    )
                }
            }

            if (state.showOriginalDeleteInfoDialog) {
                VaultOriginalDeleteInfoDialog(
                    fileName = state.pendingOriginalDeleteFileName,
                    onDeleteOriginalClick = {
                        onEvent(VaultEvents.OriginalDeleteInfoConfirmClicked)
                    },
                    onKeepOriginalClick = {
                        onEvent(VaultEvents.OriginalDeleteInfoKeepOriginalClicked)
                    }
                )
            }

            if (state.showFirstTimeDialog) {
                VaultFirstTimeDialog(
                    onSetPinClick = {
                        onEvent(VaultEvents.FirstTimeSetPinClicked)
                    },
                    onCancelClick = {
                        onEvent(VaultEvents.FirstTimeCancelClicked)
                    }
                )
            }

            if (state.showIncorrectPinDialog) {
                VaultIncorrectPinDialog(
                    onRetryClick = {
                        onEvent(VaultEvents.IncorrectPinRetryClicked)
                    },
                    onForgotPinClick = {
                        onEvent(VaultEvents.ForgotPinClicked)
                    }
                )
            }
            if (state.isAddingFile) {
                VaultSecuringFileDialog()
            }

            if (state.isPreparingPlayback) {
                VaultPreparingPlaybackDialog()
            }

            if (state.showRestoreConfirmDialog) {
                VaultRestoreConfirmDialog(
                    fileName = state.restoreTargetFileName,
                    onRestoreClick = {
                        onEvent(VaultEvents.RestoreDialogConfirmClicked)
                    },
                    onCancelClick = {
                        onEvent(VaultEvents.RestoreDialogDismissed)
                    }
                )
            }

            if (state.isRestoringFile) {
                VaultRestoringFileDialog()
            }

            if (state.showRestoreSuccessDialog) {
                VaultRestoreSuccessDialog(
                    onDoneClick = {
                        onEvent(VaultEvents.RestoreSuccessDismissed)
                    }
                )
            }

            if (state.showDeleteVaultConfirmDialog) {
                VaultDeleteConfirmDialog(
                    fileName = state.deleteTargetFileName,
                    onDeleteClick = {
                        onEvent(VaultEvents.DeleteVaultDialogConfirmClicked)
                    },
                    onCancelClick = {
                        onEvent(VaultEvents.DeleteVaultDialogDismissed)
                    }
                )
            }

            if (state.isDeletingVaultFile) {
                VaultDeletingFileDialog()
            }

            if (state.showDeleteVaultSuccessDialog) {
                VaultDeleteSuccessDialog(
                    onDoneClick = {
                        onEvent(VaultEvents.DeleteVaultSuccessDismissed)
                    }
                )
            }



            state.errorMessage?.let { message ->
                androidx.compose.material3.Text(
                    text = message,
                    color = AppColors.Error,
                    style = com.video.downloader.presentation.theme.AppTextStyles.bodySmall,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                )
            }
        }
    }
}