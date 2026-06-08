package com.video.downloader.presentation.screens.videoSplitter.screen

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
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterPreviewPlayerCard
import com.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterRangeSection
import com.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterResultContent
import com.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterTopBar
import com.video.downloader.presentation.screens.videoSplitter.componants.VideoSplitterUploadCard
import com.video.downloader.presentation.screens.videoSplitter.events.VideoSplitterEvents
import com.video.downloader.presentation.screens.videoSplitter.states.VideoSplitterStates
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VideoSplitterSRC(
    state: VideoSplitterStates,
    onEvent: (VideoSplitterEvents) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )

                VideoSplitterTopBar(
                    onBackClick = {
                        onEvent(VideoSplitterEvents.BackClicked)
                    }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp),
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