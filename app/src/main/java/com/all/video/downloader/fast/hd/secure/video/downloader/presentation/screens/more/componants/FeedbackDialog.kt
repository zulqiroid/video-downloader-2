package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.FeedbackCategory
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes

@Composable
fun FeedbackDialog(
    selectedCategory: FeedbackCategory,
    message: String,
    error: String?,
    isSubmitting: Boolean,
    onCategorySelected: (FeedbackCategory) -> Unit,
    onMessageChanged: (String) -> Unit,
    onSubmitClick: () -> Unit,
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
                    .padding(horizontal = 22.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                FeedbackHeader()

                FeedbackCategoryGrid(
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = onMessageChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp),
                    placeholder = {
                        Text(
                            text = "Describe your issue or suggestion...",
                            color = AppColors.TextDisabled,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    isError = error != null,
                    supportingText = {
                        error?.let { message ->
                            Text(
                                text = message,
                                color = AppColors.Error
                            )
                        }
                    },
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.HighlightGradientTop,
                        unfocusedBorderColor = AppColors.BorderLight,
                        errorBorderColor = AppColors.Error,
                        cursorColor = AppColors.HighlightGradientTop,
                        focusedTextColor = AppColors.TextEnabled,
                        unfocusedTextColor = AppColors.TextEnabled,
                        focusedContainerColor = AppColors.Background,
                        unfocusedContainerColor = AppColors.Background
                    ),
                    shape = AppShapes.large
                )

                AppGradientButton(
                    text = "Submit Feedback",
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = message.isNotBlank() && !isSubmitting,
                    isLoading = isSubmitting,
                    buttonHeight = 50.dp
                )

                AppOutlinedButton(
                    text = "Cancel",
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting,
                    buttonHeight = 46.dp,
                    borderBrush = AppGradients.HighlightVertical,
                    textColor = AppColors.HighlightGradientBottom
                )
            }
        }
    }
}

@Composable
private fun FeedbackHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(AppShapes.large)
                .background(AppGradients.HighlightVertical),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_pen_filled),
                contentDescription = null,
                tint = AppColors.OnHighlight,
                modifier = Modifier.size(26.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Send Feedback",
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Help us improve by sharing your experience.",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextDisabled,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeedbackCategoryGrid(
    selectedCategory: FeedbackCategory,
    onCategorySelected: (FeedbackCategory) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FeedbackCategory.entries
            .chunked(2)
            .forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowCategories.forEach { category ->
                        FeedbackCategoryChip(
                            category = category,
                            selected = category == selectedCategory,
                            onClick = {
                                onCategorySelected(category)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (rowCategories.size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
    }
}

@Composable
private fun FeedbackCategoryChip(
    category: FeedbackCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)

    val backgroundBrush = if (selected) {
        AppGradients.HighlightVertical
    } else {
        Brush.verticalGradient(
            colors = listOf(
                AppColors.Background,
                AppColors.Background
            )
        )
    }

    val borderColor = if (selected) {
        AppColors.HighlightGradientTop
    } else {
        AppColors.BorderLight
    }

    Box(
        modifier = modifier
            .height(38.dp)
            .clip(shape)
            .background(
                brush = backgroundBrush,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                AppColors.OnHighlight
            } else {
                AppColors.TextEnabled
            },
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}