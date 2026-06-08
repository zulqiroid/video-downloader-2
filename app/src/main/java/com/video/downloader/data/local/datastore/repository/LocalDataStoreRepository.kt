package com.video.downloader.data.local.datastore.repository

import kotlinx.coroutines.flow.Flow

interface LocalDataStoreRepository {

    fun isOnBoardingCompleted(): Flow<Boolean>
    suspend fun setOnBoardingCompletedFlag()

    fun getSelectedLanguageCode(): Flow<String?>
    suspend fun setSelectedLanguageCode(code: String)

}