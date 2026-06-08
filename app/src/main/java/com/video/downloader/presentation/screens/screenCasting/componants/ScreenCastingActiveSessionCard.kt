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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.common.componants.AppOutlinedButton
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ScreenCastingActiveSessionCard(
    onManageClick: () -> Unit,
    onMarkStoppedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppColors.Background,
                shape = AppShapes.extraLarge
            )
            .border(
                width = 1.dp,
                brush = AppGradients.HighlightVertical,
                shape = AppShapes.extraLarge
            )
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .background(
                        brush = AppGradients.HighlightVertical,
                        shape = CircleShape
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CastConnected,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Active Casting Session",
                    style = AppTextStyles.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = AppColors.TextEnabled
                )

                Text(
                    text = "Your screen is marked as casting. Use Android cast panel to manage or stop it.",
                    style = AppTextStyles.bodySmall.copy(
                        color = AppColors.TextDisabled
                    )
                )
            }
        }

        Spacer(modifier = Modifier.size(18.dp))

        AppGradientButton(
            text = "Manage / Stop Casting",
            onClick = onManageClick,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.StopScreenShare,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(22.dp)
                )
            }
        )

        Spacer(modifier = Modifier.size(12.dp))

        AppOutlinedButton(
            text = "Mark as Stopped",
            onClick = onMarkStoppedClick,
            modifier = Modifier.fillMaxWidth(),
            borderBrush = AppGradients.HighlightVertical,
            textBrush = AppGradients.HighlightVertical
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "Use this only if casting has already stopped from the system panel or TV.",
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.TextDisabled
            ),
            textAlign = TextAlign.Center
        )
    }
}