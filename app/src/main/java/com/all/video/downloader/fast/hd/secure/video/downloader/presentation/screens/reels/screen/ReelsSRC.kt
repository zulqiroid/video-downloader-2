package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.google.android.material.loadingindicator.LoadingIndicator
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.componants.ReelItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.componants.ReelsTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events.ReelsEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.states.ReelsStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.viewModel.ReelsViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles
import kotlin.collections.get
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ReelsSRC(
    mainPaddingValues: PaddingValues,
    state: ReelsStates,
    viewModel: ReelsViewModel,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
) {

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        pageCount = { state.reels.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                viewModel.onEvent(
                    ReelsEvents.OnPageChanged(page)
                )
            }
    }

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
                    screenKey = VideoDownloaderAdScreenKeys.REELS,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                ReelsTopBar(
                    onSettingCLicked = {
                        viewModel.onEvent(ReelsEvents.OnSettingCLicked)
                    },
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick
                )

                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.REELS,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )

            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AppColors.HighlightGradientTop
                    )
                }
                return@Box
            }
            if (state.reels.isEmpty()) {
                EmptyReelList(
                    paddingValues = paddingValues,
                    mainPaddingValues = mainPaddingValues,
                    errorMessage = state.errorMessage,
                    onRefresh = {
                        viewModel.onEvent(ReelsEvents.LoadReels)
                    }
                )
                return@Box
            }


            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = mainPaddingValues.calculateBottomPadding()
                ),
            ) { page ->
                val reel = state.reels[page]

                ReelItem(
                    reel = reel,
                    isActive = page == state.currentIndex,
                    onLikeClick = {
                        viewModel.onEvent(
                            ReelsEvents.OnLikeClicked(reel.id)
                        )
                    },
                    onShareClick = {
                        viewModel.onEvent(
                            ReelsEvents.OnShareClicked(reel.id)
                        )
                    },
                    onDownloadClick = {
                        viewModel.onEvent(
                            ReelsEvents.OnDownloadClicked(reel.id)
                        )
                    }
                )
            }
        }

    }

}

@Composable
 fun EmptyReelList(
    paddingValues: PaddingValues,
    mainPaddingValues: PaddingValues,
    errorMessage: String?,
    onRefresh: () -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = mainPaddingValues.calculateBottomPadding()
            )
            .clip(
                shape = AppShapes.large
            )
            .background(
                color = AppColors.TextDisabled.copy(alpha = 0.5f)
            )
            .padding(horizontal = 30.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppColors.TextDisabled.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_fallen_media),
                    contentDescription = null,
                    tint = AppColors.TextDisabled,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(34.dp)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(R.string.no_trending_reels_available),
                style = AppTextStyles.titleLarge
            )
            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = errorMessage
                    ?: stringResource(R.string.check_back_later_for_the_latest_viral_shorts_and_trending_content),
                style = AppTextStyles.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(24.dp))

            AppOutlinedButton(
                text = "Refresh",
                borderBrush = AppGradients.HighlightVertical,
                textBrush = AppGradients.HighlightVertical,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_recreate_outlined),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = onRefresh
            )

        }

    }
}

