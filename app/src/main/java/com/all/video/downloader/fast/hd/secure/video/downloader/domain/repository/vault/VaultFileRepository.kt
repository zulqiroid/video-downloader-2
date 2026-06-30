package com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.vault

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.vault.VaultFile
import kotlinx.coroutines.flow.Flow

interface VaultFileRepository {

    fun observeVaultFiles(): Flow<List<VaultFile>>

    suspend fun addFileToVault(sourceUri: String): VaultFile

    suspend fun prepareVaultFileForPlayback(id: String): MediaFile

    suspend fun restoreVaultFile(id: String): String

    suspend fun markOriginalFileDeleted(id: String)

    suspend fun markOriginalFileDeleteFailed(id: String)

    suspend fun deleteVaultFile(id: String)
}