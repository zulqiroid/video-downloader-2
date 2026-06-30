package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.mapper.toMediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AudioCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.VideoCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.events.PlayerEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states.PlayerStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states.PlayerTab
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states.currentTabItems
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.viewModel.PlayerViewModel

@Composable
fun PlayerDefaultContent(
    state: PlayerStates,
    viewModel: PlayerViewModel,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaItem) -> Unit,
    mainPaddingValues: PaddingValues,
) {
    val list = state.currentTabItems()
    val isVideo = state.selectedTab == PlayerTab.VIDEO

    val isCurrentTabInitialLoading = if (isVideo) {
        state.isVideoInitialLoading
    } else {
        state.isAudioInitialLoading
    }

    val isCurrentTabPageLoading = if (isVideo) {
        state.isVideoPageLoading
    } else {
        state.isAudioPageLoading
    }

    val hasMoreCurrentTabItems = if (isVideo) {
        state.hasMoreVideos
    } else {
        state.hasMoreAudios
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        MediaTabs(
            selectedTab = state.selectedTab,
            onTabChange = viewModel::onTabChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isCurrentTabInitialLoading) {
            LoadingView()
            return@Column
        }

        if (list.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmptyListView(
                    icon = if (isVideo) {
                        R.drawable.ic_play
                    } else {
                        R.drawable.ic_headphones
                    },
                    title = if (isVideo) {
                        stringResource(R.string.no_videos_found)
                    } else {
                        stringResource(R.string.no_audios_found)
                    },
                    subtitle = if (isVideo) {
                        stringResource(R.string.your_downloaded_videos_will_appear_once_completed)
                    } else {
                        stringResource(R.string.your_downloaded_audios_will_appear_once_completed)
                    }
                )

                Spacer(
                    modifier = Modifier.height(
                        mainPaddingValues.calculateBottomPadding()
                    )
                )
            }

            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = list,
                key = { _, item -> item.id }
            ) { index, item ->

                if (index >= list.lastIndex - LOAD_MORE_PREFETCH_DISTANCE) {
                    LaunchedEffect(
                        list.size,
                        state.selectedTab,
                        hasMoreCurrentTabItems,
                        isCurrentTabPageLoading
                    ) {
                        viewModel.onEvent(
                            PlayerEvents.OnLoadNextMediaPage(
                                tab = state.selectedTab
                            )
                        )
                    }
                }

                if (isVideo) {
                    VideoCard(
                        item = item,
                        onClick = {
                            val mediaList = state.videos.map { mediaItem ->
                                mediaItem.toMediaFile()
                            }

                            if (mediaList.isNotEmpty()) {
                                sendToMedia(
                                   mediaList,
                                      index
                                )
                            }
                        },
                        onMoreClick = { mediaItem ->
                            onMoreClick(mediaItem)
                        }
                    )
                } else {
                    AudioCard(
                        item = item,
                        onClick = {
                            val mediaList = state.audios.map { mediaItem ->
                                mediaItem.toMediaFile()
                            }

                            if (mediaList.isNotEmpty()) {
                                sendToMedia(
                                    mediaList,
                                    index
                                )
                            }
                        },
                        onMoreClick = { mediaItem ->
                            onMoreClick(mediaItem)
                        }
                    )
                }
            }

            if (isCurrentTabPageLoading) {
                item(
                    key = "player_media_page_loader_${state.selectedTab}"
                ) {
                    PaginationLoadingView()
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
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PaginationLoadingView() {
    Column(
        modifier = Modifier.height(72.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

private const val LOAD_MORE_PREFETCH_DISTANCE = 5