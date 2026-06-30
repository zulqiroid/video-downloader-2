package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles
import java.io.File
import java.util.Locale

@Composable
fun DownloadingCard(
    item: MediaItem,
    onPauseClick: (Long) -> Unit,
    onResumeClick: (Long) -> Unit,
    onMoreClick: (MediaItem) -> Unit,
) {
    val status = item.downloadStatus
    val isPaused = status == DownloadStatus.PAUSED
    val isFailed = status == DownloadStatus.FAILED
    val progress = item.progress.coerceIn(0, 100)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(
                color = Color(0xFFE5E7EB),
                width = 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoThumbnail(
            path = item.filePath.takeIf { path -> File(path).exists() },
            width = 78,
            height = 62,
            overlayIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title.ifBlank { item.fileName },
                    style = AppTextStyles.titleMedium.copy(fontSize = 13.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Rounded.MoreHoriz,
                    contentDescription = "Download options",
                    tint = AppColors.TextDisabled,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onMoreClick(item) }
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = buildDownloadSubtitle(item),
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(50)),
                    color = AppColors.backgroundGradientTop,
                    trackColor = Color(0xFFF3F4F6),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$progress%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.backgroundGradientTop
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DownloadStatusChip(
                    text = when {
                        isPaused -> "Paused"
                        isFailed -> "Failed"
                        else -> "Downloading"
                    },
                    isError = isFailed
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = buildBottomInfo(item),
                    fontSize = 10.sp,
                    color = Color(0xFF71717A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(
                            color = Color(0xFFE5E7EB),
                            width = 1.dp,
                            shape = CircleShape
                        )
                        .clickable {
                            if (isPaused || isFailed) {
                                onResumeClick(item.id)
                            } else {
                                onPauseClick(item.id)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPaused || isFailed) {
                            Icons.Rounded.PlayArrow
                        } else {
                            Icons.Rounded.Pause
                        },
                        contentDescription = when {
                            isFailed -> "Retry download"
                            isPaused -> "Resume download"
                            else -> "Pause download"
                        },
                        tint = Color(0xFF52525B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadStatusChip(
    text: String,
    isError: Boolean,
) {
    val background = if (isError) {
        Color(0xFFE00004).copy(alpha = 0.10f)
    } else {
        AppColors.backgroundGradientTop.copy(alpha = 0.12f)
    }

    val color = if (isError) {
        Color(0xFFE00004)
    } else {
        AppColors.backgroundGradientTop
    }

    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

private fun buildDownloadSubtitle(item: MediaItem): String {
    val size = item.formattedSize.ifBlank { "Size unknown" }
    val quality = item.qualityLabel.ifBlank { "MP4 Video" }

    return "$size • $quality"
}

private fun buildBottomInfo(item: MediaItem): String {
    val speed = item.speedBytesPerSecond.toReadableSpeed()
    val eta = item.formattedEta.ifBlank { "Calculating" }

    return "$speed • $eta"
}

private fun Long.toReadableSpeed(): String {
    if (this <= 0L) return "0 KB/s"

    val kb = 1024.0
    val mb = kb * 1024.0

    return if (this >= mb) {
        String.format(Locale.US, "%.1f MB/s", this / mb)
    } else {
        String.format(Locale.US, "%.1f KB/s", this / kb)
    }
}