package com.video.downloader.presentation.screens.home.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.common.componants.AppOutlinedButton
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFetchFailedDialog(
    onRetryClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onCancelClick
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 30.dp,
                    shape = AppShapes.extraLarge,
                    clip = false
                )
                .background(
                    color = AppColors.Background,
                    shape = AppShapes.extraLarge
                )
                .border(
                    width = 1.dp,
                    color = AppColors.BorderLight,
                    shape = AppShapes.extraLarge
                )
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = AppGradients.HighlightVertical,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.size(22.dp))

            Text(
                text = "Video Fetch Failed",
                style = AppTextStyles.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Something went wrong while fetching your video link.",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(24.dp))

            AppGradientButton(
                text = "Retry",
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(12.dp))

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