package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.data.mediaActions.MediaFileActionService
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.mediaActions.MediaFileActionResult
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumEntitlementRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainUiEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MoreFileDetailsDialogueEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.BottomNavItems
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.MainStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.PendingMediaStoreAction
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaFileActionService: MediaFileActionService,
    private val premiumEntitlementRepository: PremiumEntitlementRepository,
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MainNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private val _uiEffects = MutableSharedFlow<MainUiEffect>(
        extraBufferCapacity = 1
    )
    val uiEffects = _uiEffects.asSharedFlow()


    init {
        observePremiumLabelState()
    }

    fun onEvent(event: MainEvents) {
        when (event) {
            is MainEvents.OnTabSelected -> {
                _state.value = _state.value.copy(
                    selectedTab = event.tab
                )
            }

            is MainEvents.OnMoreClicked -> {
                _state.update {
                    it.copy(
                        showPlayerDialogue = true,
                        playerMediaItem = event.mediaFile
                    )
                }
            }

            is MainEvents.OnRenameMediaRequested -> {
                openRenameDialog(event.item)
            }

            is MainEvents.OnMoveMediaToVaultRequested -> {
                moveToVault(event.item)
            }

            is MainEvents.OnShareMediaRequested -> {
                shareMedia(event.item)
            }

            is MainEvents.OnFileInfoRequested -> {
                openFileInfo(event.item)
            }

            is MainEvents.OnDeleteMediaRequested -> {
                openDeleteDialog(event.item)
            }

            MainEvents.OnOpenDownloadProgressFromNotification -> {
                _state.update {
                    it.copy(
                        selectedTab = BottomNavItems.Files
                    )
                }
            }

            is MainEvents.OnMediaStoreApprovalResult -> {
                onMediaStoreApprovalResult(event.approved)
            }
        }
    }

    fun onFileDialogueEvents(event: MoreFileDetailsDialogueEvents) {
        when (event) {
            MoreFileDetailsDialogueEvents.OnPlayerDialogDismissed -> {
                dismissOptionsDialog()
            }

            MoreFileDetailsDialogueEvents.OnRenameClicked -> {
                val item = _state.value.playerMediaItem ?: return
                openRenameDialog(item)
            }

            is MoreFileDetailsDialogueEvents.OnRenameValueChanged -> {
                _state.update {
                    it.copy(
                        renameDraftName = event.value,
                        renameError = null
                    )
                }
            }

            MoreFileDetailsDialogueEvents.OnRenameDismissed -> {
                dismissRenameDialog()
            }

            MoreFileDetailsDialogueEvents.OnRenameConfirmClicked -> {
                renameSelectedFile()
            }

            MoreFileDetailsDialogueEvents.OnMoveToVaultClicked -> {
                val item = _state.value.playerMediaItem ?: return
                moveToVault(item)
            }

            MoreFileDetailsDialogueEvents.OnShareClicked -> {
                val item = _state.value.playerMediaItem ?: return
                shareMedia(item)
            }

            MoreFileDetailsDialogueEvents.OnFileInfoClicked -> {
                val item = _state.value.playerMediaItem ?: return
                openFileInfo(item)
            }

            MoreFileDetailsDialogueEvents.OnFileInfoDismissed -> {
                _state.update {
                    it.copy(fileInfoItem = null)
                }
            }

            MoreFileDetailsDialogueEvents.OnDeleteClicked -> {
                val item = _state.value.playerMediaItem ?: return
                openDeleteDialog(item)
            }

            MoreFileDetailsDialogueEvents.OnDeleteDismissed -> {
                dismissDeleteDialog()
            }

            MoreFileDetailsDialogueEvents.OnDeleteConfirmClicked -> {
                deleteSelectedFile()
            }
        }
    }

    private fun openRenameDialog(item: MediaItem) {
        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null,
                renameMediaItem = item,
                renameDraftName = item.fileName
                    .ifBlank { item.title }
                    .substringBeforeLast('.'),
                renameError = null,
                isRenaming = false
            )
        }
    }

    private fun renameSelectedFile() {
        val currentState = _state.value
        val item = currentState.renameMediaItem ?: return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRenaming = true,
                    renameError = null
                )
            }
            when (
                val result = mediaFileActionService.renameMediaItem(
                    item = item,
                    newNameWithoutExtension = currentState.renameDraftName
                )
            ) {
                is MediaFileActionResult.Success -> {
                    _state.update {
                        it.copy(
                            renameMediaItem = null,
                            renameDraftName = "",
                            renameError = null,
                            isRenaming = false,
                            pendingMediaStoreAction = null
                        )
                    }

                    emitMessage("File renamed successfully.")
                }

                is MediaFileActionResult.RequiresUserApproval -> {
                    _state.update {
                        it.copy(
                            isRenaming = false,
                            pendingMediaStoreAction = PendingMediaStoreAction.Rename(
                                item = item,
                                newNameWithoutExtension = currentState.renameDraftName
                            )
                        )
                    }

                    _uiEffects.emit(
                        MainUiEffect.RequestMediaStoreApproval(
                            intentSender = result.intentSender,
                            message = result.message
                        )
                    )
                }

                is MediaFileActionResult.Failure -> {
                    _state.update {
                        it.copy(
                            isRenaming = false,
                            renameError = result.message
                        )
                    }
                }
            }
        }
    }

    private fun dismissRenameDialog() {
        _state.update {
            it.copy(
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenaming = false
            )
        }
    }

    private fun moveToVault(item: MediaItem) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showPlayerDialogue = false,
                    playerMediaItem = null,
                    isMovingToVault = true
                )
            }

            when (
                val result = mediaFileActionService.moveMediaItemToVault(item)
            ) {
                is MediaFileActionResult.Success -> {
                    emitMessage("File moved to Vault.")
                }

                is MediaFileActionResult.RequiresUserApproval -> {
                    _state.update {
                        it.copy(
                            pendingMediaStoreAction = PendingMediaStoreAction.DeleteOriginalAfterVault(item)
                        )
                    }

                    _uiEffects.emit(
                        MainUiEffect.RequestMediaStoreApproval(
                            intentSender = result.intentSender,
                            message = result.message
                        )
                    )
                }

                is MediaFileActionResult.Failure -> {
                    emitMessage(result.message)
                }
            }

            _state.update {
                it.copy(isMovingToVault = false)
            }
        }
    }

    private fun shareMedia(item: MediaItem) {
        dismissOptionsDialog()

        viewModelScope.launch {
            _uiEffects.emit(
                MainUiEffect.ShareMediaItem(item)
            )
        }
    }

    private fun openFileInfo(item: MediaItem) {
        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null,
                fileInfoItem = item
            )
        }
    }

    private fun openDeleteDialog(item: MediaItem) {
        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null,
                deleteMediaItem = item,
                isDeleting = false
            )
        }
    }

    private fun deleteSelectedFile() {
        val item = _state.value.deleteMediaItem ?: return

        viewModelScope.launch {
            _state.update {
                it.copy(isDeleting = true)
            }

            when (
                val result = mediaFileActionService.deleteMediaItem(item)
            ) {
                is MediaFileActionResult.Success -> {
                    _state.update {
                        it.copy(
                            deleteMediaItem = null,
                            isDeleting = false,
                            pendingMediaStoreAction = null
                        )
                    }

                    emitMessage("File deleted successfully.")
                }

                is MediaFileActionResult.RequiresUserApproval -> {
                    _state.update {
                        it.copy(
                            isDeleting = false,
                            pendingMediaStoreAction = PendingMediaStoreAction.Delete(item)
                        )
                    }

                    _uiEffects.emit(
                        MainUiEffect.RequestMediaStoreApproval(
                            intentSender = result.intentSender,
                            message = result.message
                        )
                    )
                }

                is MediaFileActionResult.Failure -> {
                    _state.update {
                        it.copy(isDeleting = false)
                    }

                    emitMessage(result.message)
                }
            }
        }
    }

    private fun dismissDeleteDialog() {
        _state.update {
            it.copy(
                deleteMediaItem = null,
                isDeleting = false
            )
        }
    }

    private fun dismissOptionsDialog() {
        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null
            )
        }
    }

    private fun onMediaStoreApprovalResult(
        approved: Boolean
    ) {
        val pendingAction = _state.value.pendingMediaStoreAction

        if (pendingAction == null) {
            return
        }

        if (!approved) {
            _state.update {
                it.copy(
                    pendingMediaStoreAction = null,
                    isRenaming = false,
                    isDeleting = false,
                    isMovingToVault = false
                )
            }

            emitMessage("Permission denied.")
            return
        }

        viewModelScope.launch {
            when (pendingAction) {
                is PendingMediaStoreAction.Rename -> {
                    _state.update {
                        it.copy(
                            isRenaming = true,
                            renameError = null
                        )
                    }

                    when (
                        val result = mediaFileActionService.renameMediaItem(
                            item = pendingAction.item,
                            newNameWithoutExtension = pendingAction.newNameWithoutExtension,
                            allowUserApproval = false
                        )
                    ) {
                        is MediaFileActionResult.Success -> {
                            _state.update {
                                it.copy(
                                    renameMediaItem = null,
                                    renameDraftName = "",
                                    renameError = null,
                                    isRenaming = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage("File renamed successfully.")
                        }

                        is MediaFileActionResult.RequiresUserApproval -> {
                            _state.update {
                                it.copy(
                                    isRenaming = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage("Permission is still required.")
                        }

                        is MediaFileActionResult.Failure -> {
                            _state.update {
                                it.copy(
                                    isRenaming = false,
                                    renameError = result.message,
                                    pendingMediaStoreAction = null
                                )
                            }
                        }
                    }
                }

                is PendingMediaStoreAction.Delete -> {
                    _state.update {
                        it.copy(isDeleting = true)
                    }

                    when (
                        val result = mediaFileActionService.deleteMediaItem(
                            item = pendingAction.item,
                            allowUserApproval = false
                        )
                    ) {
                        is MediaFileActionResult.Success -> {
                            _state.update {
                                it.copy(
                                    deleteMediaItem = null,
                                    isDeleting = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage("File deleted successfully.")
                        }

                        is MediaFileActionResult.RequiresUserApproval -> {
                            _state.update {
                                it.copy(
                                    isDeleting = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage("Permission is still required.")
                        }

                        is MediaFileActionResult.Failure -> {
                            _state.update {
                                it.copy(
                                    isDeleting = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage(result.message)
                        }
                    }
                }

                is PendingMediaStoreAction.DeleteOriginalAfterVault -> {
                    _state.update {
                        it.copy(isMovingToVault = true)
                    }

                    when (
                        val result = mediaFileActionService.deleteMediaItem(
                            item = pendingAction.item,
                            allowUserApproval = false
                        )
                    ) {
                        is MediaFileActionResult.Success -> {
                            _state.update {
                                it.copy(
                                    isMovingToVault = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage("File moved to Vault and removed from Gallery.")
                        }

                        is MediaFileActionResult.RequiresUserApproval -> {
                            _state.update {
                                it.copy(
                                    isMovingToVault = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage("Permission is still required.")
                        }

                        is MediaFileActionResult.Failure -> {
                            _state.update {
                                it.copy(
                                    isMovingToVault = false,
                                    pendingMediaStoreAction = null
                                )
                            }

                            emitMessage(
                                "File moved to Vault, but original gallery file could not be removed."
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observePremiumLabelState() {
        viewModelScope.launch {
            combine(
                premiumEntitlementRepository.observeEntitlement(),
                remoteConfigRepository.configState
            ) { entitlement, snapshot ->
                val premiumConfig = snapshot.appConfig.premium
                val isPremiumActive = entitlement.isPremiumUser || entitlement.hasLifetimePurchase

                premiumConfig.enabled&&
                        premiumConfig.showPremiumScreen &&
                        premiumConfig.showPremiumLabel &&
                        !isPremiumActive
            }.collect { shouldShow ->
                Timber.d("observePremiumLabelState: $shouldShow")

                _state.update { currentState ->
                    currentState.copy(showPremiumLabel = shouldShow)
                }
            }
        }
    }

    private fun emitMessage(message: String) {
        viewModelScope.launch {
            _uiEffects.emit(
                MainUiEffect.ShowMessage(message)
            )
        }
    }
}