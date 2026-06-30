package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.bootstrap

import android.app.Application
import com.core.ads.domain.manager.AdsManager
import com.core.ads.lifecycle.AppOpenAdLifecycleObserver
import com.core.ads.lifecycle.CurrentActivityProvider
import com.all.video.downloader.fast.hd.secure.video.downloader.di.MainDispatcher
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.VideoDownloaderRemoteConfigSnapshot
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigBootstrapper @Inject constructor(
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) {

    private val started = AtomicBoolean(false)
    private val lifecycleCallbacksRegistered = AtomicBoolean(false)

    private val appScope = CoroutineScope(
        SupervisorJob() + mainDispatcher
    )

    fun start(application: Application) {
        if (!started.compareAndSet(false, true)) {
            Timber.d("RemoteConfigBootstrapper already started. Skipping.")
            return
        }

        val adsDependencies = resolveAdsDependencies()

        registerAdsLifecycleCallbacks(
            application = application,
            dependencies = adsDependencies
        )

        appScope.launch {
            bootstrapRemoteConfigAndAds(
                adsManager = adsDependencies.adsManager
            )
        }
    }

    private suspend fun bootstrapRemoteConfigAndAds(
        adsManager: AdsManager
    ) {
        Timber.d("Remote config bootstrap started.")

        /**
         * Step 1:
         * Read currently activated/cached Firebase config.
         *
         * This also applies local defaults internally if Firebase has no value yet.
         */
        val cachedSnapshot = remoteConfigRepository.initialize()
        applyAdsConfig(
            adsManager = adsManager,
            snapshot = cachedSnapshot,
            stage = "cached"
        )

        /**
         * Step 2:
         * Fetch latest config from Firebase and activate it.
         *
         * If fetch fails, repository returns safe fallback snapshot.
         */
        val remoteSnapshot = remoteConfigRepository.fetchAndActivate()
        applyAdsConfig(
            adsManager = adsManager,
            snapshot = remoteSnapshot,
            stage = "remote"
        )

        /**
         * Step 3:
         * Start ads pipeline after final available config is applied.
         *
         * Important:
         * We intentionally initialize after applying Remote Config.
         * Agar pehle disabled fallback ke sath initialize kar dein to ads SDK
         * blocked state mein ja sakta hai aur same process mein restart nahi hota.
         */
        adsManager.initialize()

        Timber.d(
            "Remote config bootstrap completed. source=%s, hasError=%s, warnings=%s",
            remoteSnapshot.source,
            remoteSnapshot.hasError,
            remoteSnapshot.warnings.joinToString()
        )
    }

    private fun applyAdsConfig(
        adsManager: AdsManager,
        snapshot: VideoDownloaderRemoteConfigSnapshot,
        stage: String
    ) {
        adsManager.updateConfig(snapshot.adsConfig)

        Timber.d(
            "Ads config applied from %s. source=%s, adsEnabled=%s, canRequestAds=%s, appOpenEnabled=%s",
            stage,
            snapshot.source,
            snapshot.adsConfig.adsEnabled,
            snapshot.adsConfig.canRequestAds,
            snapshot.adsConfig.appOpenAdConfig.enabled
        )

        if (snapshot.warnings.isNotEmpty()) {
            Timber.w(
                "Remote config warnings from %s: %s",
                stage,
                snapshot.warnings.joinToString()
            )
        }

        snapshot.errorMessage?.let { error ->
            Timber.w("Remote config %s error: %s", stage, error)
        }
    }

    private fun registerAdsLifecycleCallbacks(
        application: Application,
        dependencies: AdsDependencies
    ) {
        if (!lifecycleCallbacksRegistered.compareAndSet(false, true)) {
            return
        }

        application.registerActivityLifecycleCallbacks(
            dependencies.currentActivityProvider
        )

        application.registerActivityLifecycleCallbacks(
            dependencies.appOpenAdLifecycleObserver
        )

        Timber.d("Ads lifecycle callbacks registered.")
    }

    private fun resolveAdsDependencies(): AdsDependencies {
        val koin = GlobalContext.get()

        return AdsDependencies(
            adsManager = koin.get(),
            currentActivityProvider = koin.get(),
            appOpenAdLifecycleObserver = koin.get()
        )
    }

    private data class AdsDependencies(
        val adsManager: AdsManager,
        val currentActivityProvider: CurrentActivityProvider,
        val appOpenAdLifecycleObserver: AppOpenAdLifecycleObserver
    )
}