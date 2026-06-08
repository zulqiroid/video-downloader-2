package com.video.downloader.presentation.screens.videoSplitter.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSplitterRangeSection(
    startMs: Long,
    endMs: Long,
    durationMs: Long,
    onRangeChange: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val safeDuration = durationMs.coerceAtLeast(1L)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "SPLIT RANGE 1",
                style = AppTextStyles.overline.copy(
                    color = AppColors.TextDisabled,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = formatVideoSplitterTime(endMs),
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextEnabled
                )
            )
        }

        Spacer(modifier = Modifier.padding(7.dp))

        RangeSlider(
            value = startMs.toFloat()..endMs.toFloat(),
            onValueChange = { range ->
                onRangeChange(
                    range.start.toLong(),
                    range.endInclusive.toLong()
                )
            },
            valueRange = 0f..safeDuration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = AppColors.HighlightGradientBottom,
                activeTrackColor = AppColors.HighlightGradientTop,
                inactiveTrackColor = AppColors.BackgroundDisabled
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimeBox(
                title = "Start Time",
                value = formatVideoSplitterTime(startMs),
                modifier = Modifier.weight(1f)
            )

            TimeBox(
                title = "End Time",
                value = formatVideoSplitterTime(endMs),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TimeBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = AppColors.Background,
                shape = AppShapes.large
            )
            .border(
                width = 1.dp,
                color = AppColors.HighlightGradientTop,
                shape = AppShapes.large
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.TextDisabled
            )
        )

        Text(
            text = value,
            style = AppTextStyles.bodyMedium.copy(
                color = AppColors.TextEnabled
            )
        )
    }
}
