package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium

import android.app.Activity
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumProduct
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppPremiumRemoteConfig

interface PremiumBillingRepository {

    suspend fun loadProducts(
        config: AppPremiumRemoteConfig
    ): List<PremiumProduct>

    suspend fun launchPurchase(
        activity: Activity,
        productId: String
    ): PremiumBillingResult

    suspend fun restorePurchases(): PremiumBillingResult
}

sealed interface PremiumBillingResult {
    data object Success : PremiumBillingResult
    data object FlowStarted : PremiumBillingResult
    data class Failure(val message: String) : PremiumBillingResult
}