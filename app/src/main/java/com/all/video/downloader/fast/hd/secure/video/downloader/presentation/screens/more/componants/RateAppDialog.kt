package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.settings.DownloadLocationType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes

@Composable
fun RateAppDialog(
    onRateNowClick: () -> Unit,
    onLaterClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            color = AppColors.Background,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(AppShapes.large)
                        .background(AppGradients.HighlightVertical),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star_filled),
                        contentDescription = null,
                        tint = AppColors.OnHighlight,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Rate Us",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.TextEnabled,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Enjoying the app? Give us a rating!",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextDisabled,
                        textAlign = TextAlign.Center
                    )
                }

                AppGradientButton(
                    text = "Rate Now",
                    onClick = onRateNowClick,
                    modifier = Modifier.fillMaxWidth(),
                    buttonHeight = 46.dp
                )

                AppOutlinedButton(
                    text = "Later",
                    onClick = onLaterClick,
                    modifier = Modifier.fillMaxWidth(),
                    buttonHeight = 46.dp,
                    borderBrush = AppGradients.HighlightVertical,
                    textColor = AppColors.HighlightGradientBottom
                )
            }
        }
    }
}

@Composable
fun DownloadLocationDialog(
    selectedType: DownloadLocationType,
    isSdCardAvailable: Boolean,
    customFolderUri: String?,
    onOptionClick: (DownloadLocationType) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = AppColors.Background,
            shadowElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Choose Download\nLocation",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.TextEnabled,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Select where you want to save your files.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextDisabled,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DownloadLocationOptionItem(
                        title = "Internal Storage",
                        subtitle = "Movies/Video Downloader",
                        icon = R.drawable.ic_download_filled,
                        selected = selectedType == DownloadLocationType.INTERNAL_STORAGE,
                        enabled = true,
                        onClick = {
                            onOptionClick(DownloadLocationType.INTERNAL_STORAGE)
                        }
                    )

                    if (isSdCardAvailable) {
                        DownloadLocationOptionItem(
                            title = "SD Card",
                            subtitle = "Available",
                            icon = R.drawable.ic_files_outlined,
                            selected = selectedType == DownloadLocationType.SD_CARD,
                            enabled = true,
                            onClick = {
                                onOptionClick(DownloadLocationType.SD_CARD)
                            }
                        )
                    }

                    DownloadLocationOptionItem(
                        title = "Custom Folder",
                        subtitle = if (customFolderUri.isNullOrBlank()) {
                            "Browse storage"
                        } else {
                            "Folder selected"
                        },
                        icon = R.drawable.ic_folder_filled,
                        selected = selectedType == DownloadLocationType.CUSTOM_FOLDER,
                        enabled = true,
                        onClick = {
                            onOptionClick(DownloadLocationType.CUSTOM_FOLDER)
                        }
                    )
                }

                AppGradientButton(
                    text = "Save",
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth(),
                    buttonHeight = 50.dp
                )

                AppOutlinedButton(
                    text = "Cancel",
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    buttonHeight = 46.dp,
                    borderBrush = AppGradients.HighlightVertical,
                    textColor = AppColors.HighlightGradientBottom
                )
            }
        }
    }
}

@Composable
private fun DownloadLocationOptionItem(
    title: String,
    subtitle: String,
    icon: Int,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) {
        AppColors.HighlightGradientTop
    } else {
        AppColors.BorderLight
    }

    val iconBackground = if (selected) {
        AppGradients.HighlightVertical
    } else {
        AppGradients.dummy
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(AppColors.Background)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = AppShapes.large
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (selected) AppColors.OnHighlight else AppColors.TextDisabled,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextEnabled
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextDisabled
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}