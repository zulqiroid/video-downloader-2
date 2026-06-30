package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterPreviewPlayerCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterRangeSection
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterResultContent
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterUploadCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.events.VideoSplitterEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.states.VideoSplitterStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.core.ads.ui.ScreenNativeAd
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys

@Composable
fun VideoSplitterSRC(
    state: VideoSplitterStates,
    onEvent: (VideoSplitterEvents) -> Unit,
    modifier: Modifier = Modifier,
) {

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    var resultInterstitialShown by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(state.showResultScreen) {
        if (state.showResultScreen && !resultInterstitialShown) {
            resultInterstitialShown = true

            interstitialAdGate?.showForFeature(
                activity = activity,
                featureKey = VideoDownloaderAdFeatureKeys.VIDEO_SPLITTER_RESULT
            )
        }

        if (!state.showResultScreen) {
            resultInterstitialShown = false
        }
    }

    if (state.showResultScreen) {
        VideoSplitterResultContent(
            clips = state.resultClips,
            isSavingClip = state.isSavingClip,
            savingClipId = state.savingClipId,
            saveSuccessMessage = state.saveSuccessMessage,
            errorMessage = state.errorMessage,
            onBackClick = {
                onEvent(VideoSplitterEvents.ResultBackClicked)
            },
            onSaveClipClick = { clip ->
                onEvent(
                    VideoSplitterEvents.SaveClipClicked(
                        clipId = clip.id
                    )
                )
            },
            modifier = modifier
        )
        return
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )

                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp) ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    VideoSplitterTopBar(
                        onBackClick = {
                            onEvent(VideoSplitterEvents.BackClicked)
                        }
                    )
                }

                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER,
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
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppGradientButton(
                        text = if (state.hasVideo) "Split Video" else "Upload Video",
                        onClick = {
                            if (state.hasVideo) {
                                onEvent(VideoSplitterEvents.SplitVideoClicked)
                            } else {
                                onEvent(VideoSplitterEvents.UploadVideoClicked)
                            }
                        },
                        enabled = if (state.hasVideo) state.canSplit else true,
                        isLoading = state.isSplitting,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = if (state.hasVideo) {
                            {
                                Icon(
                                    imageVector = Icons.Default.ContentCut,
                                    contentDescription = null,
                                    tint = AppColors.OnHighlight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {


            if (!state.hasVideo) {
                VideoSplitterUploadCard(
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                VideoSplitterPreviewPlayerCard(
                    videoUri = state.selectedVideoUri.orEmpty(),
                    startMs = state.rangeStartMs,
                    endMs = state.rangeEndMs,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(22.dp))

                VideoSplitterRangeSection(
                    startMs = state.rangeStartMs,
                    endMs = state.rangeEndMs,
                    durationMs = state.durationMs,
                    onRangeChange = { start, end ->
                        onEvent(
                            VideoSplitterEvents.RangeChanged(
                                startMs = start,
                                endMs = end
                            )
                        )
                    }
                )

                state.errorMessage?.let { message ->
                    Spacer(modifier = Modifier.size(16.dp))

                    Text(
                        text = message,
                        style = AppTextStyles.bodySmall.copy(
                            color = AppColors.Error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}