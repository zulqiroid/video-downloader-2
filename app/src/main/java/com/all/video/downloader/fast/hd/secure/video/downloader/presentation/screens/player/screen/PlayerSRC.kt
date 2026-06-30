package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MoreFileDetailsDialogueEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.MainStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.componants.PlayerDefaultContent
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.componants.PlayerMediaActionsDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.componants.PlayerTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.events.PlayerEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states.PlayerStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.viewModel.PlayerViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors


@Composable
fun PlayerSRC(
    mainViewModel: MainViewModel,
    state: PlayerStates,
    viewModel: PlayerViewModel,
    mainPaddingValues: PaddingValues,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
 ) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AppColors.Background,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                    ScreenBannerAd(
                        screenKey = VideoDownloaderAdScreenKeys.PLAYER,
                        position = AdSlotPosition.TOP,
                        modifier = Modifier.fillMaxWidth(),
                        spacingAfter = 10.dp
                    )
                    PlayerTopBar(
                        onSettingCLicked = {
                            viewModel.onEvent(PlayerEvents.OnSettingClicked)
                        },
                        showPremiumLabel = showPremiumLabel,
                        onPremiumClick = onPremiumClick,
                    )
                    ScreenNativeAd(
                        screenKey = VideoDownloaderAdScreenKeys.PLAYER,
                        position = AdSlotPosition.TOP,
                        modifier = Modifier.fillMaxWidth(),
                        spacingBefore = 10.dp
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                PlayerDefaultContent(
                    mainPaddingValues = mainPaddingValues,
                    state = state,
                    viewModel = viewModel,
                    sendToMedia = { mediaList, startIndex ->

                        viewModel.onEvent(
                            PlayerEvents.SendToMedia(
                                mediaList = mediaList,
                                startIndex = startIndex
                            )
                        )

                    },
                    onMoreClick = {
                        mainViewModel.onEvent(MainEvents.OnMoreClicked(it))
                    }
                )

                Spacer(
                    modifier = Modifier.height(
                        mainPaddingValues.calculateBottomPadding() + 16.dp
                    )
                )
            }
        }


    }



}