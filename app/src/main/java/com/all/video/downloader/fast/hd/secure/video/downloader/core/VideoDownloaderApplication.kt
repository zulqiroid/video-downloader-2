package com.all.video.downloader.fast.hd.secure.video.downloader.core

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.core.ads.di.adsModule
import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.di.appAdsModule
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.bootstrap.RemoteConfigBootstrapper
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber
import javax.inject.Inject
import com.all.video.downloader.fast.hd.secure.video.downloader.premium.PremiumEntitlementBootstrapper

@HiltAndroidApp
class VideoDownloaderApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var premiumEntitlementBootstrapper: PremiumEntitlementBootstrapper

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var remoteConfigBootstrapper: RemoteConfigBootstrapper

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@VideoDownloaderApplication)
            modules(
                appAdsModule,
                adsModule
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        premiumEntitlementBootstrapper.start()

        remoteConfigBootstrapper.start(this)
    }
}