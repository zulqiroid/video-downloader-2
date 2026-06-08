package com.video.downloader.presentation.screens.download.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.screens.download.componants.DownloadFilesTabs
import com.video.downloader.presentation.screens.download.componants.DownloadListContent
import com.video.downloader.presentation.screens.download.componants.DownloadTopBar
import com.video.downloader.presentation.screens.download.events.DownloadEvents
import com.video.downloader.presentation.screens.download.states.DownloadStates
import com.video.downloader.presentation.screens.download.states.currentTabItems
import com.video.downloader.presentation.screens.download.viewModel.DownloadViewModel
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.video.downloader.presentation.theme.AppColors


@Composable
fun DownloadSRC(
    mainViewModel: MainViewModel,
    mainPaddingValues: PaddingValues,
    state: DownloadStates,
    viewModel: DownloadViewModel
) {

    val list = state.currentTabItems()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),

            ) {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

                DownloadTopBar(
                    onSettingCLicked = {
                        viewModel.onEvents(DownloadEvents.OnSettingClicked)
                    }
                )

            }
        }
    ) {paddingValues ->

        Box(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DownloadFilesTabs(
                    selectedTab = state.selectedTab,
                    onTabChange = {
                        viewModel.onEvents(
                            DownloadEvents.OnTabChange(it)
                        )
                    }
                )

                DownloadListContent(
                    state = state,
                    mainPaddingValues = mainPaddingValues,
                    selectedTab = state.selectedTab,
                    list = list,
                    viewModel = viewModel,
                    sendToMedia = { mediaList, startIndex -> },
                    onMoreClick = {}
                )
            }
        }

    }

}