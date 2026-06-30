package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.events

import android.net.Uri

sealed interface VaultEvents {

    data object FirstTimeSetPinClicked : VaultEvents
    data object FirstTimeCancelClicked : VaultEvents

    data object BackClicked : VaultEvents
    data object ScreenLeft : VaultEvents

    data class PinChanged(
        val value: String
    ) : VaultEvents

    data class ConfirmPinChanged(
        val value: String
    ) : VaultEvents

    data class UnlockPinChanged(
        val value: String
    ) : VaultEvents

    data object UnlockPinClicked : VaultEvents

    data object IncorrectPinRetryClicked : VaultEvents
    data object ForgotPinClicked : VaultEvents

    data class BiometricAvailabilityChanged(
        val available: Boolean
    ) : VaultEvents

    data class BiometricToggleChanged(
        val enabled: Boolean
    ) : VaultEvents

    data object BiometricUnlockClicked : VaultEvents
    data object BiometricAuthSucceeded : VaultEvents

    data class BiometricAuthFailed(
        val message: String
    ) : VaultEvents

    data object BiometricAuthCancelled : VaultEvents

    data object SavePinClicked : VaultEvents

    data object AddVaultFileClicked : VaultEvents

    data class VaultFileSelected(
        val uri: Uri
    ) : VaultEvents

    data class OriginalFileDeleteResult(
        val vaultFileId: String,
        val deleted: Boolean,
        val message: String? = null
    ) : VaultEvents

    data object OriginalDeleteInfoConfirmClicked : VaultEvents
    data object OriginalDeleteInfoKeepOriginalClicked : VaultEvents

    data class VaultItemClicked(
        val id: String
    ) : VaultEvents


    data class RestoreVaultFileClicked(
        val id: String
    ) : VaultEvents

    data object RestoreDialogConfirmClicked : VaultEvents

    data object RestoreDialogDismissed : VaultEvents

    data object RestoreSuccessDismissed : VaultEvents


    data class DeleteVaultFileClicked(
        val id: String
    ) : VaultEvents

    data object DeleteVaultDialogConfirmClicked : VaultEvents

    data object DeleteVaultDialogDismissed : VaultEvents

    data object DeleteVaultSuccessDismissed : VaultEvents

}