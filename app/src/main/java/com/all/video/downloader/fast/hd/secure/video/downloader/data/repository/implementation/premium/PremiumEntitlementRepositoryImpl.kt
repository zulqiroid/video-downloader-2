package com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.premium

import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.di.PremiumAdsEntitlementBridge
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumEntitlement
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumEntitlementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumEntitlementRepositoryImpl @Inject constructor(
    private val localDataStoreRepository: LocalDataStoreRepository
) : PremiumEntitlementRepository {

    override fun observeEntitlement(): Flow<PremiumEntitlement> {
        return combine(
            localDataStoreRepository.isPremiumUser(),
            localDataStoreRepository.hasLifetimePurchase(),
            localDataStoreRepository.getPremiumProductId()
        ) { isPremiumUser, hasLifetimePurchase, productId ->
            PremiumEntitlement(
                isPremiumUser = isPremiumUser,
                hasLifetimePurchase = hasLifetimePurchase,
                productId = productId
            )
        }.onEach { entitlement ->
            PremiumAdsEntitlementBridge.update(entitlement)
        }
    }

    override suspend fun setPremiumEntitlement(
        productId: String?,
        hasLifetimePurchase: Boolean
    ) {
        localDataStoreRepository.setPremiumEntitlement(
            isPremiumUser = true,
            hasLifetimePurchase = hasLifetimePurchase,
            productId = productId
        )

        PremiumAdsEntitlementBridge.update(
            PremiumEntitlement(
                isPremiumUser = true,
                hasLifetimePurchase = hasLifetimePurchase,
                productId = productId
            )
        )
    }

    override suspend fun clearPremiumEntitlement() {
        localDataStoreRepository.setPremiumEntitlement(
            isPremiumUser = false,
            hasLifetimePurchase = false,
            productId = null
        )

        PremiumAdsEntitlementBridge.update(PremiumEntitlement())
    }
}