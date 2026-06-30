package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MoreFileDetailsDialogueEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.componants.PlayerMediaActionsDialog
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import com.all.video.downloader.fast.hd.secure.video.downloader.di.EntryPointAccessors
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants.DownloadFileInfoDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.componants.DeleteMediaConfirmationDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.componants.RenameMediaDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainUiEffect
 import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.BottomNavItems

@Composable
fun MainRootSRC(
    backStack: NavBackStack<NavKey>,
    notificationOpenRequestId: Long = 0L,
    viewModel: MainViewModel = hiltViewModel<MainViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    val mediaStoreApprovalLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.onEvent(
            MainEvents.OnMediaStoreApprovalResult(
                approved = result.resultCode == Activity.RESULT_OK
            )
        )
    }

    LaunchedEffect(notificationOpenRequestId) {
        if (notificationOpenRequestId > 0L) {
            viewModel.onEvent(MainEvents.OnOpenDownloadProgressFromNotification)
        }
    }

    LaunchedEffect(viewModel.uiEffects, context) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is MainUiEffect.ShareMediaItem -> {
                    context.shareMediaItem(effect.item)
                }

                is MainUiEffect.ShowMessage -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MainUiEffect.RequestMediaStoreApproval -> {
                    runCatching {
                        mediaStoreApprovalLauncher.launch(
                            IntentSenderRequest.Builder(effect.intentSender).build()
                        )
                    }.getOrElse {
                        Toast.makeText(
                            context,
                            effect.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    BackHandler {
        if (state.selectedTab == BottomNavItems.Home){
            activity?.finish()
        }else{
            viewModel.onEvent(MainEvents.OnTabSelected(BottomNavItems.Home))
        }
    }

    MainSRC(
        backStack = backStack,
        state = state,
        viewModel = viewModel,
        notificationOpenRequestId = notificationOpenRequestId
    )

    PlayerMediaActionsDialog(
        isVisible = state.showPlayerDialogue,
        mediaFile = state.playerMediaItem,
        onDismiss = {
            viewModel.onFileDialogueEvents(
                MoreFileDetailsDialogueEvents.OnPlayerDialogDismissed
            )
        },
        onRenameClick = {
            viewModel.onFileDialogueEvents(
                MoreFileDetailsDialogueEvents.OnRenameClicked
            )
        },
        onMoveToVaultClick = {
            viewModel.onFileDialogueEvents(
                MoreFileDetailsDialogueEvents.OnMoveToVaultClicked
            )
        },
        onShareClick = {
            viewModel.onFileDialogueEvents(
                MoreFileDetailsDialogueEvents.OnShareClicked
            )
        },
        onFileInfoClick = {
            viewModel.onFileDialogueEvents(
                MoreFileDetailsDialogueEvents.OnFileInfoClicked
            )
        },
        onDeleteClick = {
            viewModel.onFileDialogueEvents(
                MoreFileDetailsDialogueEvents.OnDeleteClicked
            )
        }
    )

    state.renameMediaItem?.let { item ->
        RenameMediaDialog(
            item = item,
            value = state.renameDraftName,
            error = state.renameError,
            isLoading = state.isRenaming,
            onValueChange = { value ->
                viewModel.onFileDialogueEvents(
                    MoreFileDetailsDialogueEvents.OnRenameValueChanged(value)
                )
            },
            onDismiss = {
                viewModel.onFileDialogueEvents(
                    MoreFileDetailsDialogueEvents.OnRenameDismissed
                )
            },
            onConfirm = {
                viewModel.onFileDialogueEvents(
                    MoreFileDetailsDialogueEvents.OnRenameConfirmClicked
                )
            }
        )
    }

    state.fileInfoItem?.let { item ->
        DownloadFileInfoDialog(
            item = item,
            onDismiss = {
                viewModel.onFileDialogueEvents(
                    MoreFileDetailsDialogueEvents.OnFileInfoDismissed
                )
            }
        )
    }

    state.deleteMediaItem?.let { item ->
        DeleteMediaConfirmationDialog(
            item = item,
            isLoading = state.isDeleting,
            onDismiss = {
                viewModel.onFileDialogueEvents(
                    MoreFileDetailsDialogueEvents.OnDeleteDismissed
                )
            },
            onConfirm = {
                viewModel.onFileDialogueEvents(
                    MoreFileDetailsDialogueEvents.OnDeleteConfirmClicked
                )
            }
        )
    }
}

private fun Context.shareMediaItem(
    item: MediaItem
) {
    val service = EntryPointAccessors.mediaFileActionService(this)
    val shareUri = service.buildShareUri(item) ?: return

    val mimeType = item.mimeType.ifBlank {
        when (item.mediaType) {
            MediaType.VIDEO -> "video/*"
            MediaType.AUDIO -> "audio/*"
            else -> "*/*"
        }
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, shareUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(
        shareIntent,
        "Share ${item.fileName.ifBlank { item.title }}"
    )

    if (this !is Activity) {
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    startActivity(chooser)


}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}