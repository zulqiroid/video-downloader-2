package com.video.downloader.presentation.screens.onboarding.componants

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.video.downloader.R
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun OnboardingBottomBar(
    buttonText: String,
    onButtonClick: () -> Unit,
    isLastPage: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .then(
                if (isLastPage) {
                    Modifier.border(
                        width = 2.dp,
                        brush = AppGradients.HighlightVertical,
                        shape = AppShapes.large
                    )
                } else {
                    Modifier
                }
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = isLastPage,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            ) + slideInVertically(
                animationSpec = tween(
                    durationMillis = 450,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetY = { fullHeight -> fullHeight / 3 }
            ) + expandVertically(
                animationSpec = tween(
                    durationMillis = 450,
                    easing = FastOutSlowInEasing
                ),
                expandFrom = Alignment.Top
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 220,
                    easing = FastOutSlowInEasing
                )
            ) + slideOutVertically(
                animationSpec = tween(
                    durationMillis = 260,
                    easing = FastOutSlowInEasing
                ),
                targetOffsetY = { fullHeight -> fullHeight / 4 }
            ) + shrinkVertically(
                animationSpec = tween(
                    durationMillis = 260,
                    easing = FastOutSlowInEasing
                ),
                shrinkTowards = Alignment.Top
            )
        ) {
            LastOnboardingPageHeader()
        }

        Spacer(modifier = Modifier.height(15.dp))

        AppGradientButton(
            modifier = Modifier.fillMaxWidth(),
            text = buttonText,
            onClick = onButtonClick,
            enabled = true,
            isLoading = false
        )
    }
}

@Composable
private fun LastOnboardingPageHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.video) + " ",
                style = AppTextStyles.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Text(
                text = stringResource(R.string.downloader),
                style = AppTextStyles.titleLarge.copy(
                    fontSize = 28.sp,
                    brush = AppGradients.HighlightVertical,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.user_friendly_download_engine),
            style = AppTextStyles.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.TextDisabled
            )
        )
    }
}