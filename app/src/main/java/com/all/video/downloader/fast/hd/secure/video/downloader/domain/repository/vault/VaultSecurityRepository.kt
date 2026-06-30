package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.vault

import kotlinx.coroutines.flow.Flow

interface VaultSecurityRepository {

    fun isVaultConfigured(): Flow<Boolean>

    fun isBiometricEnabled(): Flow<Boolean>

    suspend fun saveVaultPin(
        pin: String,
        biometricEnabled: Boolean
    )

    suspend fun verifyVaultPin(
        pin: String
    ): Boolean
}