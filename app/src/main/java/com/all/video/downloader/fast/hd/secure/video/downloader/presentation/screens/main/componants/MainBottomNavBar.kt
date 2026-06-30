package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.componants

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.BottomNavItems
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.MainStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import java.util.Locale

@Composable
fun MainBottomNavBar(
    state: MainStates,
    onTabSelected: (BottomNavItems) -> Unit,
    modifier: Modifier = Modifier,
){
    val hapticFeedback = rememberAppHapticFeedback()

     Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(50.dp),
                clip = false
            )
            .clip(RoundedCornerShape(50.dp))
            .background(
                color = AppColors.Background,
                shape = RoundedCornerShape(50.dp)
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(5.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            state.totalTabs.forEach { item ->
                val selected = state.selectedTab == item
                when (item) {
                    BottomNavItems.Reels -> {
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .then(
                                    if (selected) {
                                        Modifier.dropShadow(
                                            shape = CircleShape,
                                            shadow = Shadow(
                                                radius = 20.dp,
                                                spread = 0.3.dp,
                                                color = AppColors.HighlightGradientTop,
                                                offset = DpOffset(0.dp, 0.dp)
                                            )
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clip(CircleShape)
                                .hapticClickable(
                                    hapticFeedback = hapticFeedback,
                                    hapticType = if (selected) {
                                        AppHapticType.Tick
                                    } else {
                                        AppHapticType.ToggleOn
                                    }
                                ) {
                                    if (selected) return@hapticClickable
                                    onTabSelected(item)
                                 } ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(brush = AppGradients.HighlightVertical)
                                    .border(
                                        width = 2.dp,
                                        color = AppColors.Background,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_vd_cam_filled),
                                    contentDescription = null,
                                    tint = AppColors.Background,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .size(28.dp)
                                )

                            }

                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .weight(0.8f)
                                .hapticClickable(
                                    hapticFeedback = hapticFeedback,
                                    hapticType = if (selected) {
                                        AppHapticType.Tick
                                    } else {
                                        AppHapticType.ToggleOn
                                    }
                                ) {
                                    if (selected) return@hapticClickable
                                    onTabSelected(item)
                                 }
                                .padding(horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    if (selected) item.iconSelected else item.iconUnselected
                                ),
                                contentDescription = stringResource(item.title),
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.size(5.dp))

                            Text(
                                text = stringResource(item.title),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    brush = if (selected) AppGradients.HighlightVertical else Brush.verticalGradient(
                                        colors = listOf(
                                            AppColors.TextDisabled,
                                            AppColors.TextDisabled
                                        )
                                    )
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

            }
        }

    }

}