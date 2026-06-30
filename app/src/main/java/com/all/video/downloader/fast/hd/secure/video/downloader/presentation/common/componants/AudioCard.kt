package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.PlayerUiItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun AudioCard(
    item: MediaItem,
    onClick: () -> Unit,
    onMoreClick: (MediaItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = AppShapes.large,
                clip = false
            )
            .background(Color.White, AppShapes.large)
            .border(
                color = AppColors.BorderLight,
                width = 1.dp,
                shape = AppShapes.large
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(90.dp)
                .clip(AppShapes.large)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.HighlightGradientTop.copy(alpha = 0.3f),
                            AppColors.HighlightGradientBottom.copy(alpha = 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_music_node_filled),
                contentDescription = "audio",
                tint = AppColors.Background,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .height(90.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.title,
                style = AppTextStyles.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "${item.formattedDuration} - ${item.formattedSize}",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AppColors.backgroundGradientTop.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_music_node_filled),
                        contentDescription = "more",
                        tint = AppColors.HighlightGradientBottom,
                        modifier = Modifier
                            .padding(7.dp)
                            .size(18.dp)
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "more",
            tint = Color(0xFF6B7280),
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    onMoreClick(item)
                }
        )
    }
}