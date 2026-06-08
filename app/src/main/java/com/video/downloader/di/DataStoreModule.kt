package com.video.downloader.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.video.downloader.data.local.datastore.instance.datastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent:: class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStoreInstance(
        @ApplicationContext context: Context
    ): DataStore<Preferences>{
        return context.datastore
    }
}