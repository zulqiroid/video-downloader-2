package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
 import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VideoSplitterProgressDialog(
    progress: Int
) {
    Dialog(
        onDismissRequest = {}
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
                .padding(horizontal = 26.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Splitting Video",
                style = AppTextStyles.titleLarge,
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Creating your selected clip securely...",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(22.dp))

            LinearProgressIndicator(
                progress = {
                    progress.coerceIn(0, 100) / 100f
                },
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.HighlightGradientTop,
                trackColor = AppColors.BackgroundDisabled
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = "${progress.coerceIn(0, 100)}%",
                style = AppTextStyles.titleMedium.copy(
                    color = AppColors.HighlightGradientTop
                )
            )
        }
    }
}