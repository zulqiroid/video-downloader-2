package com.video.downloader.presentation.screens.onboarding.componants

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.video.downloader.presentation.screens.onboarding.states.OnboardingPage
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun OnboardingPageContent(
    modifier: Modifier = Modifier,
    page: OnboardingPage,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        page.image?.let {
            Box(
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

}


@Composable
fun OnBoardingPageLabels(
    currentPage: OnboardingPage,
    isLast: Boolean = false,
    modifier: Modifier = Modifier,
 ) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        currentPage.title?.let {
            AnimatedContent(
                targetState = it,
                label = "OnboardingTitleAnimation",
                transitionSpec = {
                    if (targetState != initialState) {
                        slideInVertically(
                            animationSpec = tween(durationMillis = 350),
                            initialOffsetY = { it / 2 }
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 350)
                        ) togetherWith slideOutVertically(
                            animationSpec = tween(durationMillis = 250),
                            targetOffsetY = { -it / 2 }
                        ) + fadeOut(
                            animationSpec = tween(durationMillis = 250)
                        )
                    } else {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(
                            animationSpec = tween(
                                250
                            )
                        )
                    }.using(
                        SizeTransform(clip = false)
                    )
                }
            ) { page ->
                Text(
                    text = stringResource(page),
                    style = AppTextStyles.titleLarge.copy(
                        color = AppColors.TextEnabled,
                        fontSize = 32.sp
                    )
                )
            }


        }

        currentPage.description?.let {
            AnimatedContent(
                targetState = it,
                label = "OnboardingDescriptionAnimation",
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(durationMillis = 350),
                        initialOffsetY = { it / 2 }
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 350)
                    ) togetherWith slideOutVertically(
                        animationSpec = tween(durationMillis = 250),
                        targetOffsetY = { -it / 2 }
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 250)
                    )
                }
            ) { description ->
                Text(
                    text = stringResource(description),
                    style = AppTextStyles.bodyLarge.copy(
                        color = AppColors.TextDisabled,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}