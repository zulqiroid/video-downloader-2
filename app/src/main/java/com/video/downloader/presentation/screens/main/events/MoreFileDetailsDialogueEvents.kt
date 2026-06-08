package com.video.downloader.presentation.screens.main.events


sealed interface MoreFileDetailsDialogueEvents {

    data object OnPlayerDialogDismissed : MoreFileDetailsDialogueEvents

    data object OnRenameClicked : MoreFileDetailsDialogueEvents

    data object OnMoveToVaultClicked : MoreFileDetailsDialogueEvents

    data object OnShareClicked : MoreFileDetailsDialogueEvents

    data object OnFileInfoClicked : MoreFileDetailsDialogueEvents

    data object OnDeleteClicked : MoreFileDetailsDialogueEvents
}