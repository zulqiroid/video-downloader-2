package com.video.downloader.data.repository.implementation.firstOpen

import com.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.video.downloader.domain.repository.firstOpen.FirstOpenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirstOpenRepoImpl @Inject constructor(
    private val datastore: LocalDataStoreRepository
): FirstOpenRepository {
    override fun isOnBoardingCompleted(): Flow<Boolean> {
        return datastore.isOnBoardingCompleted()
    }

    override suspend fun setOnBoardingCompletedFlag() {
        datastore.setOnBoardingCompletedFlag()
    }
}