package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.vault

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.vault.VaultFileRepository
import javax.inject.Inject

data class VaultFileUseCases @Inject constructor(
    val observeVaultFiles: ObserveVaultFilesUseCase,
    val addFileToVault: AddFileToVaultUseCase,
    val prepareVaultFileForPlayback: PrepareVaultFileForPlaybackUseCase,
    val restoreVaultFile: RestoreVaultFileUseCase,
    val markOriginalFileDeleted: MarkOriginalFileDeletedUseCase,
    val markOriginalFileDeleteFailed: MarkOriginalFileDeleteFailedUseCase,
    val deleteVaultFile: DeleteVaultFileUseCase
)

class ObserveVaultFilesUseCase @Inject constructor(
    private val repository: VaultFileRepository
) {
    operator fun invoke() = repository.observeVaultFiles()
}

class AddFileToVaultUseCase @Inject constructor(
    private val repository: VaultFileRepository
) {
    suspend operator fun invoke(sourceUri: String) =
        repository.addFileToVault(sourceUri)
}

class PrepareVaultFileForPlaybackUseCase @Inject constructor(
    private val repository: VaultFileRepository
) {
    suspend operator fun invoke(id: String) =
        repository.prepareVaultFileForPlayback(id)
}

class RestoreVaultFileUseCase @Inject constructor(
    private val repository: VaultFileRepository
) {
    suspend operator fun invoke(id: String) =
        repository.restoreVaultFile(id)
}

class MarkOriginalFileDeletedUseCase @Inject constructor(
    private val repository: VaultFileRepository
) {
    suspend operator fun invoke(id: String) {
        repository.markOriginalFileDeleted(id)
    }
}

class MarkOriginalFileDeleteFailedUseCase @Inject constructor(
    private val repository: VaultFileRepository
) {
    suspend operator fun invoke(id: String) {
        repository.markOriginalFileDeleteFailed(id)
    }
}

class DeleteVaultFileUseCase @Inject constructor(
    private val repository: VaultFileRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteVaultFile(id)
    }
}