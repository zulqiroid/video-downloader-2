package com.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.downloader.domain.mapper.toPlayerUiItem
import com.video.downloader.domain.models.MediaFile
import com.video.downloader.domain.models.MediaItem
import com.video.downloader.domain.models.PlayerUiItem
import com.video.downloader.presentation.common.componants.VideoCard
import com.video.downloader.presentation.screens.download.states.DownloadStates
import com.video.downloader.presentation.screens.download.states.DownloadTab
import com.video.downloader.presentation.screens.download.states.DownloadUiItem
import com.video.downloader.presentation.screens.download.states.toMediaFile
import com.video.downloader.presentation.screens.download.viewModel.DownloadViewModel
import com.video.downloader.presentation.screens.player.states.toMediaFile

@Composable
 fun DownloadListContent(
    mainPaddingValues: PaddingValues,
    selectedTab: DownloadTab,
    list: List<MediaItem>,
    viewModel: DownloadViewModel,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
    state: DownloadStates,
) {
    if (list.isEmpty()) {
        DownloadEmptyContent(
            mainPaddingValues = mainPaddingValues,
            selectedTab = selectedTab,
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        itemsIndexed(
            items = list,
            key = { _, item -> item.id }
        ) { index, item ->

            when (selectedTab) {
                DownloadTab.IN_PROGRESS -> {

                    VideoCard(
                        item = item,
                        onClick = {
                       /*     sendToMedia(
                                list.map { it.toMediaFile(true) },
                                index
                            )*/
                        },
                        onMoreClick = {

                        }
                    )
                /*    DownloadingCard(
                        item = item,
                        state =state,
                        viewModel = viewModel
                    )*/
                }

                DownloadTab.COMPLETED -> {

                    VideoCard(
                        item = item,
                        onClick = {
                    /*        sendToMedia(
                                list.map { it.toMediaFile(true) },
                                index
                            )*/
                        },
                        onMoreClick = {

                        }
                    )
                /*    CompletedCard(
                        item = item,
                        state = state,
                        viewModel = viewModel,
                        onItemCLicked = {
                            sendToMedia(
                                list.map { it.toMediaFile(true) },
                                index
                            )
                        },
                        onMoreClicked = {
                            onMoreClick(
                                it.toMediaFile(true)
                            )
                        }
                    )*/
                }
            }

        }

        item {
            Spacer(modifier = Modifier.height(mainPaddingValues.calculateBottomPadding()))
        }
    }
}