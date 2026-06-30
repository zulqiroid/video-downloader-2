package com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download.DownloadProgressDao
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download.DownloadProgressEntity
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.vault.VaultFileDao
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.vault.VaultFileEntity

@Database(
    entities = [
        VaultFileEntity::class,
        DownloadProgressEntity::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vaultFileDao(): VaultFileDao

    abstract fun downloadProgressDao(): DownloadProgressDao
}