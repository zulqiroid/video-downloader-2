package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients.ReelWatchCardBG
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun HomeTrendingReelsCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = rememberAppHapticFeedback()
    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                clip = false
            )
            .clip(cardShape)
            .background(
                brush = ReelWatchCardBG,
                shape = cardShape
            )
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = AppHapticType.Click,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        DecorativeReelsCardBackground(
            modifier = Modifier.matchParentSize()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReelsPlayIconButton()

            Spacer(modifier = Modifier.size(15.dp))

           Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = AppTextStyles.titleMedium.copy(
                        color = AppColors.TextEnabled,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = subtitle,
                    style = AppTextStyles.bodySmall.copy(
                        color = AppColors.TextDisabled,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.size(5.dp))

            ReelsArrowButton()
        }
    }
}

@Composable
private fun DecorativeReelsCardBackground(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.22f),
            radius = size.height * 0.78f,
            center = Offset(
                x = size.width * 0.95f,
                y = size.height * -0.05f
            )
        )

        drawCircle(
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.18f),
            radius = size.height * 0.40f,
            center = Offset(
                x = size.width * 0.92f,
                y = size.height * 0.46f
            )
        )
    }
}

@Composable
private fun ReelsPlayIconButton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(15.dp)
                .clip(CircleShape)
                .background(AppGradients.HighlightVertical),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = AppColors.OnHighlight,
                modifier = Modifier.padding(10.dp).size(26.dp)
            )
        }
    }
}

@Composable
private fun ReelsArrowButton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.34f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = AppColors.HighlightGradientTop,
            modifier = Modifier.padding(10.dp).size(22.dp)
        )
    }
}

