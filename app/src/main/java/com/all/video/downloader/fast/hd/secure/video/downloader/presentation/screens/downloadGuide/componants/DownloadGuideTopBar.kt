package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun DownloadGuideTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                text = title,
                style = AppTextStyles.titleLarge,
                textAlign = TextAlign.Center,
             )

            Box(
                modifier = Modifier.size(24.dp)
            )
        }
    }
}