package com.core.ads.di

import com.core.ads.data.GoogleMobileAdsInitializer
import com.core.ads.data.appopen.GoogleAppOpenAdController
import com.core.ads.data.banner.GoogleBannerAdController
import com.core.ads.data.config.DefaultAdsConfigStore
import com.core.ads.data.consent.GoogleUmpConsentInitializer
import com.core.ads.data.consent.UmpConsentPolicy
import com.core.ads.data.display.DefaultFullScreenAdTransitionGuard
import com.core.ads.data.init.DefaultAdsInitializationManager
import com.core.ads.data.init.DefaultAdsSdkStateProvider
import com.core.ads.data.interstitial.DefaultInterstitialAdGate
import com.core.ads.data.interstitial.GoogleInterstitialAdController
import com.core.ads.data.manager.DefaultAdsManager
import com.core.ads.data.nativead.GoogleNativeAdController
import com.core.ads.data.policy.DefaultAdsPolicyEvaluator
import com.core.ads.data.remote.AdsRemoteConfigMapper
import com.core.ads.data.rewarded.GoogleRewardedAdController
import com.core.ads.data.runtime.DefaultAdsRuntimeCleaner
import com.core.ads.data.runtime.DefaultAdsRuntimeMonitor
import com.core.ads.domain.AdsInitializer
import com.core.ads.domain.analytics.AdsAnalyticsTracker
import com.core.ads.domain.analytics.DebugAdsAnalyticsTracker
import com.core.ads.domain.appopen.AppOpenAdController
import com.core.ads.domain.banner.BannerAdController
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.consent.ConsentInitializer
import com.core.ads.domain.display.AdsDisplayGuard
import com.core.ads.domain.display.DefaultAdsDisplayGuard
import com.core.ads.domain.display.FullScreenAdTransitionGuard
import com.core.ads.domain.init.AdsInitializationManager
import com.core.ads.domain.init.AdsSdkStateProvider
import com.core.ads.domain.init.MutableAdsSdkStateProvider
import com.core.ads.domain.interstitial.InterstitialAdController
import com.core.ads.domain.manager.AdsManager
import com.core.ads.domain.nativead.NativeAdController
import com.core.ads.domain.policy.AdsConsentPolicy
import com.core.ads.domain.policy.AdsPolicyEvaluator
import com.core.ads.domain.rewarded.RewardedAdController
import com.core.ads.domain.runtime.AdsRuntimeCleaner
import com.core.ads.domain.runtime.AdsRuntimeMonitor
import com.core.ads.lifecycle.AppOpenAdLifecycleObserver
import com.core.ads.lifecycle.AppOpenAdLifecyclePolicy
import com.core.ads.lifecycle.CurrentActivityProvider
import com.core.ads.lifecycle.DefaultAppOpenAdLifecyclePolicy
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import com.core.ads.data.loading.DefaultAdLoadingUiController
import com.core.ads.domain.interstitial.InterstitialAdGate
import com.core.ads.domain.loading.AdLoadingUiController

val adsModule = module {

    /**
     * Config store.
     *
     * App module updates this through AdsManager.updateConfig(...)
     * after Remote Config is fetched and mapped.
     */
    single<AdsConfigStore> {
        DefaultAdsConfigStore()
    }

    single<AdLoadingUiController> {
        DefaultAdLoadingUiController()
    }

    /**
     * Remote Config JSON mapper.
     *
     * Firebase itself stays in app module.
     */
    single {
        AdsRemoteConfigMapper()
    }

    /**
     * Lifecycle policy and current Activity tracker.
     */
    single<AppOpenAdLifecyclePolicy> {
        DefaultAppOpenAdLifecyclePolicy()
    }

    single<CurrentActivityProvider> {
        CurrentActivityProvider(
            lifecyclePolicy = get()
        )
    }

    /**
     * Consent.
     */
    single {
        UmpConsentPolicy(
            application = androidApplication()
        )
    }

    single<AdsConsentPolicy> {
        get<UmpConsentPolicy>()
    }

    single<ConsentInitializer> {
        GoogleUmpConsentInitializer(
            currentActivityProvider = get(),
            consentPolicy = get(),
            configStore = get()
        )
    }

    /**
     * SDK state.
     */
    single<MutableAdsSdkStateProvider> {
        DefaultAdsSdkStateProvider()
    }

    single<AdsSdkStateProvider> {
        get<MutableAdsSdkStateProvider>()
    }

    /**
     * Google Mobile Ads SDK initializer.
     */
    single<AdsInitializer> {
        GoogleMobileAdsInitializer(
            context = androidApplication(),
            configStore = get(),
            adsUserPolicy = get(),
            adsConsentPolicy = get(),
            sdkStateProvider = get()
        )
    }

    /**
     * Analytics.
     *
     * App module can override later with FirebaseAdsAnalyticsTracker.
     */
    single<AdsAnalyticsTracker> {
        DebugAdsAnalyticsTracker()
    }

    /**
     * Global full-screen display guard.
     */
    single<AdsDisplayGuard> {
        DefaultAdsDisplayGuard()
    }

    /**
     * Central policy evaluator.
     *
     * Handles:
     * - ads disabled
     * - requests disabled
     * - premium/remove-ads user
     * - consent
     * - SDK state
     * - display guard
     * - activity validity
     * - cooldown/cache/show safety
     */
    single<AdsPolicyEvaluator> {
        DefaultAdsPolicyEvaluator(
            configStore = get(),
            adsUserPolicy = get(),
            adsConsentPolicy = get(),
            adsSdkStateProvider = get(),
            adsDisplayGuard = get()
        )
    }

    /**
     * Controllers.
     */
    single<AppOpenAdController> {
        GoogleAppOpenAdController(
            context = androidApplication(),
            configStore = get(),
            policyEvaluator = get(),
            analyticsTracker = get(),
            adsDisplayGuard = get()
        )
    }

    single<InterstitialAdController> {
        GoogleInterstitialAdController(
            context = androidApplication(),
            configStore = get(),
            policyEvaluator = get(),
            analyticsTracker = get(),
            adsDisplayGuard = get(),
            get()
        )
    }

    single<InterstitialAdGate> {
        DefaultInterstitialAdGate(
            adsManager = get()
        )
    }

    single<RewardedAdController> {
        GoogleRewardedAdController(
            context = androidApplication(),
            configStore = get(),
            policyEvaluator = get(),
            analyticsTracker = get(),
            adsDisplayGuard = get(),
            get()
        )
    }

    single<BannerAdController> {
        GoogleBannerAdController(
            context = androidApplication(),
            configStore = get(),
            policyEvaluator = get(),
            analyticsTracker = get()
        )
    }

    single<NativeAdController> {
        GoogleNativeAdController(
            context = androidApplication(),
            configStore = get(),
            policyEvaluator = get(),
            analyticsTracker = get()
        )
    }

    /**
     * Runtime cleanup + monitor.
     */
    single<AdsRuntimeCleaner> {
        DefaultAdsRuntimeCleaner(
            appOpenAdController = get(),
            interstitialAdController = get(),
            rewardedAdController = get(),
            bannerAdController = get(),
            nativeAdController = get(),
            adsDisplayGuard = get()
        )
    }

    single<AdsRuntimeMonitor> {
        DefaultAdsRuntimeMonitor(
            configStore = get(),
            adsUserPolicy = get(),
            adsConsentPolicy = get(),
            adsSdkStateProvider = get(),
            adsRuntimeCleaner = get()
        )
    }

    /**
     * Initialization manager.
     */
    single<AdsInitializationManager> {
        DefaultAdsInitializationManager(
            consentInitializer = get(),
            adsInitializer = get(),
            appOpenAdController = get(),
            adsSdkStateProvider = get(),
            adsRuntimeMonitor = get()
        )
    }

    /**
     * AppOpen lifecycle observer.
     */
    factory {
        AppOpenAdLifecycleObserver(
            appOpenAdController = get(),
            configStore = get(),
            currentActivityProvider = get(),
            adsInitializationManager = get(),
            lifecyclePolicy = get(),
            get(),
            get(),
        )
    }

    /**
     * App-facing facade.
     */
    single<AdsManager> {
        DefaultAdsManager(
            configStore = get(),
            initializationManager = get(),
            sdkStateProvider = get(),
            runtimeCleaner = get(),
            appOpenAdController = get(),
            interstitialAdController = get(),
            rewardedAdController = get(),
            bannerAdController = get(),
            get(),
            get(),
        )
    }

    single<FullScreenAdTransitionGuard> {
        DefaultFullScreenAdTransitionGuard()
    }
}