package com.video.downloader.presentation.screens.videoToMp3.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun PresetModeCard(
    preset: VideoToMp3Preset,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (selected) Color(0xFFFFF2FC) else Color(0xFFF5F3F8)
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) {
                    AppColors.HighlightGradientTop
                } else {
                    Color(0xFFE8E5EF)
                },
                shape = RoundedCornerShape(18.dp)
            )
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                enabled = enabled,
                hapticType = if (selected) AppHapticType.Tick else AppHapticType.ToggleOn,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (selected) AppGradients.HighlightVertical else AppGradients.dummy
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = preset.icon(),
                contentDescription = null,
                tint = if (selected) AppColors.OnHighlight else Color(0xFFB8B3C4),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = preset.titleText(),
                style = AppTextStyles.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = AppColors.TextEnabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = preset.subtitleText(),
                style = AppTextStyles.caption.copy(
                    color = Color(0xFFA3A0AD)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (selected) {
                        AppColors.HighlightGradientTop
                    } else {
                        Color(0xFFE1DEEA)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(AppColors.HighlightGradientTop)
                )
            }
        }
    }
}