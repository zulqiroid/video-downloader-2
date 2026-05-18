package com.video.downloader.presentation.screens.more.screen

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
import com.video.downloader.presentation.screens.more.componants.MoreTopBar
import com.video.downloader.presentation.screens.more.componants.SettingsItemView
import com.video.downloader.presentation.screens.more.events.MoreEvents
import com.video.downloader.presentation.screens.more.states.MoreStates
import com.video.downloader.presentation.screens.more.states.settingsCategories
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes


@Composable
fun MoreSRC(onEvent: (MoreEvents) -> Unit, state: MoreStates) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                MoreTopBar(
                    onBackClick = {
                        onEvent(MoreEvents.BackClicked)
                    }
                )
            }
        }
    ) {paddingValues ->

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

}