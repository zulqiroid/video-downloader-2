package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.PlayerUiItem
import java.io.File
import coil.compose.SubcomposeAsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles
import timber.log.Timber


@Composable
fun VideoCard(
    item: MediaItem,
    onClick: () -> Unit,
    onMoreClick: (MediaItem) -> Unit,
) {

    LaunchedEffect(item) {
        Timber.tag("media item").d("VideoCard file path: ${item.filePath}")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(
                color = Color(0xFFE5E7EB),
                width = 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        VideoThumbnail(
            path = item.filePath,
            width = 120,
            height = 90,
            duration = item.formattedDuration
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.height(90.dp).weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.title,
                style = AppTextStyles.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "${item.formattedSize} • ${item.qualityLabel}",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ){
                Box(
                    modifier = Modifier.clip(CircleShape).background(AppColors.backgroundGradientTop.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play),
                        contentDescription = "more",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(7.dp)
                            .size(18.dp)
                            .clickable {
                                onMoreClick(item)
                            }
                    )
                }
            }

        }

        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "more",
            tint = AppColors.TextDisabled,
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    onMoreClick(item)
                }
        )
    }
}

@Composable
fun VideoThumbnail(
    path: String?,
    height: Int = 65,
    width: Int = 100,
    duration: String,
) {

    LaunchedEffect(path) {
        Timber.tag("media item").d("VideoThumbnail file path: $path")
    }

    if (path == null) {
        Box(
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .background(Color.Gray, AppShapes.large)
        )

        return
    }

    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .clip(AppShapes.large)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File(path))
                .decoderFactory { result, options, _ ->
                    VideoFrameDecoder(result.source, options)
                }
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                VideoThumbnailPlaceholderContent()
            },
            error = {
                VideoThumbnailPlaceholderContent()
            }
        )


        Text(
            text = duration,
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.Background,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .align(Alignment.BottomEnd)
        )

    }
}

@Composable
private fun VideoThumbnailPlaceholderContent() {
    val transition = rememberInfiniteTransition(label = "thumbnail_shimmer_transition")

    val shimmerTranslate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "thumbnail_shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            AppColors.TextDisabled,
            AppColors.TextDisabled.copy(alpha = 0.45f),
            AppColors.TextDisabled
        ),
        start = Offset(
            x = shimmerTranslate - 300f,
            y = shimmerTranslate - 300f
        ),
        end = Offset(
            x = shimmerTranslate,
            y = shimmerTranslate
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(shimmerBrush),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = null,
            tint = AppColors.Background,
            modifier = Modifier.size(28.dp)
        )
    }
}