package com.video.downloader.presentation.screens.downloadGuide.screen

import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.video.downloader.R
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.screens.downloadGuide.componants.DownloadGuideStepCard
import com.video.downloader.presentation.screens.downloadGuide.componants.DownloadGuideTopBar
import com.video.downloader.presentation.screens.downloadGuide.events.DownloadGuideEvents
import com.video.downloader.presentation.screens.downloadGuide.states.DownloadGuideStates
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun DownloadGuideSRC(
    state: DownloadGuideStates,
    onEvent: (DownloadGuideEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
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
                DownloadGuideTopBar(
                    title = stringResource(id = R.string.how_to_download_videos),
                    onBackClick = {
                        onEvent(DownloadGuideEvents.BackClicked)
                    }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppGradientButton(
                    text = stringResource(id = R.string.try_now),
                    onClick = {
                        onEvent(DownloadGuideEvents.TryNowClicked)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(
                    Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Text(
                text = stringResource(id = R.string.download_guide_subtitle),
                style = AppTextStyles.bodySmall,
                color = AppColors.TextDisabled,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .padding(top = 20.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 17.dp)
                    .padding(top = 26.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(
                    items = state.steps,
                    key = { step -> step.id }
                ) { step ->
                    DownloadGuideStepCard(
                        step = step
                    )
                }
            }
        }


    }
}