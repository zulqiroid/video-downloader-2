package com.video.downloader.presentation.screens.screenCasting.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ScreenCastingGuidelinesCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppColors.Background,
                shape = AppShapes.large
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.large
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Before You Start",
            style = AppTextStyles.titleMedium,
            color = AppColors.TextEnabled
        )

        ScreenCastingGuidelineItem(
            icon = Icons.Default.Wifi,
            title = "Same Wi-Fi Network",
            description = "Connect your phone and TV to the same Wi-Fi network."
        )

        ScreenCastingGuidelineItem(
            icon = Icons.Default.BatteryChargingFull,
            title = "Battery Usage",
            description = "Screen mirroring can use more battery while casting."
        )

        ScreenCastingGuidelineItem(
            icon = Icons.Default.Security,
            title = "System Controlled",
            description = "Final device selection and mirroring are handled by Android."
        )
    }
}

@Composable
private fun ScreenCastingGuidelineItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = AppTextStyles.bodyMedium,
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