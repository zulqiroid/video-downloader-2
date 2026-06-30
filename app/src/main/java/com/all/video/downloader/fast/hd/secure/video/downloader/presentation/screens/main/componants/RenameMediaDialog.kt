package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.componants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun RenameMediaDialog(
    item: MediaItem,
    value: String,
    error: String?,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = AppColors.Background,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Rename File",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppColors.TextEnabled
                )

                Text(
                    text = item.fileName.ifBlank { item.title },
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextDisabled,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = error != null,
                    label = {
                        Text(text = "File name")
                    },
                    supportingText = {
                        if (error != null) {
                            Text(text = error)
                        }
                    }
                )

                AppGradientButton(
                    text = if (isLoading) "Renaming..." else "Rename",
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    buttonHeight = 48.dp,
                    enabled = !isLoading
                )

                AppOutlinedButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    buttonHeight = 46.dp,
                    borderBrush = AppGradients.HighlightVertical,
                    textColor = AppColors.HighlightGradientBottom,
                    enabled = !isLoading
                )
            }
        }
    }
}

@Composable
fun DeleteMediaConfirmationDialog(
    item: MediaItem,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val targetName = remember(item.fileName, item.title) {
        item.fileName.ifBlank { item.title }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.76f)
                .widthIn(max = 280.dp)
                .semantics {
                    contentDescription = "Delete confirmation for $targetName"
                },
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DeleteDialogIcon()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Delete File",
                    color = AppColors.TextEnabled,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Are you sure you want to delete this file? This action cannot be undone.",
                    modifier = Modifier.widthIn(max = 205.dp),
                    color = AppColors.TextDisabled,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                DeleteDialogGradientButton(
                    text = if (isLoading) "Deleting..." else "Delete",
                    isLoading = isLoading,
                    enabled = !isLoading,
                    onClick = onConfirm
                )

                Spacer(modifier = Modifier.height(8.dp))

                DeleteDialogOutlinedButton(
                    text = "Cancel",
                    enabled = !isLoading,
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
private fun DeleteDialogIcon() {
    Box(
        modifier = Modifier.size(62.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .blur(18.dp)
                .background(
                    color = AppColors.HighlightGradientBottom.copy(alpha = 0.28f),
                    shape = RoundedCornerShape(18.dp)
                )
        )

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = AppGradients.HighlightVertical,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = AppColors.HighlightGradientBottom,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DeleteDialogGradientButton(
    text: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(shape)
            .background(
                brush = AppGradients.HighlightVertical,
                shape = shape
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DeleteDialogOutlinedButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(shape)
            .background(
                color = Color.White,
                shape = shape
            )
            .border(
                width = 1.2.dp,
                brush = AppGradients.HighlightVertical,
                shape = shape
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = AppColors.HighlightGradientBottom,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}