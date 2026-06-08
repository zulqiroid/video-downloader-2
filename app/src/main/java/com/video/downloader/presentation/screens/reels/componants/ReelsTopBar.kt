package com.video.downloader.presentation.screens.reels.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles


@Composable
fun ReelsTopBar(
    onSettingCLicked: () -> Unit
) {
    val hapticFeedback = rememberAppHapticFeedback()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.trending_reels),
            style = AppTextStyles.titleLarge
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppGradients.PremiumChipBG)
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_crown_with_star),
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.pro),
                    style = AppTextStyles.bodySmall.copy(
                        color = AppColors.OnHighlight
                    )
                )
            }


            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = null,
                tint = AppColors.TextEnabled,
                modifier = Modifier
                    .size(32.dp)
                    .hapticClickable(
                        hapticFeedback = hapticFeedback,
                        hapticType = AppHapticType.Click,
                        role = Role.Button,
                        onClick = onSettingCLicked
                    )
            )

        }

    }
}