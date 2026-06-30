package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles


@Composable
fun EmptyListView(
    icon: Int,
    title: String,
    subtitle: String,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(
            modifier = Modifier
                .dropShadow(
                    shape = AppShapes.extraLarge,
                    shadow = Shadow(
                        radius = 20.dp,
                        spread = 0.dp,
                        brush = AppGradients.HighlightVertical
                    )
                )
                .clip(AppShapes.extraLarge)
                .background(
                    color = AppColors.Background
                )
                .border(
                    width = 1.dp,
                    brush = AppGradients.HighlightVertical,
                    shape = AppShapes.extraLarge
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(40.dp)
                    .size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = AppTextStyles.titleLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = subtitle,
            style = AppTextStyles.bodyMedium.copy(
                color = AppColors.TextDisabled
            ),
            textAlign = TextAlign.Center
        )

    }


}