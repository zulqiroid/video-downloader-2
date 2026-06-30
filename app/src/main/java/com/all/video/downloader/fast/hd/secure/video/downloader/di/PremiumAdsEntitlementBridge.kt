package com.all.video.downloader.fast.hd.secure.video.downloader.di

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumEntitlement
import com.core.ads.domain.policy.AdsUserAdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PremiumAdsEntitlementBridge {

    private val mutableAdsState = MutableStateFlow(AdsUserAdState())

    val adsState: StateFlow<AdsUserAdState> = mutableAdsState.asStateFlow()

    fun update(entitlement: PremiumEntitlement) {
        val premiumActive = entitlement.isPremiumUser || entitlement.hasLifetimePurchase

        mutableAdsState.value = AdsUserAdState(
            canShowAds = !premiumActive,
            isPremiumUser = entitlement.isPremiumUser,
            hasRemoveAdsPurchase = entitlement.hasLifetimePurchase,
            reason = if (premiumActive) {
                "Premium entitlement active"
            } else {
                "Free user"
            }
        )
    }
}