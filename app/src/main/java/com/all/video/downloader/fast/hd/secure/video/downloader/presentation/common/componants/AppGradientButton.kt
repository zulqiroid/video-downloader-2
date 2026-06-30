package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.LoadingHapticEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun AppGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    buttonHeight: Dp = 55.dp,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    hapticType: AppHapticType = AppHapticType.Confirm
) {
    val shape = RoundedCornerShape(16.dp)
    val hapticFeedback = rememberAppHapticFeedback()

    LoadingHapticEffect(
        isLoading = isLoading,
        hapticFeedback = hapticFeedback
    )

    Box(
        modifier = modifier
            .height(buttonHeight)
            .defaultMinSize(minHeight = 42.dp)
            .clip(shape)
            .background(
                brush = if (enabled) {
                    AppGradients.HighlightVertical
                } else {
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            AppColors.DisabledContainer,
                            AppColors.DisabledContainer
                        )
                    )
                },
                shape = shape
            )
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                enabled = enabled && !isLoading,
                hapticType = hapticType,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = AppColors.OnHighlight
            )
        } else {

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                leadingIcon?.let {
                    leadingIcon()
                }

                Text(
                    text = text,
                    style = AppTextStyles.buttonLarge,
                    color = if (enabled) AppColors.OnHighlight else AppColors.TextDisabled,
                    textAlign = TextAlign.Center
                )

                trailingIcon?.let {
                    trailingIcon()
                }

            }
        }
    }
}