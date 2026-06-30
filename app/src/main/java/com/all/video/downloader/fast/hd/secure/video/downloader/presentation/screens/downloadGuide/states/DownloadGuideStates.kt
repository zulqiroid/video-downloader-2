package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.states

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.all.video.downloader.fast.hd.secure.video.downloader.R

@Immutable
data class DownloadGuideStates(
    val steps: List<DownloadGuideStepUiModel> = defaultDownloadGuideSteps,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Immutable
data class DownloadGuideStepUiModel(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val iconType: DownloadGuideIconType
)

enum class DownloadGuideIconType {
    Link,
    Clipboard,
    Download
}

val defaultDownloadGuideSteps = listOf(
    DownloadGuideStepUiModel(
        id = "copy_link",
        titleRes = R.string.copy_link,
        descriptionRes = R.string.copy_link_description,
        iconType = DownloadGuideIconType.Link
    ),
    DownloadGuideStepUiModel(
        id = "paste_link",
        titleRes = R.string.paste_link,
        descriptionRes = R.string.paste_link_description,
        iconType = DownloadGuideIconType.Clipboard
    ),
    DownloadGuideStepUiModel(
        id = "download",
        titleRes = R.string.download,
        descriptionRes = R.string.download_description,
        iconType = DownloadGuideIconType.Download
    )
)