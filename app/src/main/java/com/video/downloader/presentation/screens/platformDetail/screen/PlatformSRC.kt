package com.video.downloader.presentation.screens.platformDetail.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.screens.platformDetail.componants.PlatformDetailTopBar
import com.video.downloader.presentation.screens.platformDetail.componants.PlatformLinkInputCard
import com.video.downloader.presentation.screens.platformDetail.componants.TrendingVideoCard
import com.video.downloader.presentation.screens.platformDetail.events.PlatformDetailEvents
import com.video.downloader.presentation.screens.platformDetail.states.PlatformDetailStates
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles


@Composable
fun PlatformSRC(
    state: PlatformDetailStates,
    onEvent: (PlatformDetailEvents) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )
                PlatformDetailTopBar(
                    platformName = state.platform.title,
                    onBackClick = {
                        onEvent(PlatformDetailEvents.BackClicked)
                    }
                )
            }
        }
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(paddingValues)
        ) {

            PlatformLinkInputCard(
                value = state.videoUrl,
                onValueChange = { value ->
                    onEvent(PlatformDetailEvents.VideoUrlChanged(value))
                },
                onPasteClick = {
                    onEvent(PlatformDetailEvents.PasteClicked(state.videoUrl))
                },
            )

            Spacer(modifier = Modifier.size(13.dp))

            AppGradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.download_video),
                onClick = {
                    onEvent(PlatformDetailEvents.DownloadVideoClicked)
                },
                leadingIcon ={
                    Icon(
                        painter = painterResource(R.drawable.ic_cloud_download),
                        contentDescription = stringResource(R.string.download_video),
                        tint = AppColors.Background,
                        modifier = Modifier.size(28.dp)
                    )
                },
                isLoading = state.isLoading,
             )


            Spacer(modifier = Modifier.size(26.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (state.trendingVideos.isEmpty()){
                    Text(
                        text = stringResource(R.string.no_trending_videos),
                        style = AppTextStyles.titleLarge,
                        color = AppColors.TextEnabled,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }else {

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.trending_now),
                            style = AppTextStyles.titleMedium,
                            color = AppColors.TextEnabled,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = stringResource(R.string.see_all),
                            style = AppTextStyles.bodyMedium.copy(
                                color = AppColors.HighlightGradientTop
                            ),
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(14.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(end = 20.dp)
                    ) {
                        items(
                            items = state.trendingVideos,
                            key = { video -> video.id }
                        ) { video ->
                            TrendingVideoCard(
                                video = video,
                                onClick = {
                                    onEvent(
                                        PlatformDetailEvents.TrendingVideoClicked(
                                            videoId = video.id
                                        )
                                    )
                                },
                                modifier = Modifier.fillParentMaxWidth(0.31f)
                            )
                        }
                    }
                }

            }

        }


    }


}