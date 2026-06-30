package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.ui.res.stringResource
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun AppLanguageSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search_language),
) {
    val shape = ShapeDefaults.Large

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = AppTextStyles.bodyMedium.copy(
            color = AppColors.TextEnabled
        ),
        cursorBrush = SolidColor(AppColors.HighlightGradientTop),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = AppColors.Background,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = shape
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchIcon(
                    modifier = Modifier.size(22.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = AppTextStyles.bodyMedium,
                            color = AppColors.TextDisabled
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun SearchIcon(
    modifier: Modifier = Modifier
) {
    val iconColor = AppColors.TextDisabled

    Canvas(modifier = modifier) {
        val strokeWidth = 1.5.dp.toPx()
        val radius = size.minDimension * 0.32f
        val center = Offset(
            x = size.width * 0.42f,
            y = size.height * 0.42f
        )

        drawCircle(
            color = iconColor,
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth
            )
        )

        drawLine(
            color = iconColor,
            start = Offset(
                x = size.width * 0.66f,
                y = size.height * 0.66f
            ),
            end = Offset(
                x = size.width * 0.88f,
                y = size.height * 0.88f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}