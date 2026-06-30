package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.platformDetail.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun PlatformLinkInputCard(
    value: String,
    onValueChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = AppShapes.large
    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.Background, shape)
            .border(
                width = 1.dp,
                color = AppColors.HighlightGradientBottom,
                shape = shape
            )
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = AppTextStyles.bodyMedium.copy(
                color = AppColors.TextEnabled
            ),
            cursorBrush = SolidColor(AppColors.HighlightGradientBottom),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = stringResource(R.string.paste_video_link_here),
                            style = AppTextStyles.bodyMedium,
                            color = AppColors.TextEnabled
                        )
                    }

                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.size(8.dp))

        Row(
            modifier = Modifier
                .background(
                    color = AppColors.DisabledContainer,
                    shape = AppShapes.large
                )
                .hapticClickable(
                    hapticFeedback = hapticFeedback,
                    hapticType = AppHapticType.Click,
                    role = Role.Button,
                    onClick = onPasteClick
                )
                .padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_paste_outlined),
                contentDescription = stringResource(R.string.paste),
                tint = AppColors.TextEnabled
            )

            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = stringResource(R.string.paste),
                style = AppTextStyles.bodySmall,
                color = AppColors.TextEnabled
            )
        }
    }
}

