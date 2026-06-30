package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants.ResultErrorMessage
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants.ResultFileCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants.ResultHero
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants.ResultLoadingContent
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants.ResultSavedMessage
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants.ResultTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.events.VideoToMp3ResultEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.states.VideoToMp3ResultState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors

@Composable
fun VideoToMp3ResultSRC(
    state: VideoToMp3ResultState,
    onEvent: (VideoToMp3ResultEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )

                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_TO_MP3_RESULT,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                ) {
                    ResultTopBar(
                        onBackClick = {
                            onEvent(VideoToMp3ResultEvents.BackClicked)
                        }
                    )
                }

                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_TO_MP3_RESULT,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_TO_MP3_RESULT,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 0.dp
                )
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_TO_MP3_RESULT,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 0.dp
                )
            }
        }
    ) { paddingValues ->

        val item = state.item

        if (item == null) {
            ResultLoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            ResultHero(
                isSaved = state.isSaved
            )

            Spacer(modifier = Modifier.height(34.dp))

            ResultFileCard(
                item = item,
                isSaving = state.isSaving,
                isSaved = state.isSaved,
                onSaveClick = {
                    onEvent(VideoToMp3ResultEvents.SaveClicked)
                }
            )

            state.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(14.dp))

                ResultErrorMessage(
                    message = message
                )
            }

            if (state.isSaved) {
                Spacer(modifier = Modifier.height(14.dp))

                ResultSavedMessage()
            }
        }
    }
}