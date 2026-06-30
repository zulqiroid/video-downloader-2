package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.LoadingHapticEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun HomeLinkCard(
    videoUrl: String,
    isLoading: Boolean,
    error: String?,
    onVideoUrlChanged: (String) -> Unit,
    onPasteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outerShape = AppShapes.large

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(outerShape)
            .background(
                color = AppColors.DisabledContainer,
                shape = outerShape
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = outerShape
            )
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HomeUrlInputField(
                value = videoUrl,
                onValueChange = onVideoUrlChanged,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomePasteButton(
                    text = stringResource(R.string.paste),
                    onClick = onPasteClick,
                    modifier = Modifier.weight(0.38f)
                )

                HomeDownloadButton(
                    text = stringResource(R.string.download),
                    isLoading = isLoading,
                    onClick = onDownloadClick,
                    modifier = Modifier.weight(0.62f)
                )
            }
        }
    }
}

@Composable
private fun HomeUrlInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = AppShapes.large

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = AppTextStyles.titleMedium.copy(
            fontSize = 21.sp,
            fontWeight = FontWeight.Normal,
            color = AppColors.TextEnabled
        ),
        cursorBrush = SolidColor(AppColors.HighlightGradientTop),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done
        ),
        modifier = modifier
            .clip(shape)
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
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_paste_outlined),
                    contentDescription = null,
                    tint = AppColors.TextDisabled,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = stringResource(R.string.paste_video_link_here),
                            style = AppTextStyles.titleMedium.copy(
                                 fontWeight = FontWeight.Normal
                            ),
                            color = AppColors.TextDisabled,
                            maxLines = 1
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun HomePasteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = AppShapes.large
    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = modifier
            .clip(shape)
            .height(60.dp)
            .background(
                color = AppColors.Background,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = AppColors.HighlightGradientTop.copy(0.6f),
                shape = shape
            )
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = AppHapticType.Click,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_check_paste_filled),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = text,
            style = AppTextStyles.buttonLarge.copy(
                brush = AppGradients.HighlightVertical
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun HomeDownloadButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = AppShapes.large
    val hapticFeedback = rememberAppHapticFeedback()

    LoadingHapticEffect(
        isLoading = isLoading,
        hapticFeedback = hapticFeedback
    )

    AppGradientButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        isLoading = isLoading,
        buttonHeight = 60.dp,
        leadingIcon = {
            DownloadIcon(
                modifier = Modifier.size(24.dp)
            )
        },
        hapticType = AppHapticType.Click
    )
}




@Composable
private fun DownloadIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val purple = AppColors.HighlightGradientTop
        val white = AppColors.OnHighlight

        drawRoundRect(
            color = white,
            topLeft = Offset(size.width * 0.08f, size.height * 0.08f),
            size = androidx.compose.ui.geometry.Size(
                width = size.width * 0.84f,
                height = size.height * 0.84f
            ),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )

        drawLine(
            color = purple,
            start = Offset(size.width * 0.50f, size.height * 0.25f),
            end = Offset(size.width * 0.50f, size.height * 0.60f),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round
        )

        val arrowPath = Path().apply {
            moveTo(size.width * 0.35f, size.height * 0.48f)
            lineTo(size.width * 0.50f, size.height * 0.63f)
            lineTo(size.width * 0.65f, size.height * 0.48f)
        }

        drawPath(
            path = arrowPath,
            color = purple,
            style = Stroke(
                width = 2.4.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = purple,
            start = Offset(size.width * 0.34f, size.height * 0.73f),
            end = Offset(size.width * 0.66f, size.height * 0.73f),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}