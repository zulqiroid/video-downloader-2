package com.video.downloader.presentation.screens.screenCasting.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCastingTroubleshootingDialog(
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppColors.Background,
                    shape = AppShapes.extraLarge
                )
                .border(
                    width = 1.dp,
                    color = AppColors.BorderLight,
                    shape = AppShapes.extraLarge
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .background(
                        brush = AppGradients.HighlightVertical,
                        shape = AppShapes.extraLarge
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = "Casting Help",
                style = AppTextStyles.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Try these steps if your TV does not appear in the Android cast panel.",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TroubleshootingItem(
                    icon = Icons.Default.Wifi,
                    title = "Same Wi-Fi",
                    description = "Keep your phone and TV connected to the same Wi-Fi network."
                )

                TroubleshootingItem(
                    icon = Icons.Default.Tv,
                    title = "TV Casting Support",
                    description = "Make sure your TV supports screen mirroring, wireless display, Miracast, or Cast."
                )

                TroubleshootingItem(
                    icon = Icons.Default.WifiTethering,
                    title = "Disable VPN / Private DNS",
                    description = "VPN or private network settings can sometimes hide local casting devices."
                )

                TroubleshootingItem(
                    icon = Icons.Default.PowerSettingsNew,
                    title = "Restart Devices",
                    description = "Restart Wi-Fi, TV, and phone if the TV is still not visible."
                )
            }

            Spacer(modifier = Modifier.size(22.dp))

            AppGradientButton(
                text = "Got It",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TroubleshootingItem(
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
                style = AppTextStyles.bodyMedium.copy(
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