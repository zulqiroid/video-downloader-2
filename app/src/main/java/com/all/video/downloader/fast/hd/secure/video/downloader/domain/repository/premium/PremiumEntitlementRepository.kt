package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumEntitlement
import kotlinx.coroutines.flow.Flow

interface PremiumEntitlementRepository {

    fun observeEntitlement(): Flow<PremiumEntitlement>

    suspend fun setPremiumEntitlement(
        productId: String?,
        hasLifetimePurchase: Boolean
    )

    suspend fun clearPremiumEntitlement()
}