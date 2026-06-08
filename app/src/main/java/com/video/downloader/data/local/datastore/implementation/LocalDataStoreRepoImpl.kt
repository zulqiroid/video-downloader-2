package com.video.downloader.data.local.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.video.downloader.utils.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataStoreRepoImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): LocalDataStoreRepository {
    override fun isOnBoardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.ONBOARDING_COMPLETED] ?: false
        }
    }

    override suspend fun setOnBoardingCompletedFlag() {
        dataStore.edit {preferences ->
            preferences[DataStoreKeys.ONBOARDING_COMPLETED] = true
        }
    }

    override fun getSelectedLanguageCode(): Flow<String?> {
        return dataStore.data.map {
            it[DataStoreKeys.LANGUAGE_CODE]
        }
    }

    override suspend fun setSelectedLanguageCode(code: String) {
        dataStore.edit {
            it[DataStoreKeys.LANGUAGE_CODE] = code
        }
    }


}