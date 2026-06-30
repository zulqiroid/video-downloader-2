package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.vault

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.vault.VaultSecurityRepository
import javax.inject.Inject

data class VaultSecurityUseCases @Inject constructor(
    val observeVaultConfigured: ObserveVaultConfiguredUseCase,
    val observeBiometricEnabled: ObserveVaultBiometricEnabledUseCase,
    val saveVaultPin: SaveVaultPinUseCase,
    val verifyVaultPin: VerifyVaultPinUseCase
)

class ObserveVaultConfiguredUseCase @Inject constructor(
    private val repository: VaultSecurityRepository
) {
    operator fun invoke() = repository.isVaultConfigured()
}

class ObserveVaultBiometricEnabledUseCase @Inject constructor(
    private val repository: VaultSecurityRepository
) {
    operator fun invoke() = repository.isBiometricEnabled()
}

class SaveVaultPinUseCase @Inject constructor(
    private val repository: VaultSecurityRepository
) {
    suspend operator fun invoke(
        pin: String,
        biometricEnabled: Boolean
    ) {
        require(pin.length == 4 && pin.all { it.isDigit() }) {
            "PIN must be exactly 4 digits."
        }

        repository.saveVaultPin(
            pin = pin,
            biometricEnabled = biometricEnabled
        )
    }
}

class VerifyVaultPinUseCase @Inject constructor(
    private val repository: VaultSecurityRepository
) {
    suspend operator fun invoke(
        pin: String
    ): Boolean {
        if (pin.length != 4 || pin.any { !it.isDigit() }) {
            return false
        }

        return repository.verifyVaultPin(pin)
    }
}