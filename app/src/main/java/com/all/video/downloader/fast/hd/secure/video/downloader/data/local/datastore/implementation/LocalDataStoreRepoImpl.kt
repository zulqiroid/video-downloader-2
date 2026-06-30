package com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.utils.DataStoreKeys
 import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class LocalDataStoreRepoImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LocalDataStoreRepository {

    override fun isOnBoardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.ONBOARDING_COMPLETED] ?: false
        }
    }

    override suspend fun setOnBoardingCompletedFlag() {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.ONBOARDING_COMPLETED] = true
        }
    }

    override fun getSelectedLanguageCode(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.LANGUAGE_CODE]
        }
    }

    override suspend fun setSelectedLanguageCode(code: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.LANGUAGE_CODE] = code
        }
    }

    override fun isNotificationsEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.NOTIFICATIONS_ENABLED] ?: true
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    override fun getDownloadLocationType(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.DOWNLOAD_LOCATION_TYPE]
        }
    }

    override suspend fun setDownloadLocationType(type: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.DOWNLOAD_LOCATION_TYPE] = type
        }
    }

    override fun getDownloadLocationTreeUri(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.DOWNLOAD_LOCATION_TREE_URI]
        }
    }

    override suspend fun setDownloadLocationTreeUri(uri: String?) {
        dataStore.edit { preferences ->
            if (uri.isNullOrBlank()) {
                preferences.remove(DataStoreKeys.DOWNLOAD_LOCATION_TREE_URI)
            } else {
                preferences[DataStoreKeys.DOWNLOAD_LOCATION_TREE_URI] = uri
            }
        }
    }


    override fun isPremiumUser(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.IS_PREMIUM_USER] ?: false
        }
    }

    override fun hasLifetimePurchase(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.HAS_LIFETIME_PURCHASE] ?: false
        }
    }

    override fun getPremiumProductId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.PREMIUM_PRODUCT_ID]
        }
    }

    override suspend fun setPremiumEntitlement(
        isPremiumUser: Boolean,
        hasLifetimePurchase: Boolean,
        productId: String?
    ) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.IS_PREMIUM_USER] = isPremiumUser
            preferences[DataStoreKeys.HAS_LIFETIME_PURCHASE] = hasLifetimePurchase

            if (productId.isNullOrBlank()) {
                preferences.remove(DataStoreKeys.PREMIUM_PRODUCT_ID)
            } else {
                preferences[DataStoreKeys.PREMIUM_PRODUCT_ID] = productId
            }
        }
    }
}