package com.all.video.downloader.fast.hd.secure.video.downloader.di

import com.core.ads.domain.policy.AdsUserPolicy
import org.koin.dsl.module

val appAdsModule = module {

    /**
     * App-side ads policy.
     *
     * Premium / remove-ads users:
     * - ads disabled
     * - ads runtime cleanup core:ads side handle karega
     */
    single<AdsUserPolicy> {
        VideoDownloaderAdsUserPolicy()
    }
}