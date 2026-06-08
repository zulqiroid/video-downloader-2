package com.video.downloader.domain.repository.firstOpen

import kotlinx.coroutines.flow.Flow

interface FirstOpenRepository {

    fun isOnBoardingCompleted(): Flow<Boolean>

    suspend fun setOnBoardingCompletedFlag()

}