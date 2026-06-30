package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.componants

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoToMp3Status
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.states.VideoToMp3States
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients

val VideoToMp3States.activeConversion: VideoToMp3ConversionItem?
    get() {
        return conversions.firstOrNull { item ->
            item.status == VideoToMp3Status.QUEUED ||
                    item.status == VideoToMp3Status.CONVERTING
        }
    }

fun VideoToMp3ConversionItem.dialogTitleText(): String {
    return when (status.name) {
        "QUEUED" -> "Preparing Conversion"
        "CONVERTING" -> "Converting to MP3"
        "READY_TO_SAVE" -> "Conversion Complete"
        "SUCCESS" -> "Conversion Complete"
        "FAILED" -> "Conversion Failed"
        "CANCELLED" -> "Conversion Cancelled"
        else -> "Processing..."
    }
}

fun VideoToMp3ConversionItem.statusText(): String {
    return when (status.name) {
        "QUEUED" -> "Preparing audio engine..."
        "CONVERTING" -> "Extracting and encoding audio..."
        "READY_TO_SAVE" -> "Finalizing output..."
        "SUCCESS" -> "Finalizing output..."
        "FAILED" -> "Unable to convert this file"
        "CANCELLED" -> "Conversion cancelled"
        else -> "Processing audio..."
    }
}

fun VideoToMp3Preset.titleText(): String {
    return when (this) {
        VideoToMp3Preset.AUTO_RECOMMENDED -> "Auto Recommended"
        VideoToMp3Preset.VOICE_LECTURE -> "Voice / Lecture"
        VideoToMp3Preset.MUSIC_SONG -> "Music / Song"
        VideoToMp3Preset.HIGH_QUALITY -> "High Quality"
    }
}

fun VideoToMp3Preset.subtitleText(): String {
    return when (this) {
        VideoToMp3Preset.AUTO_RECOMMENDED -> "Smart mode based on video audio"
        VideoToMp3Preset.VOICE_LECTURE -> "96kbps • Mono • Lectures, podcasts, meetings"
        VideoToMp3Preset.MUSIC_SONG -> "128kbps • Stereo • Songs, reels, music videos"
        VideoToMp3Preset.HIGH_QUALITY -> "192kbps • Stereo • Better quality, larger file"
    }
}

fun VideoToMp3Preset.icon(): ImageVector {
    return when (this) {
        VideoToMp3Preset.AUTO_RECOMMENDED -> Icons.Default.GraphicEq
        VideoToMp3Preset.VOICE_LECTURE -> Icons.Default.MusicNote
        VideoToMp3Preset.MUSIC_SONG -> Icons.Default.MusicNote
        VideoToMp3Preset.HIGH_QUALITY -> Icons.Default.GraphicEq
    }
}

fun Modifier.dashedBorder(
    color: Color,
    radius: Float,
    strokeWidth: Float
): Modifier {
    return drawBehind {
        drawRoundRect(
            brush = AppGradients.HighlightVertical,
            size = size,
            cornerRadius = CornerRadius(radius.dp.toPx(), radius.dp.toPx()),
            style = Stroke(
                width = strokeWidth.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(14f, 9f),
                    phase = 0f
                )
            )
        )
    }
}