package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.platformDetail.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.platformDetail.states.TrendingVideoUiModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun TrendingVideoCard(
    video: TrendingVideoUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Column(
        modifier = modifier.hapticClickable(
            hapticFeedback = hapticFeedback,
            hapticType = AppHapticType.Click,
            role = Role.Button,
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.76f)
                .background(
                    brush = Brush.verticalGradient(video.thumbnailColors),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(7.dp)
        ) {
            Text(
                text = "▶ ${video.views}",
                style = AppTextStyles.bodyMedium,
                color = AppColors.OnHighlight,
                modifier = Modifier.align(Alignment.BottomStart)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp)
                    .background(
                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.45f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                PlayIcon(
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(7.dp))

        Text(
            text = video.title,
            style = AppTextStyles.bodySmall,
            color = AppColors.TextEnabled,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PlayIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.25f, size.height * 0.15f)
            lineTo(size.width * 0.25f, size.height * 0.85f)
            lineTo(size.width * 0.82f, size.height * 0.50f)
            close()
        }

        drawPath(
            path = path,
            color = AppColors.OnHighlight
        )
    }
}