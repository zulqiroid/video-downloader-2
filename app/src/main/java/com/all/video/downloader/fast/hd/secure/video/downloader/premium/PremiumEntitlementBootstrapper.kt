package com.all.video.downloader.fast.hd.secure.video.downloader.premium

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumEntitlementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumEntitlementBootstrapper @Inject constructor(
    private val premiumEntitlementRepository: PremiumEntitlementRepository
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        scope.launch {
            premiumEntitlementRepository
                .observeEntitlement()
                .collect {
                    /**
                     * onEach inside repository updates PremiumAdsEntitlementBridge.
                     */
                }
        }
    }
}