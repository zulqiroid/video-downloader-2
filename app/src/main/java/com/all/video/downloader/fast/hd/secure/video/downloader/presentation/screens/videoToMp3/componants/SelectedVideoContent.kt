package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.componants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.events.VideoToMp3Events
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.states.VideoToMp3States
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun SelectedVideoContent(
    state: VideoToMp3States,
    activeConversion: VideoToMp3ConversionItem?,
    onEvent: (VideoToMp3Events) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoPreviewCard(
            uri = state.selectedVideoUri.orEmpty()
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "CONVERSION MODE",
            style = AppTextStyles.overline.copy(
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        VideoToMp3Preset.entries.forEachIndexed { index, preset ->
            PresetModeCard(
                preset = preset,
                selected = state.selectedPreset == preset,
                enabled = activeConversion == null && !state.isSubmitting,
                onClick = {
                    onEvent(
                        VideoToMp3Events.PresetSelected(preset)
                    )
                }
            )

            if (index != VideoToMp3Preset.entries.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}