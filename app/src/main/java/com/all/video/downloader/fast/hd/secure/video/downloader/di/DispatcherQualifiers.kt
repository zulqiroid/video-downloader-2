package com.all.video.downloader.fast.hd.secure.video.downloader.di

    import javax.inject.Qualifier

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IoDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainDispatcher