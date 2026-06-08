    package com.video.downloader.presentation.screens.videoToMp3Result.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun ResultHero(
    isSaved: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .dropShadow(
                    shape = RoundedCornerShape(26.dp),
                    shadow = Shadow(
                        radius = 30.dp,
                        spread = 0.05.dp,
                        brush = AppGradients.HighlightVertical,
                        offset = DpOffset(0.dp, 0.dp)
                    )
                )
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_music_node_filled),
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier.size(60.dp)
            )

            Icon(
                painter = painterResource(R.drawable.ic_single_music_node_filled),
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier
                    .padding(top = 5.dp, end = 5.dp)
                    .size(26.dp)
                    .align(Alignment.TopEnd)
            )

            Icon(
                painter = painterResource(R.drawable.ic_music_nodes_filled),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(bottom = 5.dp, start = 10.dp)
                    .size(26.dp)
                    .align(Alignment.BottomStart)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = if (isSaved) "MP3 Saved" else "Conversion Complete",
            style = AppTextStyles.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = AppColors.TextEnabled,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSaved) {
                "Your audio has been saved successfully."
            } else {
                "High-quality audio extraction finished successfully."
            },
            style = AppTextStyles.bodySmall.copy(
                color = Color(0xFF667085)
            ),
            textAlign = TextAlign.Center
        )
    }
}