package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoSplitter.VideoSplitClip
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles
import java.util.Locale

@Composable
fun VideoSplitterResultContent(
    clips: List<VideoSplitClip>,
    isSavingClip: Boolean,
    savingClipId: String?,
    saveSuccessMessage: String?,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onSaveClipClick: (VideoSplitClip) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )

                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER_RESULT,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = AppColors.BackgroundDisabled,
                                shape = CircleShape
                            )
                            .hapticClickable(
                                hapticFeedback = hapticFeedback,
                                hapticType = AppHapticType.Click,
                                role = Role.Button,
                                onClick = onBackClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = null,
                            tint = AppColors.TextEnabled,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Video Splitter",
                        style = AppTextStyles.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = AppColors.TextEnabled
                    )

                    Box(
                        modifier = Modifier.size(42.dp)
                    )
                }

                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER_RESULT,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER_RESULT,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 0.dp
                )
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.VIDEO_SPLITTER_RESULT,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 0.dp
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.size(20.dp))

                Box(
                    modifier = Modifier
                        .size(118.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            ambientColor = AppColors.HighlightGradientTop.copy(alpha = 0.45f),
                            spotColor = AppColors.HighlightGradientTop.copy(alpha = 0.45f)
                        )
                        .background(
                            color = AppColors.Background,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = AppColors.HighlightGradientTop,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.size(26.dp))

                Text(
                    text = "Video Split Successful",
                    style = AppTextStyles.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = AppColors.TextEnabled,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Your video has been split into ${clips.size} clip${if (clips.size == 1) "" else "s"}.",
                    style = AppTextStyles.bodySmall.copy(
                        color = AppColors.TextDisabled
                    ),
                    textAlign = TextAlign.Center
                )

                saveSuccessMessage?.let { message ->
                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = message,
                        style = AppTextStyles.bodySmall.copy(
                            color = AppColors.HighlightGradientTop,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = message,
                        style = AppTextStyles.bodySmall.copy(
                            color = AppColors.Error,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.size(28.dp))
            }

            items(
                items = clips,
                key = { it.id }
            ) { clip ->
                VideoSplitResultClipCard(
                    clip = clip,
                    isSaving = isSavingClip && savingClipId == clip.id,
                    onSaveClick = {
                        onSaveClipClick(clip)
                    }
                )

                Spacer(modifier = Modifier.size(14.dp))
            }

            item {
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }
        }
    }
}

@Composable
private fun VideoSplitResultClipCard(
    clip: VideoSplitClip,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = AppShapes.extraLarge,
                clip = false
            )
            .background(
                color = AppColors.Background,
                shape = AppShapes.extraLarge
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.extraLarge
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = AppColors.TextDisabled.copy(alpha = 0.35f),
                    shape = AppShapes.large
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = AppColors.Background,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = clip.fileName,
                style = AppTextStyles.titleSmall,
                color = AppColors.TextEnabled,
                maxLines = 1
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "${formatVideoSplitterTime(clip.startMs)} - ${formatVideoSplitterTime(clip.endMs)} • ${
                    formatFileSize(
                        clip.sizeBytes
                    )
                }",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = if (clip.isSaved) {
                        AppColors.HighlightGradientTop.copy(alpha = 0.14f)
                    } else {
                        AppColors.BackgroundDisabled
                    },
                    shape = CircleShape
                )
                .hapticClickable(
                    hapticFeedback = hapticFeedback,
                    enabled = !isSaving && !clip.isSaved,
                    hapticType = AppHapticType.Confirm,
                    role = Role.Button,
                    onClick = onSaveClick
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isSaving -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = AppColors.HighlightGradientTop
                    )
                }

                clip.isSaved -> {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = AppColors.HighlightGradientTop,
                        modifier = Modifier.size(20.dp)
                    )
                }

                else -> {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = AppColors.TextDisabled,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun formatVideoSplitterTime(ms: Long): String {
    val totalSeconds = ms / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L

    return if (hours > 0L) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "0 B"

    val kb = 1024.0
    val mb = kb * 1024.0
    val gb = mb * 1024.0

    return when {
        bytes >= gb -> String.format(Locale.US, "%.2f GB", bytes / gb)
        bytes >= mb -> String.format(Locale.US, "%.1f MB", bytes / mb)
        bytes >= kb -> String.format(Locale.US, "%.1f KB", bytes / kb)
        else -> "$bytes B"
    }
}