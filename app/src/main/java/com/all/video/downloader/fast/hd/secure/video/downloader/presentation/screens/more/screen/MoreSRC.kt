package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.componants.MoreTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.componants.SettingsItemView
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events.MoreEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.MoreStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.settingsCategories
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.componants.DownloadLocationDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.componants.FeedbackDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.componants.RateAppDialog


@Composable
fun MoreSRC(onEvent: (MoreEvents) -> Unit, state: MoreStates) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.MORE,
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
                    MoreTopBar(
                        onBackClick = {
                            onEvent(MoreEvents.BackClicked)
                        }
                    )
                }
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.MORE,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth().navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.MORE,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 0.dp
                )
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.MORE,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 0.dp
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize() ,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {


                items(settingsCategories) { category ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() ,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(category.title),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AppColors.TextDisabled,
                                fontWeight = FontWeight.ExtraBold,
                            ),
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(AppShapes.large)
                                .background(AppColors.BackgroundDisabled)
                                .border(
                                    width = 1.dp,
                                    color = AppColors.BorderLight,
                                    shape =AppShapes.large,
                                ),
                        ) {
                            category.items.forEachIndexed { index, item ->
                                SettingsItemView(
                                    state = state,
                                    item = item,
                                    onClick = {
                                         onEvent(MoreEvents.OnSettingItemClicked(item))
                                    },
                                    isNotificationEnabled = state.isNotificationEnabled,
                                    onNotificationCheckedChange = {
                                        onEvent(MoreEvents.OnNotificationCheckedChange(it))
                                    }
                                )

                                if (index != category.items.lastIndex) {
                                    HorizontalDivider(
                                        color = AppColors.BorderLight,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showRateDialog) {
        RateAppDialog(
            onRateNowClick = {
                onEvent(MoreEvents.RateNowClicked)
            },
            onLaterClick = {
                onEvent(MoreEvents.RateLaterClicked)
            },
            onDismiss = {
                onEvent(MoreEvents.DismissRateDialog)
            }
        )
    }

    if (state.showDownloadLocationDialog) {
        DownloadLocationDialog(
            selectedType = state.pendingDownloadLocationType,
            isSdCardAvailable = state.isSdCardAvailable,
            customFolderUri = state.pendingCustomFolderUri,
            onOptionClick = { type ->
                onEvent(MoreEvents.OnDownloadLocationOptionSelected(type))
            },
            onSaveClick = {
                onEvent(MoreEvents.SaveDownloadLocationClicked)
            },
            onCancelClick = {
                onEvent(MoreEvents.DismissDownloadLocationDialog)
            },
            onDismiss = {
                onEvent(MoreEvents.DismissDownloadLocationDialog)
            }
        )
    }

    if (state.showFeedbackDialog) {
        FeedbackDialog(
            selectedCategory = state.selectedFeedbackCategory,
            message = state.feedbackMessage,
            error = state.feedbackError,
            isSubmitting = state.isSubmittingFeedback,
            onCategorySelected = { category ->
                onEvent(MoreEvents.OnFeedbackCategorySelected(category))
            },
            onMessageChanged = { message ->
                onEvent(MoreEvents.OnFeedbackMessageChanged(message))
            },
            onSubmitClick = {
                onEvent(MoreEvents.SubmitFeedbackClicked)
            },
            onCancelClick = {
                onEvent(MoreEvents.DismissFeedbackDialog)
            },
            onDismiss = {
                onEvent(MoreEvents.DismissFeedbackDialog)
            }
        )
    }

}