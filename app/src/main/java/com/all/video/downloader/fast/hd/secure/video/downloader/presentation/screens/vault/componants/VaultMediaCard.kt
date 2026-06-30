package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.states.VaultMediaType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.states.VaultUiItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VaultFileCard(
    item: VaultUiItem,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = AppShapes.large,
                clip = false
            )
            .clip(AppShapes.large)
            .background(AppColors.Background)
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.large
            )
            .combinedClickable(
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongPress,
                onClickLabel = "Open secure media",
                onLongClickLabel = "Restore media"
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .clip(AppShapes.medium)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.HighlightGradientTop.copy(alpha = 0.18f),
                            AppColors.HighlightGradientBottom.copy(alpha = 0.30f)
                        )
                    )
                )
        ) {
            if (!item.thumbnailUri.isNullOrBlank()) {
                AsyncImage(
                    model = item.thumbnailUri,
                    contentDescription = item.title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = when (item.mediaType) {
                    VaultMediaType.VIDEO -> "Video"
                    VaultMediaType.AUDIO -> "Audio"
                },
                style = AppTextStyles.overline.copy(
                    color = AppColors.OnHighlight,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .clip(AppShapes.small)
                    .background(AppGradients.HighlightVertical)
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable {
                        onDeleteClick()
                    }
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = item.title,
            style = AppTextStyles.caption.copy(
                color = AppColors.TextEnabled,
                fontWeight = FontWeight.ExtraBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.size(3.dp))

        Text(
            text = item.metaText,
            style = AppTextStyles.caption.copy(
                color = AppColors.TextDisabled,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}