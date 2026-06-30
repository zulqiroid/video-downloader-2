package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.componants

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles
import java.io.File
import java.util.Locale

@Composable
fun PlayerMediaActionsDialog(
    isVisible: Boolean,
    mediaFile: MediaItem?,
    onDismiss: () -> Unit,
    onRenameClick: () -> Unit,
    onMoveToVaultClick: () -> Unit,
    onShareClick: () -> Unit,
    onFileInfoClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var latestMediaFile by remember {
        mutableStateOf<MediaItem?>(null)
    }

    LaunchedEffect(mediaFile) {
        if (mediaFile != null) {
            latestMediaFile = mediaFile
        }
    }

    AnimatedVisibility(
        visible = isVisible && latestMediaFile != null,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 220,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 180,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        val scrimInteractionSource = remember { MutableInteractionSource() }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.24f))
                .clickable(
                    interactionSource = scrimInteractionSource,
                    indication = null,
                    onClick = onDismiss
                )
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            latestMediaFile?.let { currentMediaFile ->
                PlayerMediaActionsCard(
                    mediaFile = currentMediaFile,
                    onRenameClick = onRenameClick,
                    onMoveToVaultClick = onMoveToVaultClick,
                    onShareClick = onShareClick,
                    onFileInfoClick = onFileInfoClick,
                    onDeleteClick = onDeleteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateEnterExit(
                            enter = scaleIn(
                                initialScale = 0.92f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 240,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                            exit = scaleOut(
                                targetScale = 0.96f,
                                animationSpec = tween(
                                    durationMillis = 160,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeOut(
                                animationSpec = tween(
                                    durationMillis = 140,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun PlayerMediaActionsCard(
    mediaFile: MediaItem,
    onRenameClick: () -> Unit,
    onMoveToVaultClick: () -> Unit,
    onShareClick: () -> Unit,
    onFileInfoClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardInteractionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .heightIn(max = 560.dp)
            .background(
                color = AppColors.Background,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight.copy(alpha = 0.7f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = cardInteractionSource,
                indication = null,
                onClick = {}
            )
            .padding(horizontal = 28.dp, vertical = 28.dp)
    ) {
        PlayerMediaDialogHeader(
            mediaFile = mediaFile
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 22.dp),
            color = AppColors.BorderLight.copy(alpha = 0.8f)
        )

        PlayerMediaActionRow(
            titleRes = R.string.rename,
            subtitleRes = R.string.change_file_name,
            icon = R.drawable.ic_pen_filled,
            onClick = onRenameClick
        )

        PlayerMediaActionRow(
            titleRes = R.string.move_to_vault,
            subtitleRes = R.string.secure_with_pin,
            icon = R.drawable.ic_vault_filled,
            onClick = onMoveToVaultClick
        )

        PlayerMediaActionRow(
            titleRes = R.string.share,
            subtitleRes = R.string.send_via_message,
            icon = R.drawable.ic_share,
            onClick = onShareClick
        )

        PlayerMediaActionRow(
            titleRes = R.string.file_info,
            subtitleRes = R.string.check_file_properties,
            icon = R.drawable.ic_info_filled,
            onClick = onFileInfoClick
        )

        Spacer(modifier = Modifier.size(8.dp))

        PlayerMediaActionRow(
            titleRes = R.string.delete,
            subtitleRes = R.string.remove_file_permanently,
            icon = R.drawable.ic_bin_filled,
            isDeleteAction = true,
            onClick = onDeleteClick
        )
    }
}

@Composable
private fun PlayerMediaDialogHeader(
    mediaFile: MediaItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(
                    color = AppColors.DisabledContainer,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (mediaFile.mediaType == MediaType.VIDEO) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = AppColors.TextDisabled,
                    modifier = Modifier
                        .size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_single_music_node_filled),
                    contentDescription = null,
                    tint = AppColors.TextDisabled,
                    modifier = Modifier
                        .size(24.dp)
                )
            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = mediaFile.fileName,
                style = AppTextStyles.titleLarge.copy(
                    color = AppColors.TextEnabled,
                    fontWeight = FontWeight.ExtraBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "${mediaFile.formattedSize} • ${mediaFile.mimeType}",
                style = AppTextStyles.bodyMedium,
                color = AppColors.TextDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PlayerMediaActionRow(
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDeleteAction: Boolean = false,
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp)
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = if (isDeleteAction) {
                    AppHapticType.Reject
                } else {
                    AppHapticType.Click
                },
                role = Role.Button,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerMediaActionIconContainer(
            icon = icon,
            isDeleteAction = isDeleteAction
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = titleRes),
                style = AppTextStyles.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isDeleteAction) {
                    AppColors.Error
                } else {
                    AppColors.TextEnabled
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(2.dp))

            Text(
                text = stringResource(id = subtitleRes),
                style = AppTextStyles.bodyMedium,
                color = if (isDeleteAction) {
                    AppColors.Error.copy(alpha = 0.78f)
                } else {
                    AppColors.TextDisabled
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PlayerMediaActionIconContainer(
    icon: Int,
    isDeleteAction: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = AppShapes.large

    Box(
        modifier = modifier
            .size(52.dp)
            .then(
                if (isDeleteAction) {
                    Modifier
                        .background(
                            color = AppColors.Background,
                            shape = shape
                        )
                        .border(
                            width = 1.dp,
                            color = AppColors.BorderLight,
                            shape = shape
                        )
                } else {
                    Modifier.background(
                        brush = AppGradients.HighlightVertical,
                        shape = shape
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (isDeleteAction) {
                AppColors.Error
            } else {
                AppColors.OnHighlight
            },
            modifier = Modifier.size(25.dp)
        )
    }
}

private fun MediaFile.buildMediaDescription(): String {
    val sizeText = formatFileSize(
        bytes = runCatching {
            File(filePath).length()
        }.getOrDefault(0L)
    )

    val extension = File(fileName)
        .extension
        .ifBlank {
            if (isVideo) "mp4" else "audio"
        }
        .uppercase(Locale.ROOT)

    val typeText = if (isVideo) {
        "$extension Video"
    } else {
        "$extension Audio"
    }

    return "$sizeText • $typeText"
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "Unknown size"

    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1.0 -> String.format(Locale.ROOT, "%.1f GB", gb)
        mb >= 1.0 -> String.format(Locale.ROOT, "%.0f MB", mb)
        kb >= 1.0 -> String.format(Locale.ROOT, "%.0f KB", kb)
        else -> "$bytes B"
    }
}