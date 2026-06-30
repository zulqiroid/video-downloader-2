package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSeekFeedbackDirection
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSeekFeedbackState

@Composable
fun MediaPlayerSeekFeedbackOverlay(
    state: MediaPlayerSeekFeedbackState,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = when (state.direction) {
                MediaPlayerSeekFeedbackDirection.Rewind -> Alignment.CenterStart
                MediaPlayerSeekFeedbackDirection.Forward -> Alignment.CenterEnd
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.62f))
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (state.direction == MediaPlayerSeekFeedbackDirection.Rewind) {
                    Icon(
                        painter = painterResource(R.drawable.ic_10_sec_backward),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = state.resolveText(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (state.direction == MediaPlayerSeekFeedbackDirection.Forward) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(R.drawable.ic_10_sec_forward),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

private fun MediaPlayerSeekFeedbackState.resolveText(): String {
    return when (direction) {
        MediaPlayerSeekFeedbackDirection.Rewind -> "-${seconds}s"
        MediaPlayerSeekFeedbackDirection.Forward -> "+${seconds}s"
    }
}