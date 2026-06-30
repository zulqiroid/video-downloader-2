package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.VideoCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events.DownloadEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadTab
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.viewModel.DownloadViewModel

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
    onMoreClick: (MediaItem) -> Unit,
    state: DownloadStates,
) {
    if (list.isEmpty()) {
        if (selectedTab == DownloadTab.COMPLETED && state.isCompletedInitialLoading) {
            CompletedInitialLoadingContent(
                mainPaddingValues = mainPaddingValues
            )
        } else {
            DownloadEmptyContent(
                mainPaddingValues = mainPaddingValues,
                selectedTab = selectedTab,
            )
        }
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
                    DownloadingCard(
                        item = item,
                        onPauseClick = { id ->
                            viewModel.onEvents(
                                DownloadEvents.OnPauseDownloadingClicked(id)
                            )
                        },
                        onResumeClick = { id ->
                            viewModel.onEvents(
                                DownloadEvents.OnResumeDownloadingClicked(id)
                            )
                        },
                        onMoreClick = onMoreClick
                    )
                }

                DownloadTab.COMPLETED -> {
                    if (index >= list.lastIndex - LOAD_MORE_PREFETCH_DISTANCE) {
                        LaunchedEffect(
                            list.size,
                            state.hasMoreCompleted,
                            state.isCompletedPageLoading
                        ) {
                            viewModel.onEvents(
                                DownloadEvents.OnLoadNextCompletedFiles
                            )
                        }
                    }

                    VideoCard(
                        item = item,
                        onClick = {
                            viewModel.onEvents(
                                DownloadEvents.OnCompletedMediaClicked(item.id)
                            )
                        },
                        onMoreClick = onMoreClick
                    )
                }
            }
        }

        if (selectedTab == DownloadTab.COMPLETED && state.isCompletedPageLoading) {
            item(
                key = "completed_pagination_loader"
            ) {
                CompletedPaginationLoadingContent()
            }
        }

        if (
            selectedTab == DownloadTab.COMPLETED &&
            !state.isCompletedPageLoading &&
            !state.hasMoreCompleted
        ) {
            item(
                key = "completed_pagination_end"
            ) {
                CompletedEndContent()
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(
                    mainPaddingValues.calculateBottomPadding()
                )
            )
        }
    }
}

@Composable
private fun CompletedInitialLoadingContent(
    mainPaddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = mainPaddingValues.calculateBottomPadding()
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CompletedPaginationLoadingContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(end = 12.dp)
        )

        Text(
            text = "Loading more videos...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CompletedEndContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No more videos",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private const val LOAD_MORE_PREFETCH_DISTANCE = 5