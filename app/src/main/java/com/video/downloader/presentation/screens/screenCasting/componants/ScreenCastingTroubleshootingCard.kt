package com.video.downloader.presentation.screens.screenCasting.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ScreenCastingTroubleshootingCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = modifier
            .background(
                color = AppColors.Background,
                shape = AppShapes.large
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.large
            )
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = AppHapticType.Click,
                role = Role.Button,
                onClick = onClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .background(
                    color = AppColors.DisabledContainer,
                    shape = AppShapes.medium
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier.size(24.dp)
            )
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "TV Not Showing?",
                style = AppTextStyles.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = AppColors.TextEnabled
            )

            Text(
                text = "Open troubleshooting tips for better casting discovery.",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                )
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = AppColors.TextDisabled,
            modifier = Modifier.size(24.dp)
        )
    }
}