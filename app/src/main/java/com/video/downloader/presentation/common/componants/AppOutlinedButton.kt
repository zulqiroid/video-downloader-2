package com.video.downloader.presentation.common.componants

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.LoadingHapticEffect
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    buttonHeight: Dp = 60.dp,
    borderWidth: Dp = 1.5.dp,
    borderColor: Color? = null,
    borderBrush: Brush? = null,
    textColor: Color? = null,
    textBrush: Brush? = null,
    backgroundColor: Color = Color.Transparent,
    disabledBorderColor: Color = AppColors.DisabledContainer,
    disabledTextColor: Color = AppColors.TextDisabled,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    hapticType: AppHapticType = AppHapticType.Confirm
) {
    val hapticFeedback = rememberAppHapticFeedback()

    LoadingHapticEffect(
        isLoading = isLoading,
        hapticFeedback = hapticFeedback
    )

    val resolvedBorderBrush = borderBrush ?: borderColor?.toSolidBrush()
    val resolvedTextBrush = textBrush
    val resolvedContentColor = when {
        !enabled -> disabledTextColor
        resolvedTextBrush != null -> Color.Unspecified
        textColor != null -> textColor
        else -> AppColors.TextEnabled
    }

    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .height(buttonHeight)
            .defaultMinSize(minHeight = 42.dp)
            .then(
                if (resolvedBorderBrush != null && enabled) {
                    Modifier.gradientBorder(
                        width = borderWidth,
                        brush = resolvedBorderBrush,
                        shape = ButtonDefaults.outlinedShape
                    )
                } else {
                    Modifier
                }
            ),
        shape = ButtonDefaults.outlinedShape,
        border = when {
            !enabled -> BorderStroke(borderWidth, disabledBorderColor)
            resolvedBorderBrush != null -> null
            borderColor != null -> BorderStroke(borderWidth, borderColor)
            else -> BorderStroke(borderWidth, AppColors.TextEnabled)
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            disabledContainerColor = Color.Transparent,
            contentColor = resolvedContentColor,
            disabledContentColor = disabledTextColor
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = if (enabled) {
                    textColor ?: AppColors.TextEnabled

                } else {
                    disabledTextColor
                }
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                leadingIcon?.invoke()

                Text(
                    text = text,
                    style = AppTextStyles.buttonLarge.withBrushOrDefault(
                        brush = resolvedTextBrush,
                        enabled = enabled
                    ),
                    color = resolvedContentColor,
                    textAlign = TextAlign.Center
                )

                trailingIcon?.invoke()
            }
        }
    }
}

private fun TextStyle.withBrushOrDefault(
    brush: Brush?,
    enabled: Boolean
): TextStyle {
    return if (enabled && brush != null) {
        copy(brush = brush)
    } else {
        this
    }
}

private fun Color.toSolidBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(this, this)
    )
}


fun Modifier.gradientBorder(
    width: Dp,
    brush: Brush,
    shape: Shape
): Modifier {
    return this
        .clip(shape)
        .border(
            width = width,
            brush = brush,
            shape = shape
        )
}