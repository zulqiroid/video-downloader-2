package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.componants

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
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VideoToMp3ErrorText(
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