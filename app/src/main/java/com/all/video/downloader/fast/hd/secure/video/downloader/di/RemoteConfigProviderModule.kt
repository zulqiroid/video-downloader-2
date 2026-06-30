package com.all.video.downloader.fast.hd.secure.video.downloader.di

import com.core.ads.data.remote.AdsRemoteConfigMapper
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.repository.FirebaseVideoDownloaderRemoteConfigRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.mapper.AppRemoteConfigMapper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigProviderModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return FirebaseRemoteConfig.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppRemoteConfigMapper(): AppRemoteConfigMapper {
        return AppRemoteConfigMapper()
    }

    /**
     * Stateless mapper from core:ads.
     *
     * Note:
     * core:ads also exposes this in Koin, but this Hilt instance is only used
     * for parsing the Firebase JSON inside app module repository.
     */
    @Provides
    @Singleton
    fun provideAdsRemoteConfigMapper(): AdsRemoteConfigMapper {
        return AdsRemoteConfigMapper()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteConfigBindingModule {

    @Binds
    @Singleton
    abstract fun bindVideoDownloaderRemoteConfigRepository(
        implementation: FirebaseVideoDownloaderRemoteConfigRepository
    ): VideoDownloaderRemoteConfigRepository
}