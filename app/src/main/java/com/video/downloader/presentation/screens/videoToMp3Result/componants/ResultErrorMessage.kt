package com.video.downloader.presentation.screens.videoToMp3Result.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ResultErrorMessage(
    message: String
) {
    Text(
        text = message,
        style = AppTextStyles.caption.copy(
            color = AppColors.Error,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(AppColors.Error.copy(alpha = 0.08f))
            .padding(12.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun ResultSavedMessage() {
    Text(
        text = "Saved in Music/VideoToAudio",
        style = AppTextStyles.caption.copy(
            color = AppColors.HighlightGradientTop,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(AppColors.HighlightGradientTop.copy(alpha = 0.08f))
            .padding(12.dp),
        textAlign = TextAlign.Center
    )
}