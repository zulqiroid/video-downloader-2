package com.video.downloader.presentation.screens.medaPlayer.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.video.downloader.R
import com.video.downloader.domain.models.MediaFile
import com.video.downloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel

@Composable
fun AudioPlayerItem(
    media: MediaFile,
    viewModel: MediaPlayerViewModel,
    onBack: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onForward: () -> Unit = {},
    onRewind: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onSeek: (Long) -> Unit = {},
    onMuteToggle: () -> Unit = {},
    onVolumeChange: (Float) -> Unit = {},
    onThreeDotsClick:() -> Unit,
    onShuffleClick: () -> Unit = {},
    onRepeatClick: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
     Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = onBack,
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = null,
                    tint = Color(0xFF101828)
                )
            }

            Text(
                text = media.fileName,
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF101828),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                maxLines = 1
            )

            IconButton(
                onClick = {
                    onThreeDotsClick()
                },
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color(0xFF101828)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 20.dp,
                            spread = 5.dp,
                            color = Color(0xFFE00004).copy(alpha = 0.4f),
                            offset = DpOffset(0.dp, 0.dp)
                        )
                    )
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.height(36.dp))
            Image(
                painter = painterResource(R.drawable.ic_single_music_node_filled),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 30.dp)
            )


            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(state.position),
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF101828)
                )

                Text(
                    text = formatTime(state.duration),
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF101828)
                )
            }

            VideoSeekBar(
                position = state.position,
                duration = state.duration,
                onSeek = {
                    viewModel.onEvent(MediaPlayerEvent.OnSeek(it))
                },
                trackColor = Color.White
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(R.drawable.ic_info_filled),
                    contentDescription = null,
                    tint = if (state.isShuffleEnabled) {
                        Color(0xFFE00004)
                    } else {
                        Color(0xFF99A1AF)
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clickable {
                            onShuffleClick()
                        }
                )

                Icon(
                    painter = painterResource(R.drawable.ic_tv),
                    contentDescription = null,
                    tint = Color(0xFF101828),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onPrevious() }
                )

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE00004))
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_vault_filled),
                    contentDescription = null,
                    tint = Color(0xFF101828),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onNext() }
                )

                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = null,
                    tint = if (state.isAudioRepeatEnabled) {
                        Color(0xFFE00004)
                    } else {
                        Color(0xFF99A1AF)
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clickable {
                            onRepeatClick()
                        }
                )
            }

            Spacer(Modifier.height(26.dp))

        }
    }
}