package com.video.downloader.presentation.screens.screenCasting.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ScreenCastingNetworkCard(
    connected: Boolean,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = if (connected) {
        Icons.Default.Wifi
    } else {
        Icons.Default.WifiOff
    }

    Row(
        modifier = modifier
            .background(
                color = AppColors.BackgroundDisabled,
                shape = AppShapes.large
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.large
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .background(
                    brush = if (connected) {
                        AppGradients.HighlightVertical
                    } else {
                        AppGradients.dummy2
                    },
                    shape = CircleShape
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.OnHighlight,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = AppTextStyles.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = AppColors.TextEnabled
            )

            Text(
                text = description,
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                )
            )
        }
    }
}