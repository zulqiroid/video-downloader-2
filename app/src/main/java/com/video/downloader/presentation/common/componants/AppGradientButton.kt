package com.video.downloader.presentation.common.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun AppGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    buttonHeight: Int = 60
) {
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(buttonHeight.dp)
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
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
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
            Text(
                text = text,
                style = AppTextStyles.buttonLarge,
                color = if (enabled) AppColors.OnHighlight else AppColors.TextDisabled,
                textAlign = TextAlign.Center
            )
        }
    }
}