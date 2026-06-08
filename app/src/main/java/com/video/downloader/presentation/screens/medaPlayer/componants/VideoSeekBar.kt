package com.video.downloader.presentation.screens.medaPlayer.componants


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp


@Composable
fun VideoSeekBar(
    position: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.White.copy(alpha = 0.2f),
    progressColor: Color = Color(0xFFE00004),
) {
    val realProgress = if (duration > 0L) {
        position.toFloat() / duration.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(realProgress) }

    val progress = if (isDragging) dragProgress else realProgress

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(horizontal = 16.dp)
    ) {
        val widthPx = constraints.maxWidth.toFloat()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .pointerInput(duration) {
                    detectTapGestures { offset ->
                        if (duration <= 0L) return@detectTapGestures

                        val newProgress = (offset.x / widthPx).coerceIn(0f, 1f)
                        onSeek((newProgress * duration).toLong())
                    }
                }
                .pointerInput(duration) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            if (duration > 0L) {
                                onSeek((dragProgress * duration).toLong())
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                        }
                    ) { change, _ ->
                        val newProgress = (change.position.x / widthPx).coerceIn(0f, 1f)
                        dragProgress = newProgress
                    }
                }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterStart)
                    .background(trackColor)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterStart)
                    .background(progressColor)
            )
        }
    }
}