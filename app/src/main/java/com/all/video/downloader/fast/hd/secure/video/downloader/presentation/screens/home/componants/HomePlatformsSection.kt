package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.Platforms
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.states.HomePlatformUiModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun HomePlatformsSection(
    platforms: List<Platforms>,
    onPlatformClick: (Platforms) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.platforms).uppercase(),
            style = AppTextStyles.titleMedium,
         )

        Spacer(modifier = Modifier.size(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            maxItemsInEachRow = 4
        ) {
            platforms.forEach { platform ->
                HomePlatformItem(
                    platform = platform,
                    onClick = {
                        onPlatformClick(platform)
                    },
                    modifier = Modifier.widthIn(
                        min = 72.dp,
                        max = 92.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun HomePlatformItem(
    platform: Platforms,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Column(
        modifier = modifier.hapticClickable(
            hapticFeedback = hapticFeedback,
            hapticType = AppHapticType.Click,
            role = Role.Button,
            onClick = onClick
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlatformIconContainer(
            platform = platform
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(platform.title),
            style = AppTextStyles.caption.copy(
                color = AppColors.TextDisabled,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun PlatformIconContainer(
    platform: Platforms,
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(28.dp)
    val iconShape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape =AppShapes.large ,
                ambientColor = AppColors.HighlightGradientTop.copy(0.5f),
                spotColor =  AppColors.HighlightGradientTop.copy(0.5f)
            )
            .clip(AppShapes.large)
            .background(
                color = AppColors.Background,
                shape = AppShapes.large
            )
            .border(
                width = 1.5.dp,
                color = AppColors.HighlightGradientTop.copy(alpha = 0.22f),
                shape = AppShapes.large
            ),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(platform.icon),
            contentDescription = stringResource(platform.title),
            modifier = Modifier
                 .padding(15.dp)
                .size(36.dp)
        )

    }
}