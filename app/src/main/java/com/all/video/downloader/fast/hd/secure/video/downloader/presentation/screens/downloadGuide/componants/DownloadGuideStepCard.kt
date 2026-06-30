package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.states.DownloadGuideIconType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.downloadGuide.states.DownloadGuideStepUiModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun DownloadGuideStepCard(
    step: DownloadGuideStepUiModel,
    modifier: Modifier = Modifier,
) {
    val shape = AppShapes.large

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp)
            .shadow(
                elevation = 4.dp,
                shape = shape,
             )
            .clip(shape)
            .background(
                color = AppColors.BackgroundDisabled,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = shape
            )
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DownloadGuideIconBox(
            iconType = step.iconType
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = step.titleRes),
                style = AppTextStyles.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(3.dp))

            Text(
                text = stringResource(id = step.descriptionRes),
                style = AppTextStyles.bodySmall,
                color = AppColors.TextDisabled,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DownloadGuideIconBox(
    iconType: DownloadGuideIconType,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AppGradients.HighlightVertical),
        contentAlignment = Alignment.Center
    ) {
        when (iconType) {
            DownloadGuideIconType.Link -> LinkIcon(
                modifier = Modifier.size(24.dp)
            )

            DownloadGuideIconType.Clipboard -> ClipboardIcon(
                modifier = Modifier.size(24.dp)
            )

            DownloadGuideIconType.Download -> DownloadIcon(
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LinkIcon(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.2.dp.toPx()
        val color = AppColors.OnHighlight

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.12f, size.height * 0.36f),
            size = androidx.compose.ui.geometry.Size(
                width = size.width * 0.42f,
                height = size.height * 0.28f
            ),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.46f, size.height * 0.36f),
            size = androidx.compose.ui.geometry.Size(
                width = size.width * 0.42f,
                height = size.height * 0.28f
            ),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        drawLine(
            color = color,
            start = Offset(size.width * 0.40f, size.height * 0.50f),
            end = Offset(size.width * 0.60f, size.height * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ClipboardIcon(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val color = AppColors.OnHighlight
        val strokeWidth = 2.dp.toPx()

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.22f, size.height * 0.20f),
            size = androidx.compose.ui.geometry.Size(
                width = size.width * 0.56f,
                height = size.height * 0.68f
            ),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.36f, size.height * 0.10f),
            size = androidx.compose.ui.geometry.Size(
                width = size.width * 0.28f,
                height = size.height * 0.18f
            ),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        drawLine(
            color = color,
            start = Offset(size.width * 0.36f, size.height * 0.45f),
            end = Offset(size.width * 0.64f, size.height * 0.45f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(size.width * 0.36f, size.height * 0.60f),
            end = Offset(size.width * 0.58f, size.height * 0.60f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DownloadIcon(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val color = AppColors.OnHighlight
        val strokeWidth = 2.2.dp.toPx()

        drawLine(
            color = color,
            start = Offset(size.width * 0.50f, size.height * 0.18f),
            end = Offset(size.width * 0.50f, size.height * 0.58f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        val arrowPath = Path().apply {
            moveTo(size.width * 0.32f, size.height * 0.42f)
            lineTo(size.width * 0.50f, size.height * 0.62f)
            lineTo(size.width * 0.68f, size.height * 0.42f)
        }

        drawPath(
            path = arrowPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = color,
            start = Offset(size.width * 0.30f, size.height * 0.78f),
            end = Offset(size.width * 0.70f, size.height * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}