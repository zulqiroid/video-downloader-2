package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

sealed interface DownloadOptionId {

    data object Pause : DownloadOptionId

    data object Resume : DownloadOptionId

    data object Retry : DownloadOptionId

    data object Cancel : DownloadOptionId

    data object Delete : DownloadOptionId

    data object Share : DownloadOptionId

    data object Rename : DownloadOptionId

    data object FileInfo : DownloadOptionId

    data object MoveToVault : DownloadOptionId
}