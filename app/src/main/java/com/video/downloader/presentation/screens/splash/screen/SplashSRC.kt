package com.video.downloader.presentation.screens.splash.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.common.componants.AppGradientButton
 import com.video.downloader.presentation.screens.splash.componants.SplashCurvedCardShape
import com.video.downloader.presentation.screens.splash.componants.SplashRollingGallery
import com.video.downloader.presentation.screens.splash.events.SplashEvents
import com.video.downloader.presentation.screens.splash.states.SplashStates
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun SplashSRC(
    state: SplashStates,
    onEvent: (SplashEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AppGradients.SplashBackground)
    ) {
        val bottomCardHeight = maxHeight * 0.46f

        SplashRollingGallery(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight * 0.68f),
            leftColumnImages = state.leftColumnImages,
            rightColumnImages = state.rightColumnImages,
            middleColumnImages = state.middleColumnImages,
        )

        SplashBottomContent(
            state = state,
            onGetStartedClick = {
                onEvent(SplashEvents.GetStartedClicked)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomCardHeight)
        )
    }
}

@Composable
private fun SplashBottomContent(
    state: SplashStates,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(SplashCurvedCardShape())
            .background(AppColors.Background)
            .padding(horizontal = 33.dp)
            .padding(top = 76.dp, bottom = 27.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.video),
                    style = AppTextStyles.splashTitle.copy(
                        brush = AppGradients.HighlightVertical
                    )
                )

                Text(
                    text = stringResource(R.string.downloader),
                    style = AppTextStyles.splashTitle,
                    color = AppColors.TextEnabled
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = stringResource(R.string.easily_download_videos_play),
                style = AppTextStyles.bodyMedium,
                color = AppColors.TextDisabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            AppGradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.get_started),
                onClick = onGetStartedClick,
                enabled = !state.isLoading,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.this_app_may_contains_ads),
                style = AppTextStyles.caption,
                color = AppColors.TextDisabled,
                textAlign = TextAlign.Center
            )
        }
    }
}