package com.video.downloader.presentation.screens.player.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.video.downloader.R
import com.video.downloader.domain.mapper.toMediaFile
import com.video.downloader.domain.models.MediaFile
import com.video.downloader.domain.models.MediaItem
import com.video.downloader.presentation.common.componants.AudioCard
import com.video.downloader.presentation.common.componants.VideoCard
import com.video.downloader.presentation.screens.player.states.PlayerStates
import com.video.downloader.presentation.screens.player.states.PlayerTab
import com.video.downloader.presentation.screens.player.states.currentTabItems
import com.video.downloader.presentation.screens.player.states.toMediaFile
import com.video.downloader.presentation.screens.player.viewModel.PlayerViewModel

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

        if (state.isLoading) {
            LoadingView()
            return@Column
        }

        if (state.videos.isEmpty() || state.audios.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    EmptyListView(
                        icon = if (isVideo) R.drawable.ic_play else R.drawable.ic_headphones,
                        title = if (isVideo) stringResource(R.string.no_videos_found) else stringResource(
                            R.string.no_audios_found
                        ),
                        subtitle = if (isVideo) stringResource(R.string.your_downloaded_videos_will_appear_once_completed) else stringResource(
                            R.string.your_downloaded_audios_will_appear_once_completed
                        )
                    )
                }
                Spacer(modifier = Modifier.height(mainPaddingValues.calculateBottomPadding()))

            }

        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                itemsIndexed(
                    items = list,
                    key = { _, item -> item.id }
                ) { index, item ->

                    if (isVideo) {
                        VideoCard(
                            item = item,
                            onClick = {
                                  val mediaList = state.videos.map { it.toMediaFile() }

                                  if (mediaList.isNotEmpty()) {
                                      sendToMedia(mediaList, index)
                                  }
                            },
                            onMoreClick = {
                                onMoreClick(it)
                            }
                        )
                    } else {
                        AudioCard(
                            item = item,
                            onClick = {
                                       val mediaList = state.audios.map { it.toMediaFile() }

                                       if (mediaList.isNotEmpty()) {
                                           sendToMedia(mediaList, index)
                                       }
                            },
                            onMoreClick = {
                                onMoreClick(it)
                            }
                        )
                    }

                }

                item {
                    Spacer(modifier = Modifier.height(mainPaddingValues.calculateBottomPadding()))
                }
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
