package com.video.downloader.presentation.screens.videoSplitter.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VideoSplitterTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = null,
            tint = AppColors.TextEnabled,
            modifier = Modifier
                .size(24.dp)
                .hapticClickable(
                    hapticFeedback = hapticFeedback,
                    hapticType = AppHapticType.Click,
                    role = Role.Button,
                    onClick = onBackClick
                )
        )

        Text(
            text = "Video Splitter",
            style = AppTextStyles.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = AppColors.TextEnabled
        )

        Box(
            modifier = Modifier.size(24.dp)
        )
    }
}