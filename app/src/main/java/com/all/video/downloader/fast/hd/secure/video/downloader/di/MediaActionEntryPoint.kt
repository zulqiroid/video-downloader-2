package com.all.video.downloader.fast.hd.secure.video.downloader.di

import android.content.Context
import com.all.video.downloader.fast.hd.secure.video.downloader.data.mediaActions.MediaFileActionService
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MediaActionEntryPoint {
    fun mediaFileActionService(): MediaFileActionService
}

object EntryPointAccessors {

    fun mediaFileActionService(
        context: Context
    ): MediaFileActionService {
        return EntryPoints.get(
            context.applicationContext,
            MediaActionEntryPoint::class.java
        ).mediaFileActionService()
    }
}