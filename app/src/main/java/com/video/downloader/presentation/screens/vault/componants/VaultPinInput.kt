package com.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultPinInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = false
) {
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        value = value,
        onValueChange = { raw ->
            onValueChange(
                raw.filter { it.isDigit() }.take(4)
            )
        },
        modifier = modifier
            .width(234.dp)
            .focusRequester(focusRequester),
        singleLine = true,
        textStyle = TextStyle.Default,
        cursorBrush = SolidColor(AppColors.HighlightGradientTop),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        visualTransformation = PasswordVisualTransformation(),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    PinDigitBox(
                        filled = value.length > index,
                        focused = value.length == index && value.length < 4
                    )
                }
            }
        }
    )
}

@Composable
private fun PinDigitBox(
    filled: Boolean,
    focused: Boolean
) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 54.dp)
            .clip(AppShapes.medium)
            .background(AppColors.Background)
            .then(
                if (focused) {
                    Modifier.border(
                        width = 1.7.dp,
                        brush = AppGradients.HighlightVertical,
                        shape = AppShapes.medium
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = AppColors.BorderLight,
                        shape = AppShapes.medium
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (filled) "•" else "",
            style = AppTextStyles.titleLarge,
            color = AppColors.TextEnabled
        )
    }
}