package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerGestureFeedbackState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerGestureFeedbackType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppCornerRadius

@Composable
fun MediaPlayerGestureFeedbackOverlay(
    state: MediaPlayerGestureFeedbackState,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(top = 28.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .width(156.dp)
                    .clip(RoundedCornerShape(AppCornerRadius.Large))
                    .background(Color.Black.copy(alpha = 0.72f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = state.resolveIcon(),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = state.resolveTitle(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LinearProgressIndicator(
                    progress = {
                        state.percent.coerceIn(0, 100) / 100f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppCornerRadius.Full)),
                    color = AppColors.HighlightGradientTop,
                    trackColor = Color.White.copy(alpha = 0.22f)
                )

                Text(
                    text = "${state.percent.coerceIn(0, 100)}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.86f)
                )
            }
        }
    }
}

private fun MediaPlayerGestureFeedbackState.resolveTitle(): String {
    return when (type) {
        MediaPlayerGestureFeedbackType.Brightness -> "Brightness"
        MediaPlayerGestureFeedbackType.Volume -> "Volume"
    }
}

private fun MediaPlayerGestureFeedbackState.resolveIcon() = when (type) {
    MediaPlayerGestureFeedbackType.Brightness -> Icons.Default.Brightness6
    MediaPlayerGestureFeedbackType.Volume -> {
        if (percent <= 0) {
            Icons.Default.VolumeOff
        } else {
            Icons.Default.VolumeUp
        }
    }
}