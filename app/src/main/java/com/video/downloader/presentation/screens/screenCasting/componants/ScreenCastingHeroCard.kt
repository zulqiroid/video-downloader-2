package com.video.downloader.presentation.screens.screenCasting.componants

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ScreenCastingHeroCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = AppGradients.ReelWatchCardBG,
                shape = AppShapes.extraLarge
            )
            .padding(horizontal = 24.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        ScreenCastingHeroDecoration(
            modifier = Modifier.matchParentSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = AppGradients.HighlightVertical,
                        shape = CircleShape
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cast,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = "Mirror Your Phone Screen",
                style = AppTextStyles.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Use Android full-screen casting to mirror your display on a supported TV.",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ScreenCastingHeroDecoration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = AppColors.Background.copy(alpha = 0.42f),
            radius = size.width * 0.22f,
            center = Offset(
                x = size.width * 0.88f,
                y = size.height * 0.10f
            )
        )

        drawCircle(
            color = AppColors.Background.copy(alpha = 0.30f),
            radius = size.width * 0.14f,
            center = Offset(
                x = size.width * 0.12f,
                y = size.height * 0.84f
            )
        )
    }
}