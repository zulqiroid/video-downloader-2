package com.video.downloader.presentation.screens.main.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
 import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.video.downloader.presentation.screens.main.componants.MainBottomNavBar
import com.video.downloader.presentation.screens.main.events.MainEvents
import com.video.downloader.presentation.screens.main.states.BottomNavItems
import com.video.downloader.presentation.screens.main.states.MainStates
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.video.downloader.presentation.theme.AppColors

@Composable
fun MainSRC(
    state: MainStates,
    viewModel: MainViewModel,
) {
    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                MainBottomNavBar(
                    state = state,
                    onTabSelected = { selectedTab ->
                        viewModel.onEvent(
                            MainEvents.OnTabSelected(selectedTab)
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = state.selectedTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.Background),
            transitionSpec = {
                val initialTabIndex = initialState.tabIndex()
                val targetTabIndex = targetState.tabIndex()

                val isMovingForward = targetTabIndex > initialTabIndex

                if (isMovingForward) {
                    slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 420,
                            easing = FastOutSlowInEasing
                        ),
                        initialOffsetX = { fullWidth -> fullWidth }
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 220,
                            easing = FastOutSlowInEasing
                        )
                    ) togetherWith slideOutHorizontally(
                        animationSpec = tween(
                            durationMillis = 420,
                            easing = FastOutSlowInEasing
                        ),
                        targetOffsetX = { fullWidth -> -fullWidth }
                    ) + fadeOut(
                        animationSpec = tween(
                            durationMillis = 180,
                            easing = FastOutSlowInEasing
                        )
                    )
                } else {
                    slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 420,
                            easing = FastOutSlowInEasing
                        ),
                        initialOffsetX = { fullWidth -> -fullWidth }
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 220,
                            easing = FastOutSlowInEasing
                        )
                    ) togetherWith slideOutHorizontally(
                        animationSpec = tween(
                            durationMillis = 420,
                            easing = FastOutSlowInEasing
                        ),
                        targetOffsetX = { fullWidth -> fullWidth }
                    ) + fadeOut(
                        animationSpec = tween(
                            durationMillis = 180,
                            easing = FastOutSlowInEasing
                        )
                    )
                }.using(
                    SizeTransform(
                        clip = false
                    )
                )
            },
            label = "MainTabContentAnimation"
        ) { selectedTab ->
            MainTabContent(
                selectedTab = selectedTab,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun MainTabContent(
    selectedTab: BottomNavItems,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (selectedTab) {
            BottomNavItems.Home -> {
                Text(text = "Home screen")
            }

            BottomNavItems.Player -> {
                Text(text = "Player screen")
            }

            BottomNavItems.Reels -> {
                Text(text = "Reels screen")
            }

            BottomNavItems.Files -> {
                Text(text = "File screen")
            }

            BottomNavItems.Vault -> {
                Text(text = "Vault screen")
            }
        }
    }
}

private fun BottomNavItems.tabIndex(): Int {
    return when (this) {
        BottomNavItems.Home -> 0
        BottomNavItems.Player -> 1
        BottomNavItems.Reels -> 2
        BottomNavItems.Files -> 3
        BottomNavItems.Vault -> 4
    }
}