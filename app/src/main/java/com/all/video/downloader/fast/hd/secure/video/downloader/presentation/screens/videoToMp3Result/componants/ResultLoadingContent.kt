package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3Result.componants

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors

@Composable
fun ResultLoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = AppColors.HighlightGradientTop
        )
    }
}