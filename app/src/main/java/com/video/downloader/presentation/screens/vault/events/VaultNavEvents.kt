package com.video.downloader.presentation.screens.vault.events

import com.video.downloader.domain.models.MediaFile

sealed interface VaultNavEvents {

    data object NavigateHome : VaultNavEvents

    data object LaunchBiometricUnlock : VaultNavEvents

    data object LaunchVaultFilePicker : VaultNavEvents

    data class RequestOriginalFileDelete(
        val vaultFileId: String,
        val originalUri: String
    ) : VaultNavEvents

    data class OpenVaultMedia(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : VaultNavEvents
}