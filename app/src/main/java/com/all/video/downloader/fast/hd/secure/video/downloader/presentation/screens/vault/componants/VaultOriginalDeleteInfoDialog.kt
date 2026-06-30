package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultOriginalDeleteInfoDialog(
    fileName: String,
    onDeleteOriginalClick: () -> Unit,
    onKeepOriginalClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            // User should make a clear choice.
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
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
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = "Secure Your Vault File",
                style = AppTextStyles.titleLarge,
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = if (fileName.isBlank()) {
                    "Your file has been added to Vault, but the original file is still visible in your Gallery."
                } else {
                    "$fileName has been added to Vault, but the original file is still visible in your Gallery."
                },
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(18.dp))

            VaultInfoRow(
                icon = Icons.Default.DeleteOutline,
                title = "Delete original copy",
                message = "To make this media private, delete the original file from the Android confirmation dialog."
            )

            Spacer(modifier = Modifier.size(12.dp))

            VaultWarningBox()

            Spacer(modifier = Modifier.size(22.dp))

            AppGradientButton(
                text = "Delete Original",
                onClick = onDeleteOriginalClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(10.dp))

            AppOutlinedButton(
                text = "Keep in Gallery",
                onClick = onKeepOriginalClick,
                modifier = Modifier.fillMaxWidth(),
                borderBrush = AppGradients.HighlightVertical,
                textBrush = AppGradients.HighlightVertical
            )
        }
    }
}

@Composable
private fun VaultInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(AppColors.BackgroundDisabled)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AppColors.HighlightGradientTop.copy(alpha = 0.12f)),
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
                style = AppTextStyles.titleSmall,
                color = AppColors.TextEnabled
            )

            Spacer(modifier = Modifier.size(3.dp))

            Text(
                text = message,
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                )
            )
        }
    }
}

@Composable
private fun VaultWarningBox() {
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = AppColors.Error,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Important: Vault files are stored securely inside this app. If you uninstall the app or clear app storage without restoring/exporting your Vault media first, those secured files may be permanently lost.",
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.Error
            )
        )
    }
}