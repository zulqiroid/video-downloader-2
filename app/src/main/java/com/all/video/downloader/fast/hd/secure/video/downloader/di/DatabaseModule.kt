package com.all.video.downloader.fast.hd.secure.video.downloader.di

import android.content.Context
import androidx.room.Room
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.AppDatabase
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download.DownloadProgressDao
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.vault.VaultFileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideVaultFileDao(
        database: AppDatabase
    ): VaultFileDao {
        return database.vaultFileDao()
    }

    @Provides
    @Singleton
    fun provideDownloadProgressDao(
        database: AppDatabase
    ): DownloadProgressDao {
        return database.downloadProgressDao()
    }

    private const val DATABASE_NAME = "video_downloader_database"
}