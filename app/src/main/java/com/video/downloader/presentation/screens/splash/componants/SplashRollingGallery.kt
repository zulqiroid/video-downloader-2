package com.video.downloader.presentation.screens.splash.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.screens.splash.states.RollingDirection
import com.video.downloader.presentation.screens.splash.states.SplashImageItem
import com.video.downloader.presentation.theme.AppColors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive

@Composable
fun SplashRollingGallery(
    leftColumnImages: List<SplashImageItem>,
    rightColumnImages: List<SplashImageItem>,
    modifier: Modifier = Modifier,
    middleColumnImages: List<SplashImageItem>,
) {


    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SplashRollingColumn(
                images = leftColumnImages,
                direction = RollingDirection.Up,
                autoScrollSpeed = 45.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            SplashRollingColumn(
                images = middleColumnImages,
                direction = RollingDirection.Down,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            SplashRollingColumn(
                images = rightColumnImages,
                autoScrollSpeed = 45.dp,
                direction = RollingDirection.Up,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

/*        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AppColors.Background.copy(alpha = 0.9f),
                            AppColors.Background,
                        )
                    )
                )
        )*/
    }

}


@Composable
private fun SplashRollingColumn(
    images: List<SplashImageItem>,
    direction: RollingDirection,
    autoScrollSpeed: Dp = 35.dp,
    modifier: Modifier = Modifier,
) {
    if (images.isEmpty()) return

    val startIndex = remember(images.size) {
        val middle = Int.MAX_VALUE / 2
        middle - (middle % images.size)
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = startIndex
    )

    val density = LocalDensity.current

    LaunchedEffect(
        images.size,
        autoScrollSpeed,
        direction
    ) {
        val speedPxPerSecond = with(density) {
            autoScrollSpeed.toPx()
        }

        var previousFrameTime = withFrameNanos { frameTime ->
            frameTime
        }

        while (isActive) {
            val currentFrameTime = withFrameNanos { frameTime ->
                frameTime
            }

            val deltaTimeSeconds = ((currentFrameTime - previousFrameTime) / 1_000_000_000f)
                .coerceAtMost(0.05f)

            previousFrameTime = currentFrameTime

            val baseScrollAmount = speedPxPerSecond * deltaTimeSeconds

            val scrollAmount = when (direction) {
                RollingDirection.Up -> baseScrollAmount
                RollingDirection.Down -> -baseScrollAmount
            }

            val scrollResult = runCatching {
                listState.scrollBy(scrollAmount)
            }

            scrollResult.onFailure { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }

                return@LaunchedEffect
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        userScrollEnabled = false,
    ) {
        items(
            count = Int.MAX_VALUE
        ) { index ->

            val item = images[index % images.size]

            SplashImageCard(
                imageRes = item.imageRes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun SplashImageCard(
    imageRes: Int,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = modifier
            .clip(ShapeDefaults.Large)
            .border(
                width = 2.dp,
                color = AppColors.BorderLight,
                shape = ShapeDefaults.Large
            ),
        contentScale = ContentScale.Crop
    )
}

