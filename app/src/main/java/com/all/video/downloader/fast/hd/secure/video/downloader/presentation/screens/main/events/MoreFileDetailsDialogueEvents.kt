package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events


sealed interface MoreFileDetailsDialogueEvents {

    data object OnPlayerDialogDismissed : MoreFileDetailsDialogueEvents

    data object OnRenameClicked : MoreFileDetailsDialogueEvents

    data class OnRenameValueChanged(
        val value: String
    ) : MoreFileDetailsDialogueEvents

    data object OnRenameDismissed : MoreFileDetailsDialogueEvents

    data object OnRenameConfirmClicked : MoreFileDetailsDialogueEvents

    data object OnMoveToVaultClicked : MoreFileDetailsDialogueEvents

    data object OnShareClicked : MoreFileDetailsDialogueEvents

    data object OnFileInfoClicked : MoreFileDetailsDialogueEvents

    data object OnFileInfoDismissed : MoreFileDetailsDialogueEvents

    data object OnDeleteClicked : MoreFileDetailsDialogueEvents

    data object OnDeleteDismissed : MoreFileDetailsDialogueEvents

    data object OnDeleteConfirmClicked : MoreFileDetailsDialogueEvents
}