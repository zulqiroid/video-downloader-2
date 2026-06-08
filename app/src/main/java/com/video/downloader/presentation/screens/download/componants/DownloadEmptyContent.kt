package com.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.video.downloader.R
import com.video.downloader.presentation.screens.download.states.DownloadTab
import com.video.downloader.presentation.screens.player.componants.EmptyListView

@Composable
  fun DownloadEmptyContent(
    mainPaddingValues: PaddingValues,
    selectedTab: DownloadTab,
 ) {
    val title = when (selectedTab) {
        DownloadTab.IN_PROGRESS -> stringResource(R.string.no_downloading_yet)
        DownloadTab.COMPLETED -> stringResource(R.string.no_downloads_yet)
    }

    val message = when (selectedTab) {
        DownloadTab.IN_PROGRESS -> stringResource(R.string.your_downloading_files_will_appear_once_starts)
        DownloadTab.COMPLETED -> stringResource(R.string.your_downloaded_files_will_appear_once_completed)
    }

    Box(
        Modifier.padding(bottom = mainPaddingValues.calculateBottomPadding()).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        EmptyListView(

            icon = R.drawable.ic_download_filled,

            title = title,

            subtitle = message

        )
    }
}