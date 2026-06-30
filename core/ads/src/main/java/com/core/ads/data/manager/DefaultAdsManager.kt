package com.core.ads.data.manager

import android.app.Activity
import com.core.ads.domain.AdLoadingDialogConfig
import com.core.ads.domain.AdsCoreConfig
import com.core.ads.domain.appopen.AppOpenAdController
import com.core.ads.domain.appopen.AppOpenAdShowResult
import com.core.ads.domain.appopen.AppOpenAdState
import com.core.ads.domain.banner.BannerAdController
import com.core.ads.domain.banner.BannerAdState
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.init.AdsInitializationManager
import com.core.ads.domain.init.AdsInitializationState
import com.core.ads.domain.init.AdsSdkState
import com.core.ads.domain.init.AdsSdkStateProvider
import com.core.ads.domain.interstitial.InterstitialAdController
import com.core.ads.domain.interstitial.InterstitialAdShowResult
import com.core.ads.domain.interstitial.InterstitialAdState
import com.core.ads.domain.loading.AdLoadingUiState
import com.core.ads.domain.manager.AdsManager
import com.core.ads.domain.nativead.NativeAdController
import com.core.ads.domain.nativead.NativeAdState
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.rewarded.RewardItem
import com.core.ads.domain.rewarded.RewardedAdController
import com.core.ads.domain.rewarded.RewardedAdShowResult
import com.core.ads.domain.rewarded.RewardedAdState
import com.core.ads.domain.runtime.AdsCleanupReason
import com.core.ads.domain.runtime.AdsRuntimeCleaner
import com.core.ads.utils.AdsLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.core.ads.domain.loading.AdLoadingUiController
import kotlin.time.Duration.Companion.milliseconds

class DefaultAdsManager(
    private val configStore: AdsConfigStore,
    private val initializationManager: AdsInitializationManager,
    private val sdkStateProvider: AdsSdkStateProvider,
    private val runtimeCleaner: AdsRuntimeCleaner,
    private val appOpenAdController: AppOpenAdController,
    private val interstitialAdController: InterstitialAdController,
    private val rewardedAdController: RewardedAdController,
    private val bannerAdController: BannerAdController,
    private val nativeAdController: NativeAdController,
    private val adLoadingUiController: AdLoadingUiController
) : AdsManager {

    private val appScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    override val configState: StateFlow<AdsCoreConfig>
        get() = configStore.state

    override val currentConfig: AdsCoreConfig
        get() = configStore.current

    override val adLoadingUiState: StateFlow<AdLoadingUiState>
        get() = adLoadingUiController.state

    override val initializationState: StateFlow<AdsInitializationState>
        get() = initializationManager.state

    override val sdkState: StateFlow<AdsSdkState>
        get() = sdkStateProvider.state

    override val appOpenState: StateFlow<AppOpenAdState>
        get() = appOpenAdController.state

    override val interstitialStates: StateFlow<Map<String, InterstitialAdState>>
        get() = interstitialAdController.states

    override val rewardedStates: StateFlow<Map<String, RewardedAdState>>
        get() = rewardedAdController.states

    override val bannerStates: StateFlow<Map<String, BannerAdState>>
        get() = bannerAdController.states

    override val nativeStates: StateFlow<Map<String, NativeAdState>>
        get() = nativeAdController.states

    override fun initialize() {
        AdsLogger.i("AdsManager initialize()")
        initializationManager.start()
    }

    override fun updateConfig(config: AdsCoreConfig) {
        AdsLogger.i(
            "AdsManager updateConfig(): " +
                    "adsEnabled=${config.adsEnabled}, " +
                    "canRequestAds=${config.canRequestAds}, " +
                    "isDebug=${config.isDebug}"
        )

        configStore.update(config)
    }

    override fun preloadAppOpen() {
        appOpenAdController.preload()
    }

    override fun preloadSplashAppOpen() {
        appOpenAdController.preloadForSplash()
    }

    override fun showAppOpenIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit
    ) {
        appScope.launch {
            showAppOpenLoadingIfAdIsReady()

            appOpenAdController.showIfAvailable(
                activity = activity,
                onComplete = onComplete
            )
        }
    }
    override fun showSplashAppOpenIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit
    ) {
        appScope.launch {
            showAppOpenLoadingIfAdIsReady()

            appOpenAdController.showSplashIfAvailable(
                activity = activity,
                onComplete = onComplete
            )
        }
    }

    override fun preloadInterstitial(
        placement: AdPlacement
    ) {
        interstitialAdController.preload(
            placement = placement
        )
    }

    override fun showInterstitialIfAvailable(
        activity: Activity,
        placement: AdPlacement,
        onComplete: (InterstitialAdShowResult) -> Unit
    ) {
        appScope.launch {
            showInterstitialLoadingIfAdIsReady(
                placement = placement
            )

            interstitialAdController.showIfAvailable(
                activity = activity,
                placement = placement,
                onComplete = onComplete
            )
        }
    }
    override fun preloadRewarded(
        placement: AdPlacement
    ) {
        rewardedAdController.preload(
            placement = placement
        )
    }

    override fun showRewardedIfAvailable(
        activity: Activity,
        placement: AdPlacement,
        onComplete: (RewardedAdShowResult) -> Unit,
        onRewardEarned: (RewardItem) -> Unit
    ) {
        appScope.launch {
            showAdLoadingIfNeeded(
                uiState = currentConfig.adLoadingDialogConfig.rewardedUiStateOrNull()
            )

            rewardedAdController.showIfAvailable(
                activity = activity,
                placement = placement,
                onComplete = onComplete,
                onRewardEarned = onRewardEarned
            )
        }
    }

    override fun clearAllAds(
        reason: AdsCleanupReason,
        message: String?
    ) {
        runtimeCleaner.clearAll(
            reason = reason,
            message = message
        )
    }

    override fun clearAll() {
        appOpenAdController.clear()
        interstitialAdController.clearAll()
        rewardedAdController.clearAll()
        bannerAdController.destroyAll()
        nativeAdController.clearAll()
    }

    override fun clearAllAdsForPremiumUser(
        message: String
    ) {
        runtimeCleaner.clearAll(
            reason = AdsCleanupReason.USER_BECAME_PREMIUM,
            message = message
        )
    }

    private suspend fun showAppOpenLoadingIfAdIsReady() {
        if (!appOpenAdController.state.value.isAvailable) return

        showAdLoadingIfNeeded(
            uiState = currentConfig.adLoadingDialogConfig.appOpenUiStateOrNull()
        )
    }

    private suspend fun showAdLoadingIfNeeded(
        uiState: AdLoadingUiState?
    ) {
        if (uiState == null) return

        adLoadingUiController.show(uiState)
        delay(uiState.durationMs.milliseconds)
        adLoadingUiController.hide()
    }

    private suspend fun showInterstitialLoadingIfAdIsReady(
        placement: AdPlacement
    ) {
        if (!interstitialAdController.getState(placement).isAvailable) return

        showAdLoadingIfNeeded(
            uiState = currentConfig.adLoadingDialogConfig.interstitialUiStateOrNull()
        )
    }

}