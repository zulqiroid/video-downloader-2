package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.states

data class VaultStates(
    val isLoading: Boolean = true,

    val isVaultConfigured: Boolean = false,
    val isVaultUnlocked: Boolean = false,

    val showFirstTimeDialog: Boolean = false,
    val showPinSetupScreen: Boolean = false,
    val showUnlockScreen: Boolean = false,

    val showIncorrectPinDialog: Boolean = false,

    val pin: String = "",
    val confirmPin: String = "",
    val unlockPin: String = "",

    val biometricAvailable: Boolean = false,
    val biometricEnabled: Boolean = false,

    val isSaving: Boolean = false,
    val isVerifying: Boolean = false,

    val wrongPinAttempts: Int = 0,
    val lockoutRemainingSeconds: Int = 0,

    val isAddingFile: Boolean = false,
    val isPreparingPlayback: Boolean = false,
    val vaultItems: List<VaultUiItem> = emptyList(),
    val errorMessage: String? = null,
    val unlockErrorMessage: String? = null,


    val showOriginalDeleteInfoDialog: Boolean = false,
    val pendingOriginalDeleteVaultFileId: String? = null,
    val pendingOriginalDeleteUri: String? = null,
    val pendingOriginalDeleteFileName: String = "",


    val showRestoreConfirmDialog: Boolean = false,
    val restoreTargetFileId: String? = null,
    val restoreTargetFileName: String = "",
    val isRestoringFile: Boolean = false,
    val showRestoreSuccessDialog: Boolean = false,
    val restoredOutputUri: String? = null,


    val showDeleteVaultConfirmDialog: Boolean = false,
    val deleteTargetFileId: String? = null,
    val deleteTargetFileName: String = "",
    val isDeletingVaultFile: Boolean = false,
    val showDeleteVaultSuccessDialog: Boolean = false,
) {
    val canSavePin: Boolean
        get() {
            return pin.length == 4 &&
                    confirmPin.length == 4 &&
                    pin == confirmPin &&
                    !isSaving
        }

    val isPinLockedOut: Boolean
        get() = lockoutRemainingSeconds > 0

    val canUnlockWithPin: Boolean
        get() {
            return unlockPin.length == 4 &&
                    !isVerifying &&
                    !isPinLockedOut
        }

    val canUseBiometricUnlock: Boolean
        get() = biometricAvailable && biometricEnabled && !isVaultUnlocked
}

data class VaultUiItem(
    val id: String,
    val title: String,
    val sizeText: String,
    val extension: String,
    val mediaType: VaultMediaType,
    val thumbnailUri: String? = null
) {
    val metaText: String
        get() {
            return listOf(
                sizeText,
                extension.uppercase()
            ).filter { it.isNotBlank() }
                .joinToString(" • ")
        }
}

enum class VaultMediaType {
    VIDEO,
    AUDIO
}