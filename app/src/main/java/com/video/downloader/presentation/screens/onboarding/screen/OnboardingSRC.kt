package com.video.downloader.presentation.screens.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.common.componants.SmoothElongateIndicator
import com.video.downloader.presentation.screens.onboarding.componants.OnBoardingPageLabels
import com.video.downloader.presentation.screens.onboarding.componants.OnboardingBottomBar
import com.video.downloader.presentation.screens.onboarding.componants.OnboardingPageContent
import com.video.downloader.presentation.screens.onboarding.componants.OnboardingTopBar
import com.video.downloader.presentation.screens.onboarding.events.OnboardingEvents
import com.video.downloader.presentation.screens.onboarding.events.OnboardingNavEvents
import com.video.downloader.presentation.screens.onboarding.states.OnboardingPage
import com.video.downloader.presentation.screens.onboarding.states.OnboardingStates
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@Composable
fun OnboardingSRC(
    state: OnboardingStates,
    effectFlow: Flow<OnboardingNavEvents>,
    onEvent: (OnboardingEvents) -> Unit,
    onFinished: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.onboardingPage.size }
    )
    val currentPageIndex = state.currentPage.coerceIn(
        minimumValue = 0,
        maximumValue = state.onboardingPage.lastIndex
    )
    val currentPage = state.onboardingPage.getOrNull(currentPageIndex)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                onEvent(OnboardingEvents.PageChanged(page))
            }
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is OnboardingNavEvents.ScrollToPage -> {
                    pagerState.animateScrollToPage(effect.page)
                }

                OnboardingNavEvents.NavigateNext -> {
                    onFinished()
                }
            }
        }
    }


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )
                if (currentPage?.isLast == false) {
                    OnboardingTopBar()
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OnboardingBottomBar(
                    isLastPage = currentPage?.isLast == true,
                    buttonText = stringResource(if (currentPage?.isLast == true) R.string.continue_text else R.string.next),
                    onButtonClick = {
                        if (currentPageIndex == state.onboardingPage.lastIndex) {
                            onEvent(OnboardingEvents.NextClicked)
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(currentPageIndex + 1)
                            }
                        }
                    }
                )
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                )

            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    if (pageIndex == state.onboardingPage.lastIndex){
                        Box(
                            modifier = Modifier.padding(20.dp).fillMaxSize().clip(AppShapes.large).background(
                                color = AppColors.TextDisabled
                            ),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "Ad here",
                                style = AppTextStyles.titleLarge
                            )
                        }
                    }else {
                        OnboardingPageContent(
                            page = state.onboardingPage[pageIndex],
                        )
                    }

                }
                OnBoardingPageLabels(
                    currentPage = currentPage ?: OnboardingPage(
                        image = null,
                        title = null,
                        description = null,
                        isLast = false
                    ),
                    isLast = currentPage?.isLast == true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (currentPage?.isLast == false) {
                    Spacer(modifier = Modifier.weight(0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SmoothElongateIndicator(
                            pageCount = state.onboardingPage.size,
                            currentPage = pagerState.currentPage,
                            onPageSelected = { page ->
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(page)
                                }
                            }
                        )
                    }
                }

            }

        }
    }

}

