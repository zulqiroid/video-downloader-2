package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants.HomeFeaturesSection
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants.HomeLinkCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants.HomePlatformsSection
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants.HomeTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants.HomeTrendingReelsCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants.NoInternetConnectionDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants.VideoFetchFailedDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.events.HomeEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.states.HomeStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients


@Composable
fun HomeSRC(
    state: HomeStates,
    onEvent: (HomeEvents) -> Unit,
    modifier: Modifier = Modifier,
    mainPaddingValues: PaddingValues,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.HOME,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                HomeTopBar(
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick,
                    onSettingCLicked = {
                        onEvent(HomeEvents.SettingClicked)
                    }
                )

                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.HOME,
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
                .padding(horizontal = 20.dp)
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            HomeLinkCard(
                videoUrl = state.videoUrl,
                isLoading = state.isDownloadActionLoading,
                error = state.error,
                onVideoUrlChanged = { value ->
                    onEvent(HomeEvents.VideoUrlChanged(value))
                },
                onPasteClick = {
                    onEvent(HomeEvents.PasteClicked(clipboardText = ""))
                },
                onDownloadClick = {
                    onEvent(HomeEvents.DownloadClicked)
                }
            )
            Spacer(modifier = Modifier.height(32.dp))

            HomeTrendingReelsCard(
                title = stringResource(R.string.watch_trending_reels),
                subtitle = stringResource(R.string.discover_the_latest_viral_shorts),
                onClick = {
                    onEvent(HomeEvents.WatchTrendingReelsClicked)
                },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(32.dp))

            HomePlatformsSection(
                platforms = state.platforms,
                onPlatformClick = { platform ->
                    onEvent(
                        HomeEvents.PlatformClicked(
                            platform = platform
                        )
                    )
                },
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.how_to_download_videos),
                onClick = {
                    onEvent(HomeEvents.HowToDownloadVideosClicked)
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_help),
                        contentDescription = stringResource(R.string.how_to_download_videos),
                        modifier = Modifier.size(24.dp)
                    )
                },
                borderBrush = AppGradients.HighlightVertical,
                textBrush = AppGradients.HighlightVertical
            )

            Spacer(modifier = Modifier.height(32.dp))


            HomeFeaturesSection(
                features = state.features,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onEvent(HomeEvents.FeatureClicked(it))
                }
            )


            Spacer(
                modifier = Modifier.height(mainPaddingValues.calculateBottomPadding() + 10.dp)
            )
        }
    }

    if (state.showNoInternetDialog) {
        NoInternetConnectionDialog(
            onRetryClick = {
                onEvent(HomeEvents.NoInternetRetryClicked)
            },
            onCancelClick = {
                onEvent(HomeEvents.NoInternetCancelClicked)
            }
        )
    }

    if (state.showVideoFetchFailedDialog) {
        VideoFetchFailedDialog(
            onRetryClick = {
                onEvent(HomeEvents.VideoFetchRetryClicked)
            },
            onCancelClick = {
                onEvent(HomeEvents.VideoFetchCancelClicked)
            }
        )
    }

}

