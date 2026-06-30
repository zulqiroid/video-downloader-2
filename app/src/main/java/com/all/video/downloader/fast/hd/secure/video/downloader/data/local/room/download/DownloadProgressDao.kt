package com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadProgressDao {

    @Query(
        """
        SELECT * FROM download_progress
        ORDER BY id DESC
        """
    )
    fun observeDownloads(): Flow<List<DownloadProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DownloadProgressEntity)

    @Query(
        """
        DELETE FROM download_progress
        WHERE id = :id
        """
    )
    suspend fun remove(id: Long)

    @Query(
        """
        SELECT * FROM download_progress
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun get(id: Long): DownloadProgressEntity?

    @Query(
        """
        SELECT * FROM download_progress
        ORDER BY id DESC
        """
    )
    suspend fun getAll(): List<DownloadProgressEntity>

    @Query("DELETE FROM download_progress")
    suspend fun clear()
}