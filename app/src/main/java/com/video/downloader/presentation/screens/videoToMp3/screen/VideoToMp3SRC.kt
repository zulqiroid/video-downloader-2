package com.video.downloader.presentation.screens.videoToMp3.screen

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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.screens.videoToMp3.componants.EmptyUploadContent
import com.video.downloader.presentation.screens.videoToMp3.componants.SelectedVideoContent
import com.video.downloader.presentation.screens.videoToMp3.componants.VideoToMp3ErrorText
import com.video.downloader.presentation.screens.videoToMp3.componants.VideoToMp3ProgressDialog
import com.video.downloader.presentation.screens.videoToMp3.componants.VideoToMp3TopBar
import com.video.downloader.presentation.screens.videoToMp3.componants.activeConversion
import com.video.downloader.presentation.screens.videoToMp3.events.VideoToMp3Events
import com.video.downloader.presentation.screens.videoToMp3.states.VideoToMp3States
import com.video.downloader.presentation.theme.AppColors

@Composable
fun VideoToMp3SRC(
    state: VideoToMp3States,
    onEvent: (VideoToMp3Events) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeConversion = state.activeConversion

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 14.dp)
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )

                VideoToMp3TopBar(
                    onBackClick = {
                        onEvent(VideoToMp3Events.BackClicked)
                    }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(top = 12.dp)
            ) {
                if (state.hasVideo) {
                    AppGradientButton(
                        text = if (activeConversion != null) {
                            "Converting..."
                        } else {
                            "Convert to MP3"
                        },
                        onClick = {
                            onEvent(VideoToMp3Events.ConvertClicked)
                        },
                        enabled = activeConversion == null && !state.isSubmitting,
                        isLoading = state.isSubmitting,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_convert_filled),
                                contentDescription = null,
                                tint = AppColors.OnHighlight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                } else {
                    AppGradientButton(
                        text = "Upload Video",
                        onClick = {
                            onEvent(VideoToMp3Events.UploadVideoClicked)
                        },
                        buttonHeight = 52.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                .padding(horizontal = 22.dp)
        ) {
            if (state.hasVideo) {
                SelectedVideoContent(
                    state = state,
                    activeConversion = activeConversion,
                    onEvent = onEvent
                )
            } else {
                EmptyUploadContent(
                    onUploadClick = {
                        onEvent(VideoToMp3Events.UploadVideoClicked)
                    }
                )
            }

            state.errorMessage?.let { message ->
                Spacer(modifier = Modifier.size(12.dp))

                VideoToMp3ErrorText(
                    message = message
                )
            }
        }
    }

    activeConversion?.let { item ->
        VideoToMp3ProgressDialog(
            item = item,
            selectedPreset = state.selectedPreset,
            onCancelClick = {
                onEvent(VideoToMp3Events.CancelClicked(item.id))
            }
        )
    }
}