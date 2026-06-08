package com.video.downloader.presentation.screens.videoToMp3Result.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ResultTopBar(
    onBackClick: () -> Unit
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(46.dp)
                .clip(CircleShape)
                .background(AppColors.BorderLight)
                .hapticClickable(
                    hapticFeedback = hapticFeedback,
                    hapticType = AppHapticType.Click,
                    role = Role.Button,
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = AppColors.TextEnabled,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Video to MP3",
            style = AppTextStyles.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = AppColors.TextEnabled
        )
    }
}