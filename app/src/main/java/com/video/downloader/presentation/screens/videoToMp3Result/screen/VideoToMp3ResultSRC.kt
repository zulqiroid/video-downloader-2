package com.video.downloader.presentation.screens.videoToMp3Result.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.screens.videoToMp3Result.componants.ResultErrorMessage
import com.video.downloader.presentation.screens.videoToMp3Result.componants.ResultFileCard
import com.video.downloader.presentation.screens.videoToMp3Result.componants.ResultHero
import com.video.downloader.presentation.screens.videoToMp3Result.componants.ResultLoadingContent
import com.video.downloader.presentation.screens.videoToMp3Result.componants.ResultSavedMessage
import com.video.downloader.presentation.screens.videoToMp3Result.componants.ResultTopBar
import com.video.downloader.presentation.screens.videoToMp3Result.events.VideoToMp3ResultEvents
import com.video.downloader.presentation.screens.videoToMp3Result.states.VideoToMp3ResultState
import com.video.downloader.presentation.theme.AppColors

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
                    .padding(horizontal = 22.dp)
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )

                ResultTopBar(
                    onBackClick = {
                        onEvent(VideoToMp3ResultEvents.BackClicked)
                    }
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