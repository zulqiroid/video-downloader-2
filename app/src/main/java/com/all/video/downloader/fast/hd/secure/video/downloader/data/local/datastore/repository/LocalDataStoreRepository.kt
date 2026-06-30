package com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository

import kotlinx.coroutines.flow.Flow

interface LocalDataStoreRepository {

    fun isOnBoardingCompleted(): Flow<Boolean>
    suspend fun setOnBoardingCompletedFlag()

    fun getSelectedLanguageCode(): Flow<String?>
    suspend fun setSelectedLanguageCode(code: String)

    fun isNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)

    fun getDownloadLocationType(): Flow<String?>
    suspend fun setDownloadLocationType(type: String)

    fun getDownloadLocationTreeUri(): Flow<String?>
    suspend fun setDownloadLocationTreeUri(uri: String?)

    fun isPremiumUser(): Flow<Boolean>
    fun hasLifetimePurchase(): Flow<Boolean>
    fun getPremiumProductId(): Flow<String?>

    suspend fun setPremiumEntitlement(
        isPremiumUser: Boolean,
        hasLifetimePurchase: Boolean,
        productId: String?
    )

}