package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.vault.VaultFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.vault.VaultFileType
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.vault.VaultFileUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.vault.VaultSecurityUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.events.VaultEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.events.VaultNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.states.VaultMediaType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.states.VaultStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.states.VaultUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val securityUseCases: VaultSecurityUseCases,
    private val fileUseCases: VaultFileUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(VaultStates())
    val state = _state.asStateFlow()

    private val _navEvents = Channel<VaultNavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    private var lockoutJob: Job? = null
    private var autoBiometricPromptShown = false

    init {
        observeVaultSecurity()
        observeVaultFiles()
    }

    fun onEvent(event: VaultEvents) {
        when (event) {
            VaultEvents.FirstTimeSetPinClicked -> {
                _state.update {
                    it.copy(
                        showFirstTimeDialog = false,
                        showPinSetupScreen = true,
                        showUnlockScreen = false,
                        errorMessage = null
                    )
                }
            }

            VaultEvents.FirstTimeCancelClicked -> {
                navigateHome()
            }

            VaultEvents.BackClicked -> {
                handleBackClicked()
            }

            VaultEvents.ScreenLeft -> {
                lockVaultForPrivacy()
            }

            is VaultEvents.PinChanged -> {
                _state.update {
                    it.copy(
                        pin = event.value.onlyFourDigits(),
                        errorMessage = null
                    )
                }
            }

            is VaultEvents.ConfirmPinChanged -> {
                _state.update {
                    it.copy(
                        confirmPin = event.value.onlyFourDigits(),
                        errorMessage = null
                    )
                }
            }

            is VaultEvents.UnlockPinChanged -> {
                _state.update {
                    it.copy(
                        unlockPin = event.value.onlyFourDigits(),
                        unlockErrorMessage = null
                    )
                }
            }

            VaultEvents.UnlockPinClicked -> {
                verifyPinAndUnlock()
            }

            VaultEvents.IncorrectPinRetryClicked -> {
                _state.update {
                    it.copy(
                        showIncorrectPinDialog = false,
                        unlockPin = "",
                        unlockErrorMessage = null
                    )
                }
            }

            VaultEvents.ForgotPinClicked -> {
                _state.update {
                    it.copy(
                        showIncorrectPinDialog = false,
                        unlockPin = "",
                        unlockErrorMessage = "For privacy, PIN reset should be handled with secure Vault reset. We will add this in the next Vault phase."
                    )
                }
            }

            is VaultEvents.BiometricAvailabilityChanged -> {
                _state.update {
                    it.copy(
                        biometricAvailable = event.available
                    )
                }

                requestAutoBiometricIfPossible()
            }

            is VaultEvents.BiometricToggleChanged -> {
                onBiometricToggleChanged(event.enabled)
            }

            VaultEvents.BiometricUnlockClicked -> {
                requestManualBiometricUnlock()
            }

            VaultEvents.BiometricAuthSucceeded -> {
                unlockVault()
            }

            is VaultEvents.BiometricAuthFailed -> {
                _state.update {
                    it.copy(
                        unlockErrorMessage = event.message
                    )
                }
            }

            VaultEvents.BiometricAuthCancelled -> {
                _state.update {
                    it.copy(
                        unlockErrorMessage = null
                    )
                }
            }

            VaultEvents.SavePinClicked -> {
                savePin()
            }

            VaultEvents.AddVaultFileClicked -> {
                onAddVaultFileClicked()
            }

            is VaultEvents.VaultFileSelected -> {
                addSelectedFileToVault(event.uri.toString())
            }

            is VaultEvents.OriginalFileDeleteResult -> {
                onOriginalFileDeleteResult(
                    vaultFileId = event.vaultFileId,
                    deleted = event.deleted,
                    message = event.message
                )
            }

            is VaultEvents.VaultItemClicked -> {
                onVaultItemClicked(event.id)
            }

            VaultEvents.OriginalDeleteInfoConfirmClicked -> {
                onOriginalDeleteInfoConfirmClicked()
            }

            VaultEvents.OriginalDeleteInfoKeepOriginalClicked -> {
                onOriginalDeleteInfoKeepOriginalClicked()
            }

            is VaultEvents.RestoreVaultFileClicked -> {
                onRestoreVaultFileClicked(event.id)
            }

            VaultEvents.RestoreDialogConfirmClicked -> {
                onRestoreDialogConfirmClicked()
            }

            VaultEvents.RestoreDialogDismissed -> {
                onRestoreDialogDismissed()
            }

            VaultEvents.RestoreSuccessDismissed -> {
                onRestoreSuccessDismissed()
            }

            is VaultEvents.DeleteVaultFileClicked -> {
                onDeleteVaultFileClicked(event.id)
            }

            VaultEvents.DeleteVaultDialogConfirmClicked -> {
                onDeleteVaultDialogConfirmClicked()
            }

            VaultEvents.DeleteVaultDialogDismissed -> {
                onDeleteVaultDialogDismissed()
            }

            VaultEvents.DeleteVaultSuccessDismissed -> {
                onDeleteVaultSuccessDismissed()
            }
        }
    }

    private fun observeVaultSecurity() {
        viewModelScope.launch {
            combine(
                securityUseCases.observeVaultConfigured(),
                securityUseCases.observeBiometricEnabled()
            ) { configured, biometricEnabled ->
                configured to biometricEnabled
            }.collect { (configured, biometricEnabled) ->

                _state.update { current ->
                    val showPinSetup = if (configured) {
                        false
                    } else {
                        current.showPinSetupScreen
                    }

                    val showFirstDialog =
                        !configured && !showPinSetup

                    val showUnlock =
                        configured && !current.isVaultUnlocked

                    current.copy(
                        isLoading = false,
                        isVaultConfigured = configured,
                        biometricEnabled = biometricEnabled,
                        showFirstTimeDialog = showFirstDialog,
                        showPinSetupScreen = showPinSetup,
                        showUnlockScreen = showUnlock,
                        errorMessage = if (configured) null else current.errorMessage
                    )
                }

                requestAutoBiometricIfPossible()
            }
        }
    }

    private fun observeVaultFiles() {
        viewModelScope.launch {
            fileUseCases.observeVaultFiles().collect { files ->
                android.util.Log.d(
                    "VaultDebug",
                    "Vault files observed: ${files.size}"
                )

                _state.update { current ->
                    current.copy(
                        vaultItems = files.map { file ->
                            file.toVaultUiItem()
                        }
                    )
                }
            }
        }
    }

    private fun onAddVaultFileClicked() {
        val current = _state.value

        if (!current.isVaultConfigured || !current.isVaultUnlocked) {
            _state.update {
                it.copy(
                    errorMessage = "Unlock your Vault before adding files."
                )
            }
            return
        }

        viewModelScope.launch {
            _navEvents.send(VaultNavEvents.LaunchVaultFilePicker)
        }
    }

    private fun addSelectedFileToVault(
        sourceUri: String
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isAddingFile = true,
                    errorMessage = null
                )
            }

            runCatching {
                fileUseCases.addFileToVault(sourceUri)
            }.onSuccess { vaultFile ->
                android.util.Log.d(
                    "VaultDebug",
                    "Vault file saved successfully: id=${vaultFile.id}, name=${vaultFile.originalFileName}"
                )

                _state.update {
                    it.copy(
                        showOriginalDeleteInfoDialog = true,
                        pendingOriginalDeleteVaultFileId = vaultFile.id,
                        pendingOriginalDeleteUri = vaultFile.originalUri,
                        pendingOriginalDeleteFileName = vaultFile.originalFileName,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                android.util.Log.e(
                    "VaultDebug",
                    "Vault file save failed",
                    throwable
                )

                _state.update {
                    it.copy(
                        errorMessage = throwable.message ?: "Unable to secure this file."
                    )
                }
            }

            _state.update {
                it.copy(
                    isAddingFile = false
                )
            }
        }
    }

    private fun onOriginalDeleteInfoConfirmClicked() {
        viewModelScope.launch {
            val current = _state.value

            val vaultFileId = current.pendingOriginalDeleteVaultFileId
            val originalUri = current.pendingOriginalDeleteUri

            if (vaultFileId.isNullOrBlank() || originalUri.isNullOrBlank()) {
                _state.update {
                    it.copy(
                        showOriginalDeleteInfoDialog = false,
                        pendingOriginalDeleteVaultFileId = null,
                        pendingOriginalDeleteUri = null,
                        pendingOriginalDeleteFileName = "",
                        errorMessage = "Original file delete request is not available."
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    showOriginalDeleteInfoDialog = false
                )
            }

            _navEvents.send(
                VaultNavEvents.RequestOriginalFileDelete(
                    vaultFileId = vaultFileId,
                    originalUri = originalUri
                )
            )
        }
    }

    private fun onOriginalDeleteInfoKeepOriginalClicked() {
        viewModelScope.launch {
            val vaultFileId = _state.value.pendingOriginalDeleteVaultFileId

            if (!vaultFileId.isNullOrBlank()) {
                fileUseCases.markOriginalFileDeleteFailed(vaultFileId)
            }

            _state.update {
                it.copy(
                    showOriginalDeleteInfoDialog = false,
                    pendingOriginalDeleteVaultFileId = null,
                    pendingOriginalDeleteUri = null,
                    pendingOriginalDeleteFileName = "",
                    errorMessage = "File is secured in Vault, but the original copy is still visible in Gallery."
                )
            }
        }
    }

    private fun onOriginalFileDeleteResult(
        vaultFileId: String,
        deleted: Boolean,
        message: String?
    ) {
        viewModelScope.launch {
            if (deleted) {
                fileUseCases.markOriginalFileDeleted(vaultFileId)

                _state.update {
                    it.copy(
                        pendingOriginalDeleteVaultFileId = null,
                        pendingOriginalDeleteUri = null,
                        pendingOriginalDeleteFileName = "",
                        errorMessage = null
                    )
                }
            } else {
                fileUseCases.markOriginalFileDeleteFailed(vaultFileId)

                _state.update {
                    it.copy(
                        pendingOriginalDeleteVaultFileId = null,
                        pendingOriginalDeleteUri = null,
                        pendingOriginalDeleteFileName = "",
                        errorMessage = message ?: "File is secured in Vault, but original file was not removed from Gallery."
                    )
                }
            }
        }
    }

    private fun handleBackClicked() {
        val current = _state.value

        when {
            current.showPinSetupScreen && !current.isVaultConfigured -> {
                _state.update {
                    it.copy(
                        showPinSetupScreen = false,
                        showFirstTimeDialog = true,
                        pin = "",
                        confirmPin = "",
                        errorMessage = null
                    )
                }
            }

            else -> {
                navigateHome()
            }
        }
    }

    private fun onBiometricToggleChanged(
        enabled: Boolean
    ) {
        val current = _state.value

        if (enabled && !current.biometricAvailable) {
            _state.update {
                it.copy(
                    biometricEnabled = false,
                    errorMessage = "Biometric unlock is not available on this device."
                )
            }
            return
        }

        _state.update {
            it.copy(
                biometricEnabled = enabled,
                errorMessage = null
            )
        }
    }

    private fun savePin() {
        val current = _state.value

        when {
            current.pin.length != 4 -> {
                _state.update {
                    it.copy(errorMessage = "Enter a 4-digit PIN.")
                }
                return
            }

            current.confirmPin.length != 4 -> {
                _state.update {
                    it.copy(errorMessage = "Confirm your 4-digit PIN.")
                }
                return
            }

            current.pin != current.confirmPin -> {
                _state.update {
                    it.copy(errorMessage = "PIN and Confirm PIN do not match.")
                }
                return
            }
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            runCatching {
                securityUseCases.saveVaultPin(
                    pin = current.pin,
                    biometricEnabled = current.biometricEnabled && current.biometricAvailable
                )
            }.onSuccess {
                _state.update {
                    it.copy(
                        isSaving = false,
                        pin = "",
                        confirmPin = "",
                        showPinSetupScreen = false,
                        showFirstTimeDialog = false,
                        showUnlockScreen = false,
                        isVaultConfigured = true,
                        isVaultUnlocked = true,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Unable to save Vault PIN."
                    )
                }
            }
        }
    }

    private fun onDeleteVaultFileClicked(id: String) {
        val item = _state.value.vaultItems.firstOrNull { vaultItem ->
            vaultItem.id == id
        }

        if (item == null) {
            _state.update {
                it.copy(
                    errorMessage = "Vault file not found."
                )
            }
            return
        }

        _state.update {
            it.copy(
                showDeleteVaultConfirmDialog = true,
                deleteTargetFileId = id,
                deleteTargetFileName = item.title,
                errorMessage = null
            )
        }
    }

    private fun onDeleteVaultDialogDismissed() {
        _state.update {
            it.copy(
                showDeleteVaultConfirmDialog = false,
                deleteTargetFileId = null,
                deleteTargetFileName = ""
            )
        }
    }

    private fun onDeleteVaultDialogConfirmClicked() {
        val id = _state.value.deleteTargetFileId

        if (id.isNullOrBlank()) {
            _state.update {
                it.copy(
                    showDeleteVaultConfirmDialog = false,
                    errorMessage = "Delete request is not available."
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    showDeleteVaultConfirmDialog = false,
                    isDeletingVaultFile = true,
                    errorMessage = null
                )
            }

            runCatching {
                fileUseCases.deleteVaultFile(id)
            }.onSuccess {
                _state.update {
                    it.copy(
                        isDeletingVaultFile = false,
                        showDeleteVaultSuccessDialog = true,
                        deleteTargetFileId = null,
                        deleteTargetFileName = ""
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isDeletingVaultFile = false,
                        deleteTargetFileId = null,
                        errorMessage = throwable.message ?: "Unable to delete this Vault file."
                    )
                }
            }
        }
    }

    private fun onDeleteVaultSuccessDismissed() {
        _state.update {
            it.copy(
                showDeleteVaultSuccessDialog = false
            )
        }
    }

    private fun verifyPinAndUnlock() {
        val current = _state.value

        if (current.isPinLockedOut) {
            _state.update {
                it.copy(
                    unlockErrorMessage = "Too many wrong attempts. Try again in ${current.lockoutRemainingSeconds}s."
                )
            }
            return
        }

        if (current.unlockPin.length != 4) {
            _state.update {
                it.copy(
                    unlockErrorMessage = "Enter your 4-digit PIN."
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isVerifying = true,
                    unlockErrorMessage = null
                )
            }

            val isCorrect = runCatching {
                securityUseCases.verifyVaultPin(current.unlockPin)
            }.getOrDefault(false)

            if (isCorrect) {
                unlockVault()
            } else {
                handleWrongPin()
            }
        }
    }

    private fun handleWrongPin() {
        val current = _state.value
        val attempts = current.wrongPinAttempts + 1

        if (attempts >= MAX_WRONG_ATTEMPTS) {
            startPinLockout()
            return
        }

        _state.update {
            it.copy(
                isVerifying = false,
                unlockPin = "",
                wrongPinAttempts = attempts,
                showIncorrectPinDialog = true,
                unlockErrorMessage = null
            )
        }
    }

    private fun startPinLockout() {
        lockoutJob?.cancel()

        _state.update {
            it.copy(
                isVerifying = false,
                unlockPin = "",
                wrongPinAttempts = 0,
                showIncorrectPinDialog = false,
                lockoutRemainingSeconds = LOCKOUT_SECONDS,
                unlockErrorMessage = "Too many wrong attempts. Try again in ${LOCKOUT_SECONDS}s."
            )
        }

        lockoutJob = viewModelScope.launch {
            for (remaining in LOCKOUT_SECONDS downTo 1) {
                _state.update {
                    it.copy(
                        lockoutRemainingSeconds = remaining,
                        unlockErrorMessage = "Too many wrong attempts. Try again in ${remaining}s."
                    )
                }

                delay(1_000L)
            }

            _state.update {
                it.copy(
                    lockoutRemainingSeconds = 0,
                    unlockErrorMessage = null
                )
            }
        }
    }

    private fun unlockVault() {
        lockoutJob?.cancel()

        _state.update {
            it.copy(
                isVaultUnlocked = true,
                showUnlockScreen = false,
                showFirstTimeDialog = false,
                showPinSetupScreen = false,
                showIncorrectPinDialog = false,
                isVerifying = false,
                unlockPin = "",
                wrongPinAttempts = 0,
                lockoutRemainingSeconds = 0,
                unlockErrorMessage = null,
                errorMessage = null
            )
        }
    }

    private fun requestAutoBiometricIfPossible() {
        val current = _state.value

        if (
            current.canUseBiometricUnlock &&
            current.showUnlockScreen &&
            !autoBiometricPromptShown
        ) {
            autoBiometricPromptShown = true

            viewModelScope.launch {
                _navEvents.send(VaultNavEvents.LaunchBiometricUnlock)
            }
        }
    }


    private fun onRestoreVaultFileClicked(id: String) {
        val item = _state.value.vaultItems.firstOrNull { it.id == id }

        if (item == null) {
            _state.update {
                it.copy(errorMessage = "Vault file not found.")
            }
            return
        }

        _state.update {
            it.copy(
                showRestoreConfirmDialog = true,
                restoreTargetFileId = id,
                restoreTargetFileName = item.title,
                errorMessage = null
            )
        }
    }

    private fun onRestoreDialogDismissed() {
        _state.update {
            it.copy(
                showRestoreConfirmDialog = false,
                restoreTargetFileId = null,
                restoreTargetFileName = ""
            )
        }
    }

    private fun onRestoreDialogConfirmClicked() {
        val id = _state.value.restoreTargetFileId

        if (id.isNullOrBlank()) {
            _state.update {
                it.copy(
                    showRestoreConfirmDialog = false,
                    errorMessage = "Restore request is not available."
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    showRestoreConfirmDialog = false,
                    isRestoringFile = true,
                    errorMessage = null
                )
            }

            runCatching {
                fileUseCases.restoreVaultFile(id)
            }.onSuccess { outputUri ->
                _state.update {
                    it.copy(
                        isRestoringFile = false,
                        showRestoreSuccessDialog = true,
                        restoredOutputUri = outputUri,
                        restoreTargetFileId = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isRestoringFile = false,
                        restoreTargetFileId = null,
                        restoredOutputUri = null,
                        errorMessage = throwable.message ?: "Unable to restore this Vault file."
                    )
                }
            }
        }
    }

    private fun onRestoreSuccessDismissed() {
        _state.update {
            it.copy(
                showRestoreSuccessDialog = false,
                restoredOutputUri = null,
                restoreTargetFileName = ""
            )
        }
    }

    private fun requestManualBiometricUnlock() {
        val current = _state.value

        if (!current.biometricAvailable) {
            _state.update {
                it.copy(
                    unlockErrorMessage = "Biometric unlock is not available on this device."
                )
            }
            return
        }

        if (!current.biometricEnabled) {
            _state.update {
                it.copy(
                    unlockErrorMessage = "Biometric unlock is disabled."
                )
            }
            return
        }

        viewModelScope.launch {
            _navEvents.send(VaultNavEvents.LaunchBiometricUnlock)
        }
    }

    private fun lockVaultForPrivacy() {
        val current = _state.value

        if (!current.isVaultConfigured) return

        autoBiometricPromptShown = false

        _state.update {
            it.copy(
                isVaultUnlocked = false,
                showUnlockScreen = true,
                showIncorrectPinDialog = false,
                unlockPin = "",
                isVerifying = false,
                unlockErrorMessage = null
            )
        }
    }

    private fun navigateHome() {
        viewModelScope.launch {
            _navEvents.send(VaultNavEvents.NavigateHome)
        }
    }

    private fun onVaultItemClicked(id: String) {
        viewModelScope.launch {
            if (!_state.value.isVaultUnlocked) {
                _state.update {
                    it.copy(
                        errorMessage = "Unlock Vault first."
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isPreparingPlayback = true,
                    errorMessage = null
                )
            }

            runCatching {
                fileUseCases.prepareVaultFileForPlayback(id)
            }.onSuccess { mediaFile ->
                _navEvents.send(
                    VaultNavEvents.OpenVaultMedia(
                        mediaList = listOf(mediaFile),
                        startIndex = 0
                    )
                )
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        errorMessage = throwable.message ?: "Unable to open this Vault file."
                    )
                }
            }

            _state.update {
                it.copy(
                    isPreparingPlayback = false
                )
            }
        }
    }

    private fun VaultFile.toVaultUiItem(): VaultUiItem {
        return VaultUiItem(
            id = id,
            title = originalFileName.substringBeforeLast(
                delimiter = ".",
                missingDelimiterValue = originalFileName
            ),
            sizeText = originalSizeBytes.toReadableFileSize(),
            extension = originalFileName.substringAfterLast(
                delimiter = ".",
                missingDelimiterValue = when (fileType) {
                    VaultFileType.VIDEO -> "MP4"
                    VaultFileType.AUDIO -> "MP3"
                }
            ),
            mediaType = when (fileType) {
                VaultFileType.VIDEO -> VaultMediaType.VIDEO
                VaultFileType.AUDIO -> VaultMediaType.AUDIO
            },
            thumbnailUri = thumbnailPath
        )
    }

    private fun Long.toReadableFileSize(): String {
        if (this <= 0L) return "0 B"

        val kb = 1024.0
        val mb = kb * 1024.0
        val gb = mb * 1024.0

        return when {
            this >= gb -> String.format("%.1f GB", this / gb)
            this >= mb -> String.format("%.0f MB", this / mb)
            this >= kb -> String.format("%.0f KB", this / kb)
            else -> "$this B"
        }
    }

    private fun String.onlyFourDigits(): String {
        return filter { it.isDigit() }.take(4)
    }

    companion object {
        private const val MAX_WRONG_ATTEMPTS = 5
        private const val LOCKOUT_SECONDS = 30
    }
}