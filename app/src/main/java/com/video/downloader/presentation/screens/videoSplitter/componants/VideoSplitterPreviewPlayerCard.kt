package com.video.downloader.presentation.screens.videoSplitter.componants

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
 import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoSplitterPreviewPlayerCard(
    videoUri: String,
    startMs: Long,
    endMs: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val selectedDurationMs = (endMs - startMs).coerceAtLeast(0L)

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var currentPositionMs by remember {
        mutableLongStateOf(0L)
    }

    val player = remember(videoUri) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
            }
    }

    LaunchedEffect(
        videoUri,
        startMs,
        endMs
    ) {
        if (videoUri.isBlank() || selectedDurationMs <= 0L) {
            player.stop()
            player.clearMediaItems()
            currentPositionMs = 0L
            isPlaying = false
            return@LaunchedEffect
        }

        val clippedMediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUri))
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startMs.coerceAtLeast(0L))
                    .setEndPositionMs(endMs.coerceAtLeast(startMs + 1L))
                    .build()
            )
            .build()

        player.setMediaItem(clippedMediaItem)
        player.prepare()
        player.pause()

        currentPositionMs = 0L
        isPlaying = false
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying = isPlayingValue
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    isPlaying = false
                    currentPositionMs = selectedDurationMs
                }
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(
        player,
        isPlaying,
        selectedDurationMs
    ) {
        while (isPlaying) {
            currentPositionMs = player.currentPosition
                .coerceIn(0L, selectedDurationMs)

            delay(250L)
        }
    }

    val progress = if (selectedDurationMs > 0L) {
        currentPositionMs.toFloat() / selectedDurationMs.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.extraLarge)
            .background(AppColors.BackgroundDisabled)
            .border(
                width = 1.dp,
                color = AppColors.BorderLight,
                shape = AppShapes.extraLarge
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.05f)
                .clip(AppShapes.large)
                .background(AppColors.TextDisabled.copy(alpha = 0.18f))
        ) {
            AndroidView(
                factory = { androidContext ->
                    PlayerView(androidContext).apply {
                        this.player = player
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                update = { playerView ->
                    playerView.player = player
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (selectedDurationMs <= 0L) return@clickable

                        if (player.playbackState == Player.STATE_ENDED) {
                            player.seekTo(0L)
                            currentPositionMs = 0L
                        }

                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = null,
                        tint = AppColors.OnHighlight,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AppColors.TextDisabled.copy(alpha = 0.48f))
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.45f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}