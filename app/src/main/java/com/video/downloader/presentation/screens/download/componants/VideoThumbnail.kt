package com.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.video.downloader.R
import java.io.File

@Composable
fun VideoThumbnail(
    path: String?,
    height: Int = 65,
    width: Int = 100,
    overlayIcon: @Composable () -> Unit
) {
    if (path == null) {
        Box(
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .background(Color.Gray, RoundedCornerShape(12.dp))
        )

        return
    }

    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File(path))
                .decoderFactory { result, options, _ ->
                    VideoFrameDecoder(result.source, options)
                }
                .size(Size.ORIGINAL)
                .build(),
            contentDescription =null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            overlayIcon()
        }
    }
}