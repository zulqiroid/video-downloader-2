package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoSplitter.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VideoSplitterUploadCard(
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(460.dp)
            .background(
                color = AppColors.HighlightGradientTop.copy(alpha = 0.08f),
                shape = shape
            )
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            drawRoundRect(
                brush = AppGradients.HighlightVertical,
                cornerRadius = CornerRadius(
                    x = 28.dp.toPx(),
                    y = 28.dp.toPx()
                ),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(18f, 12f),
                        0f
                    )
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_vd_cam_filled),
                    contentDescription = null,
                    tint = AppColors.HighlightGradientBottom,
                    modifier = Modifier.size(62.dp)
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape
                        )
                        .background(
                            color = AppColors.Background,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = AppColors.HighlightGradientBottom,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = "Upload a video to start\nsplitting.",
                style = AppTextStyles.bodyMedium.copy(
                    color = AppColors.HighlightGradientTop
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}