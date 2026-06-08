package com.video.downloader.di

import android.content.Context
import androidx.room.Room
import com.video.downloader.data.local.room.AppDatabase
import com.video.downloader.data.local.room.vault.VaultFileDao
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
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideVaultFileDao(
        database: AppDatabase
    ): VaultFileDao {
        return database.vaultFileDao()
    }

    private const val DATABASE_NAME = "video_downloader_database"
}