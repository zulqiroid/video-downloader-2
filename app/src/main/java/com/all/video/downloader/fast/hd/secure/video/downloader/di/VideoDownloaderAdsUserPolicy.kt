package com.all.video.downloader.fast.hd.secure.video.downloader.di

import com.core.ads.domain.policy.AdsUserAdState
import com.core.ads.domain.policy.AdsUserPolicy
import kotlinx.coroutines.flow.StateFlow

class VideoDownloaderAdsUserPolicy : AdsUserPolicy {

    override val state: StateFlow<AdsUserAdState>
        get() = PremiumAdsEntitlementBridge.adsState
}