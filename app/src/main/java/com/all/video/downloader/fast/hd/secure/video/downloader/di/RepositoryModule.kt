package com.all.video.downloader.fast.hd.secure.video.downloader.di

import com.all.video.downloader.fast.hd.secure.video.downloader.data.connectivity.AndroidNetworkMonitor
import com.all.video.downloader.fast.hd.secure.video.downloader.data.encoder.audio.LameMp3Encoder
import com.all.video.downloader.fast.hd.secure.video.downloader.data.encoder.audio.NativeLameMp3Encoder
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.implementation.LocalDataStoreRepoImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.MediaRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.RoomDownloadProgressStore
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.VideoDownloadRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.download.DownloaderRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.firstOpen.FirstOpenRepoImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.reels.ReelsRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.vault.VaultFileRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.vault.VaultSecurityRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.videoSplitter.VideoSplitterRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.videoToMp3.VideoToMp3RepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.connectivity.NetworkMonitor
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.DownloadProgressStore
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.MediaRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.VideoDownloadRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.download.DownloaderRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.firstOpen.FirstOpenRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.reels.ReelsRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.vault.VaultFileRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.vault.VaultSecurityRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.videoSplitter.VideoSplitterRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.videoToMp3.VideoToMp3Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation.premium.PremiumEntitlementRepositoryImpl
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumEntitlementRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.data.billing.GooglePlayPremiumBillingRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumBillingRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun  bindLocalDataStoreRepository(
        implementation : LocalDataStoreRepoImpl
    ): LocalDataStoreRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        implementation: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindDownloadProgressRepo(
        implementation: RoomDownloadProgressStore
    ): DownloadProgressStore

    @Binds
    @Singleton
    abstract fun bindDownloadRepo(
        implementation: VideoDownloadRepositoryImpl
    ): VideoDownloadRepository


    @Binds
    @Singleton
    abstract fun bindFirstOpenRepo(
        implementation: FirstOpenRepoImpl
    ): FirstOpenRepository

    @Binds
    @Singleton
    abstract fun bindVideoToMp3Repository(
        implementation: VideoToMp3RepositoryImpl
    ): VideoToMp3Repository

    @Binds
    @Singleton
    abstract fun bindLameMp3Encoder(
        implementation: NativeLameMp3Encoder
    ): LameMp3Encoder

    @Binds
    @Singleton
    abstract fun bindVaultSecurityRepository(
        implementation: VaultSecurityRepositoryImpl
    ): VaultSecurityRepository

    @Binds
    @Singleton
    abstract fun bindVaultFileRepository(
        implementation: VaultFileRepositoryImpl
    ): VaultFileRepository

    @Binds
    @Singleton
    abstract fun bindVideoSplitterRepository(
        implementation: VideoSplitterRepositoryImpl
    ): VideoSplitterRepository


    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        implementation: AndroidNetworkMonitor
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindDownloaderRepository(
        implementation: DownloaderRepositoryImpl
    ): DownloaderRepository

    @Binds
    @Singleton
    abstract fun bindReelsRepository(
        implementation: ReelsRepositoryImpl
    ): ReelsRepository

    @Binds
    @Singleton
    abstract fun bindPremiumEntitlementRepository(
        implementation: PremiumEntitlementRepositoryImpl
    ): PremiumEntitlementRepository

    @Binds
    @Singleton
    abstract fun bindPremiumBillingRepository(
        implementation: GooglePlayPremiumBillingRepository
    ): PremiumBillingRepository
}