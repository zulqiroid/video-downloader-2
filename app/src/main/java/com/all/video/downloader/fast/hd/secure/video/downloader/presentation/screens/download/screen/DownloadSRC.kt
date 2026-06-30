package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.screen

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
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants.DownloadConfirmationDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants.DownloadFileInfoDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants.DownloadFilesTabs
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants.DownloadListContent
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants.DownloadOptionsDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants.DownloadTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.events.DownloadEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.currentTabItems
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.viewModel.DownloadViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainEvents

@Composable
fun DownloadSRC(
    mainViewModel: MainViewModel,
    mainPaddingValues: PaddingValues,
    state: DownloadStates,
    viewModel: DownloadViewModel,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
) {
    val list = state.currentTabItems()

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

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
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.FILES,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                DownloadTopBar(
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick,
                    onSettingCLicked = {
                        viewModel.onEvents(DownloadEvents.OnSettingClicked)
                    }
                )
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.FILES,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                    sendToMedia = { _, _ -> },
                    onMoreClick = { item ->
                        interstitialAdGate?.showForFeature(
                            activity = activity,
                            featureKey = VideoDownloaderAdFeatureKeys.DOWNLOAD_OPTIONS_OPEN,
                            onComplete = {
                                viewModel.onEvents(
                                    DownloadEvents.OnDownloadMoreClicked(item)
                                )
                            }
                        ) ?: viewModel.onEvents(
                            DownloadEvents.OnDownloadMoreClicked(item)
                        )
                    }
                )
            }
        }
    }

    state.selectedDownloadItem?.let { item ->
        DownloadOptionsDialog(
            item = item,
            onDismiss = {
                viewModel.onEvents(DownloadEvents.OnDownloadMenuDismissed)
            },
            onPause = { id ->
                viewModel.onEvents(DownloadEvents.OnPauseDownloadingClicked(id))
            },
            onResume = { id ->
                viewModel.onEvents(DownloadEvents.OnResumeDownloadingClicked(id))
            },
            onCancel = {
                viewModel.onEvents(
                    DownloadEvents.OnCancelDownloadRequested(item)
                )
            },
            onDelete = {
                viewModel.onEvents(
                    DownloadEvents.OnDeleteFileRequested(item)
                )
            },
            onFileInfo = { selectedItem ->
                viewModel.onEvents(DownloadEvents.OnFileInfoClicked(selectedItem))
            },
            onRename = { selectedItem ->
                viewModel.onEvents(DownloadEvents.OnDownloadMenuDismissed)
                mainViewModel.onEvent(
                    MainEvents.OnRenameMediaRequested(selectedItem)
                )
            },
            onMoveToVault = { selectedItem ->
                viewModel.onEvents(DownloadEvents.OnDownloadMenuDismissed)
                mainViewModel.onEvent(
                    MainEvents.OnMoveMediaToVaultRequested(selectedItem)
                )
            },
            onShare = { selectedItem ->
                viewModel.onEvents(DownloadEvents.OnDownloadMenuDismissed)
                mainViewModel.onEvent(
                    MainEvents.OnShareMediaRequested(selectedItem)
                )
            }
        )
    }

    state.fileInfoItem?.let { item ->
        DownloadFileInfoDialog(
            item = item,
            onDismiss = {
                viewModel.onEvents(DownloadEvents.OnFileInfoDismissed)
            }
        )
    }

    state.confirmation?.let { confirmation ->
        DownloadConfirmationDialog(
            confirmation = confirmation,
            onDismiss = {
                viewModel.onEvents(DownloadEvents.OnConfirmActionDismissed)
            },
            onConfirm = {
                viewModel.onEvents(DownloadEvents.OnConfirmActionClicked)
            }
        )
    }
}