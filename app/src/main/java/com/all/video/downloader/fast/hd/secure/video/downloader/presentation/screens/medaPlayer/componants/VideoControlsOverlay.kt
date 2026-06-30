package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors

@Composable
fun VideoControlsOverlay(
    media: MediaFile,
    state: MediaPlayerState,
    showCenterControls: Boolean,
    isVideoZoomed: Boolean,
    isLandscape: Boolean,
    isControlsLocked: Boolean,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onForward: () -> Unit,
    onRewind: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onMuteToggle: () -> Unit,
    onResizeToggle: () -> Unit,
    onRotateClick: () -> Unit,
    onPictureInPictureClick: () -> Unit,
    onLockToggle: () -> Unit,
    onThreeDotsClick: () -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isControlsLocked) {
                    Color.Transparent
                } else {
                    Color.Black.copy(alpha = 0.28f)
                }
            )
    ) {
        if (isControlsLocked) {
            UnlockButton(
                isLandscape = isLandscape,
                onClick = onLockToggle
            )
            return@Box
        }

        VideoTopControls(
            media = media,
            isLandscape = isLandscape,
            onBack = onBack,
            onPictureInPictureClick = onPictureInPictureClick,
            onThreeDotsClick = onThreeDotsClick
        )

        AnimatedVisibility(
            visible = showCenterControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            VideoCenterControls(
                isLandscape = isLandscape,
                onRewind = onRewind,
                onForward = onForward
            )
        }

        if (isLandscape) {
            VideoLandscapeBottomControls(
                state = state,
                isVideoZoomed = isVideoZoomed,
                onSeek = onSeek,
                onMuteToggle = onMuteToggle,
                onResizeToggle = onResizeToggle,
                onRotateClick = onRotateClick,
                onLockToggle = onLockToggle,
                onPrevious = onPrevious,
                onPlayPause = onPlayPause,
                onNext = onNext
            )
        } else {
            VideoPortraitBottomControls(
                state = state,
                isVideoZoomed = isVideoZoomed,
                onSeek = onSeek,
                onMuteToggle = onMuteToggle,
                onResizeToggle = onResizeToggle,
                onRotateClick = onRotateClick,
                onLockToggle = onLockToggle,
                onPrevious = onPrevious,
                onPlayPause = onPlayPause,
                onNext = onNext
            )
        }
    }
}

@Composable
private fun VideoTopControls(
    media: MediaFile,
    isLandscape: Boolean,
    onBack: () -> Unit,
    onPictureInPictureClick: () -> Unit,
    onThreeDotsClick: () -> Unit,
) {
    val horizontalPadding = if (isLandscape) 28.dp else 16.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                WindowInsets.safeDrawing.asPaddingValues()
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFF191919)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            text = media.fileName,
            fontSize = if (isLandscape) 13.sp else 14.sp,
            fontWeight = FontWeight.W600,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            maxLines = 1
        )

        IconButton(
            onClick = onPictureInPictureClick,
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFF191919)
            )
        ) {
            Icon(
                imageVector = Icons.Default.PictureInPictureAlt,
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(
            onClick = onThreeDotsClick,
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFF191919)
            )
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.White
            )
        }

    }
}

@Composable
private fun VideoCenterControls(
    isLandscape: Boolean,
    onRewind: () -> Unit,
    onForward: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleButton(
            icon = R.drawable.ic_10_sec_backward,
            onClick = onRewind
        )

        if (!isLandscape) {
            Spacer(modifier = Modifier.size(44.dp))
        }

        CircleButton(
            icon = R.drawable.ic_10_sec_forward,
            onClick = onForward
        )
    }
}

@Composable
private fun VideoPortraitBottomControls(
    state: MediaPlayerState,
    isVideoZoomed: Boolean,
    onSeek: (Long) -> Unit,
    onMuteToggle: () -> Unit,
    onResizeToggle: () -> Unit,
    onRotateClick: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onLockToggle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        VideoSeekBar(
            position = state.position,
            duration = state.duration,
            onSeek = onSeek
        )

        VideoTimeAndScreenActionsRow(
            state = state,
            isVideoZoomed = isVideoZoomed,
            onRotateClick = onRotateClick,
            onResizeToggle = onResizeToggle
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeakerButton(
                isMuted = state.isMuted,
                onClick = onMuteToggle
            )

            Icon(
                painter = painterResource(R.drawable.ic_previous_filled),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        onPrevious()
                    }
            )

            PlayPauseButton(
                isPlaying = state.isPlaying,
                size = 90,
                iconSize = 42,
                onClick = onPlayPause
            )

            Icon(
                painter = painterResource(R.drawable.ic_next_filled),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        onNext()
                    }
            )

            LockButton(
                onClick = onLockToggle
            )
        }
    }
}

@Composable
private fun VideoLandscapeBottomControls(
    state: MediaPlayerState,
    isVideoZoomed: Boolean,
    onSeek: (Long) -> Unit,
    onMuteToggle: () -> Unit,
    onResizeToggle: () -> Unit,
    onRotateClick: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onLockToggle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
               horizontal = 20.dp
            )
            .padding(bottom = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        VideoSeekBar(
            position = state.position,
            duration = state.duration,
            onSeek = onSeek,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TimeText(
                state = state,
                fontSize = 11
            )

            Spacer(modifier = Modifier.weight(1f))

            SpeakerButton(
                isMuted = state.isMuted,
                size = 26,
                onClick = onMuteToggle
            )

            Spacer(modifier = Modifier.size(14.dp))

            Icon(
                painter = painterResource(R.drawable.ic_previous_filled),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onPrevious()
                    }
            )

            Spacer(modifier = Modifier.size(14.dp))

            PlayPauseButton(
                isPlaying = state.isPlaying,
                size = 52,
                iconSize = 28,
                onClick = onPlayPause
            )

            Spacer(modifier = Modifier.size(14.dp))

            Icon(
                painter = painterResource(R.drawable.ic_next_filled),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onNext()
                    }
            )

            Spacer(modifier = Modifier.size(14.dp))

            ScreenActionButtons(
                isVideoZoomed = isVideoZoomed,
                iconSize = 22,
                onRotateClick = onRotateClick,
                onResizeToggle = onResizeToggle
            )

            Spacer(modifier = Modifier.size(10.dp))

            LockButton(
                size = 28,
                onClick = onLockToggle
            )
        }
    }
}

@Composable
private fun TimeText(
    state: MediaPlayerState,
    fontSize: Int = 14
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatTime(state.position),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.W500,
            color = Color.White.copy(alpha = 0.8f)
        )

        Text(
            text = " / ",
            fontSize = fontSize.sp,
            fontWeight = FontWeight.W500,
            color = Color.White.copy(alpha = 0.5f)
        )

        Text(
            text = formatTime(state.duration),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.W500,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}


@Composable
private fun ScreenActionButtons(
    isVideoZoomed: Boolean,
    iconSize: Int = 24,
    onRotateClick: () -> Unit,
    onResizeToggle: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        IconButton(
            onClick = onRotateClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_rotate_outlined),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize.dp)
            )
        }

        IconButton(
            onClick = onResizeToggle,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_resize_outlined),
                contentDescription = null,
                tint = if (isVideoZoomed) {
                    AppColors.HighlightGradientTop
                } else {
                    Color.White
                },
                modifier = Modifier.size(iconSize.dp)
            )
        }
    }
}

@Composable
private fun SpeakerButton(
    isMuted: Boolean,
    size: Int = 34,
    onClick: () -> Unit,
) {
    Icon(
        painter = if (isMuted) {
            painterResource(R.drawable.ic_mute_outlined)
        } else {
            painterResource(R.drawable.ic_unmute_filled)
        },
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(size.dp)
            .clickable {
                onClick()
            }
    )
}

@Composable
private fun VideoTimeAndScreenActionsRow(
    state: MediaPlayerState,
    isVideoZoomed: Boolean,
    onRotateClick: () -> Unit,
    onResizeToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimeText(state = state)

        ScreenActionButtons(
            isVideoZoomed = isVideoZoomed,
            onRotateClick = onRotateClick,
            onResizeToggle = onResizeToggle
        )
    }
}

@Composable
private fun TimeText(
    state: MediaPlayerState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatTime(state.position),
            fontWeight = FontWeight.W500,
            color = Color.White.copy(alpha = 0.8f)
        )

        Text(
            text = " / ",
            fontWeight = FontWeight.W500,
            color = Color.White.copy(alpha = 0.5f)
        )

        Text(
            text = formatTime(state.duration),
            fontWeight = FontWeight.W500,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ScreenActionButtons(
    isVideoZoomed: Boolean,
    onRotateClick: () -> Unit,
    onResizeToggle: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
     IconButton(
            onClick = onRotateClick
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_rotate_outlined),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = onResizeToggle
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_resize_outlined),
                contentDescription = null,
                tint = if (isVideoZoomed) {
                    AppColors.HighlightGradientTop
                } else {
                    Color.White
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SpeakerButton(
    isMuted: Boolean,
    onClick: () -> Unit,
) {
    Icon(
        painter = if (isMuted) {
            painterResource(R.drawable.ic_mute_outlined)
        } else {
            painterResource(R.drawable.ic_unmute_filled)
        },
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(34.dp)
            .clickable {
                onClick()
            }
    )
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    size: Int,
    iconSize: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(AppColors.HighlightGradientTop)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) {
                Icons.Default.Pause
            } else {
                Icons.Default.PlayArrow
            },
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize.dp)
        )
    }
}

@Composable
private fun LockButton(
    size: Int = 34,
    onClick: () -> Unit,
) {
    Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(size.dp)
            .clickable {
                onClick()
            }
    )
}

@Composable
private fun UnlockButton(
    isLandscape: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                end = if (isLandscape) 38.dp else 30.dp,
                bottom = if (isLandscape) 20.dp else 34.dp
            ),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(if (isLandscape) 44.dp else 52.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier.size(if (isLandscape) 24.dp else 28.dp)
            )
        }
    }
}

private fun Modifier.alignToBottomWithLandscapeInsets(): Modifier {
    return this
        .fillMaxWidth()
        .padding(bottom = 8.dp)
}