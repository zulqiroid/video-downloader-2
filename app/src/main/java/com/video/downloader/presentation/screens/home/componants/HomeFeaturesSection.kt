package com.video.downloader.presentation.screens.home.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.screens.home.states.HomeFeatures
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles


@Composable
fun HomeFeaturesSection(
    features: List<HomeFeatures>,
    modifier: Modifier = Modifier,
    onClick: (HomeFeatures) -> Unit = {}
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        features.forEach {
            HomeFeatureItem(
                modifier = Modifier.weight(1f),
                feature = it,
                onClick = {
                    onClick(it)
                }
            )
        }

    }

}

@Composable
fun HomeFeatureItem(
    modifier: Modifier = Modifier,
    feature: HomeFeatures,
    onClick: () -> Unit = {}
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = AppShapes.large,
                clip = false
            )
            .clip(AppShapes.large)
            .background(
                color = AppColors.Background
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.large
            ).hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = AppHapticType.Click,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Box(
                modifier = Modifier
                    .clip(AppShapes.large)
                    .background(
                        color = feature.backgroundColor.copy(0.1f)
                    ),
                contentAlignment = Alignment.Center
             ){
                Icon(
                    painter = androidx.compose.ui.res.painterResource(feature.icon),
                    contentDescription = null,
                    tint = feature.backgroundColor,
                    modifier = Modifier.padding(15.dp).size(24.dp)
                )
            }

            Text(
                text =stringResource(feature.title),
                style = AppTextStyles.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}