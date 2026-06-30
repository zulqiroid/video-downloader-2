package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.CompletedOptionsFactory
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import java.util.Locale
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadConfirmationAction
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadConfirmationState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadOptionId
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadOptionItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.InProgressOptionsFactory
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun DownloadOptionsDialog(
    item: MediaItem,
    onDismiss: () -> Unit,
    onPause: (Long) -> Unit,
    onResume: (Long) -> Unit,
    onCancel: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onFileInfo: (MediaItem) -> Unit,
    onRename: (MediaItem) -> Unit,
    onMoveToVault: (MediaItem) -> Unit,
    onShare: (MediaItem) -> Unit,
) {
    val status = item.downloadStatus
    val isDownloading = status == DownloadStatus.DOWNLOADING
    val isPaused = status == DownloadStatus.PAUSED
    val isFailed = status == DownloadStatus.FAILED
    val canResume = isPaused || isFailed
    val isCompleted = status == DownloadStatus.SUCCESS || item.isDownloaded

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Background, AppShapes.large)
                .padding(22.dp)
        ) {
            DownloadDialogHeader(item = item)

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(
                color = AppColors.BorderLight, thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(18.dp))

            if (isCompleted) {
                DownloadedOptions(
                    onItemClick = {
                        when (it) {
                            DownloadOptionId.Delete -> {
                                onDelete(item.id)
                            }

                            DownloadOptionId.FileInfo -> {
                                onFileInfo(item)
                            }

                            DownloadOptionId.MoveToVault -> {
                                onMoveToVault(item)
                            }

                            DownloadOptionId.Rename -> {
                                onRename(item)
                            }

                            DownloadOptionId.Share -> {
                                onShare(item)
                            }

                            else -> Unit
                        }
                    }
                )
            } else {
                InProgressOptions(
                    status = status,
                    onItemClick = {
                        when (it) {
                            DownloadOptionId.Cancel -> {
                                onCancel(item.id)
                            }

                            DownloadOptionId.Delete -> {
                                onDelete(item.id)
                            }

                            DownloadOptionId.FileInfo -> {
                                onFileInfo(item)
                            }

                            DownloadOptionId.MoveToVault -> {

                            }

                            DownloadOptionId.Pause -> {
                                onPause(item.id)
                            }

                            DownloadOptionId.Resume -> {
                                onResume(item.id)
                            }

                            DownloadOptionId.Retry -> {
                                onPause(item.id)
                            }

                            else -> Unit
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DownloadFileInfoDialog(
    item: MediaItem,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = AppColors.Background,
        onDismissRequest = onDismiss,
        confirmButton = {
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "File info", fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                FileInfoLine(label = "Name", value = item.fileName.ifBlank { item.title })
                 FileInfoLine(label = "Size", value = item.formattedSize.ifBlank { "Unknown" })
                 FileInfoLine(label = "Duration", value = item.formattedDuration.ifBlank { "Unknown" })
                FileInfoLine(label = "Format", value = item.qualityLabel.ifBlank { "Unknown" })
                FileInfoLine(label = "Date Added", value = item.formattedDate.ifBlank { "Not saved yet" })
            }
        })
}

@Composable
private fun DownloadDialogHeader(item: MediaItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .background(AppColors.BackgroundV2, AppShapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppColors.Background.copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = AppColors.BackgroundV2,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.fileName.ifBlank { item.title }, style = AppTextStyles.titleMedium.copy(
                    fontSize = 18.sp
                ), maxLines = 1, overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildHeaderSubtitle(item),
                style = AppTextStyles.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun FileInfoLine(
    label: String,
    value: String,
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = label,
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.TextDisabled
            )
        )

        Text(
            text = value,
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.TextEnabled,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

private fun buildHeaderSubtitle(item: MediaItem): String {
    val size = item.formattedSize.ifBlank { "Size unknown" }
    val quality = item.qualityLabel.ifBlank { "MP4 Video" }

    return "$size • $quality"
}


@Composable
fun DownloadConfirmationDialog(
    confirmation: DownloadConfirmationState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val item = confirmation.item

    val title = when (confirmation.action) {
        DownloadConfirmationAction.CANCEL_DOWNLOAD -> "Cancel download?"
        DownloadConfirmationAction.DELETE_FILE -> "Delete file?"
    }

    val message = when (confirmation.action) {
        DownloadConfirmationAction.CANCEL_DOWNLOAD -> {
            "Is download ko cancel karna hai? Partial downloaded file bhi remove ho jayegi."
        }

        DownloadConfirmationAction.DELETE_FILE -> {
            "Ye file/download permanently remove ho jayegi. Ye action undo nahi hoga."
        }
    }

    val confirmText = when (confirmation.action) {
        DownloadConfirmationAction.CANCEL_DOWNLOAD -> "Cancel Download"
        DownloadConfirmationAction.DELETE_FILE -> "Delete"
    }

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = title, fontWeight = FontWeight.Bold, color = Color(0xFF111827)
        )
    }, text = {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = item.fileName.ifBlank { item.title },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = message, fontSize = 13.sp, color = Color(0xFF64748B)
            )
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = "Keep")
        }
    }, confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(
                text = confirmText, color = Color(0xFFE00004), fontWeight = FontWeight.Bold
            )
        }
    })
}


@Composable
fun DownloadedOptions(
    onItemClick: (DownloadOptionId) -> Unit,
) {

    val items = remember {
        CompletedOptionsFactory.create()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {

        items(items) { item ->
            DownloadOptionItemView(
                item = item, onClick = {
                    onItemClick(item.id)
                }
            )
        }
    }

}

@Composable
fun InProgressOptions(
    status: DownloadStatus?,
    onItemClick: (DownloadOptionId) -> Unit,
) {

    val items = remember(status) {
        InProgressOptionsFactory.create(status!!)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {

        items(items) { item ->

            DownloadOptionItemView(
                item = item, onClick = {
                    onItemClick(item.id)
                })
        }
    }
}

@Composable
fun DownloadOptionItemView(
    item: DownloadOptionItem,
    onClick: () -> Unit,
) {

    val enable = item.enabled
    val destructive = item.isDestructive

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enable, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
    ) {
        Box(
            modifier = Modifier
                .clip(AppShapes.large)
                .shadow(
                    elevation = 10.dp,
                    shape = AppShapes.large,
                    ambientColor = AppColors.TextDisabled,
                    spotColor = AppColors.TextDisabled
                )
                .background(
                    brush = (if (!enable) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppColors.TextDisabled, AppColors.TextDisabled
                            )
                        )
                    } else if (destructive) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppColors.Background, AppColors.Background
                            )
                        )

                    } else {
                        AppGradients.HighlightVertical
                    })
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = if (destructive) AppColors.Error else AppColors.Background,
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(item.title),
                style = AppTextStyles.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (!enable) AppColors.TextDisabled else if (destructive) AppColors.Error else AppColors.TextEnabled
                ),
            )
            Text(
                text = stringResource(item.description),
                style = AppTextStyles.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    color = if (!enable) AppColors.TextDisabled else if (destructive) AppColors.Error else AppColors.TextEnabled
                ),
            )
        }

    }
}