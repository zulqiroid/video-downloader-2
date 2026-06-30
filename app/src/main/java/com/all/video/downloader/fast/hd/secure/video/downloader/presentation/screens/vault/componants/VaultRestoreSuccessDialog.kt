package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultRestoreSuccessDialog(
    onDoneClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDoneClick
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
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = "Restored Successfully",
                style = AppTextStyles.titleLarge,
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Your media has been restored and may now appear in Gallery or Music apps.",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(22.dp))

            AppGradientButton(
                text = "Done",
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}