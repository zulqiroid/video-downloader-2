package com.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.common.componants.AppOutlinedButton
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultRestoreConfirmDialog(
    fileName: String,
    onRestoreClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancelClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.extraLarge)
                .background(AppColors.Background)
                .border(
                    width = 1.dp,
                    color = AppColors.BorderLight,
                    shape = AppShapes.extraLarge
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(AppGradients.HighlightVertical),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = "Restore Media",
                style = AppTextStyles.titleLarge,
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = if (fileName.isBlank()) {
                    "This Vault file will be restored back to your public media folder."
                } else {
                    "$fileName will be restored back to your public media folder."
                },
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.large)
                    .background(AppColors.Error.copy(alpha = 0.08f))
                    .border(
                        width = 1.dp,
                        color = AppColors.Error.copy(alpha = 0.18f),
                        shape = AppShapes.large
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = AppColors.Error,
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    text = "After restore, this media may become visible in Gallery or Music apps. The encrypted Vault copy will remain safe unless you delete it manually.",
                    style = AppTextStyles.bodySmall.copy(
                        color = AppColors.Error
                    )
                )
            }

            Spacer(modifier = Modifier.size(22.dp))

            AppGradientButton(
                text = "Restore Now",
                onClick = onRestoreClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(10.dp))

            AppOutlinedButton(
                text = "Cancel",
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth(),
                borderBrush = AppGradients.HighlightVertical,
                textBrush = AppGradients.HighlightVertical
            )
        }
    }
}