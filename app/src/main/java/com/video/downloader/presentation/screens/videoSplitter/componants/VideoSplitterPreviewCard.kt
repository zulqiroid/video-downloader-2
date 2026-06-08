package com.video.downloader.presentation.screens.videoSplitter.componants

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes

@Composable
fun VideoSplitterPreviewCard(
    videoUri: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.extraLarge)
            .background(AppColors.BackgroundDisabled)
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.extraLarge
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.05f)
                .clip(AppShapes.large)
                .background(AppColors.TextDisabled.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = Uri.parse(videoUri),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.62f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AppColors.TextDisabled.copy(alpha = 0.45f))
                        .padding(12.dp)
                        .size(24.dp)
                )
            }
        }
    }
}