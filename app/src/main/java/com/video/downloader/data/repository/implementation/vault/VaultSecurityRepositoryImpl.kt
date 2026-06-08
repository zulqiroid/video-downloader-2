package com.video.downloader.data.repository.implementation.vault

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.video.downloader.data.security.vault.VaultPinHashingService
import com.video.downloader.domain.repository.vault.VaultSecurityRepository
import com.video.downloader.utils.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultSecurityRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val hashingService: VaultPinHashingService
) : VaultSecurityRepository {

    override fun isVaultConfigured(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val hash = preferences[DataStoreKeys.VAULT_PIN_HASH]
            val salt = preferences[DataStoreKeys.VAULT_PIN_SALT]

            !hash.isNullOrBlank() && !salt.isNullOrBlank()
        }
    }

    override fun isBiometricEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.VAULT_BIOMETRIC_ENABLED] ?: false
        }
    }

    override suspend fun saveVaultPin(
        pin: String,
        biometricEnabled: Boolean
    ) {
        val salt = hashingService.generateSalt()

        val hash = hashingService.hashPin(
            pin = pin,
            salt = salt
        )

        dataStore.edit { preferences ->
            preferences[DataStoreKeys.VAULT_PIN_SALT] = salt
            preferences[DataStoreKeys.VAULT_PIN_HASH] = hash
            preferences[DataStoreKeys.VAULT_BIOMETRIC_ENABLED] = biometricEnabled
        }
    }

    override suspend fun verifyVaultPin(
        pin: String
    ): Boolean {
        val preferences = dataStore.data.first()

        val salt = preferences[DataStoreKeys.VAULT_PIN_SALT]
            ?: return false

        val hash = preferences[DataStoreKeys.VAULT_PIN_HASH]
            ?: return false

        return hashingService.matchesPin(
            pin = pin,
            salt = salt,
            expectedHash = hash
        )
    }
}