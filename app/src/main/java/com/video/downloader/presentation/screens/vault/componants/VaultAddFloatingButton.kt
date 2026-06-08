package com.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients

@Composable
fun VaultAddFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Box(
        modifier = modifier
            .size(58.dp)
            .shadow(
                elevation = 18.dp,
                shape = CircleShape,
                ambientColor = AppColors.HighlightGradientTop.copy(alpha = 0.45f),
                spotColor = AppColors.HighlightGradientBottom.copy(alpha = 0.45f)
            )
            .clip(CircleShape)
            .background(AppGradients.HighlightVertical)
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = AppHapticType.Confirm,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_folder_filled),
            contentDescription = null,
            tint = AppColors.OnHighlight,
            modifier = Modifier
                .padding(14.dp)
                .size(30.dp)
        )
    }
}