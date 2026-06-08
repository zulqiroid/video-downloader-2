package com.video.downloader.data.local.room.vault

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultFileDao {

    @Query(
        """
        SELECT * FROM vault_files
        ORDER BY createdAtMillis DESC
        """
    )
    fun observeVaultFiles(): Flow<List<VaultFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVaultFile(entity: VaultFileEntity)

    @Query(
        """
        SELECT * FROM vault_files
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getVaultFileById(id: String): VaultFileEntity?

    @Query(
        """
        UPDATE vault_files
        SET originalDeleteStatus = :status
        WHERE id = :id
        """
    )
    suspend fun updateOriginalDeleteStatus(
        id: String,
        status: String
    )

    @Query(
        """
        DELETE FROM vault_files
        WHERE id = :id
        """
    )
    suspend fun deleteVaultFileById(id: String)
}