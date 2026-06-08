package com.video.downloader.presentation.screens.main.screen

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.screens.download.screen.DownloadRootSRC
import com.video.downloader.presentation.screens.home.screen.HomeRootSRC
import com.video.downloader.presentation.screens.main.componants.MainBottomNavBar
import com.video.downloader.presentation.screens.main.events.MainEvents
import com.video.downloader.presentation.screens.main.states.BottomNavItems
import com.video.downloader.presentation.screens.main.states.MainStates
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.video.downloader.presentation.screens.player.screen.PlayerRootSRC
import com.video.downloader.presentation.screens.reels.screen.ReelsRootSRC
import com.video.downloader.presentation.screens.vault.screen.VaultRootSRC
import com.video.downloader.presentation.theme.AppColors

@Composable
fun MainSRC(
    state: MainStates,
    viewModel: MainViewModel,
    backStack: NavBackStack<NavKey>,
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
                mainViewModel = viewModel,
                paddingValues = paddingValues,
                backStack = backStack,
                selectedTab = selectedTab,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun MainTabContent(
    mainViewModel: MainViewModel,
    paddingValues: PaddingValues,
    backStack: NavBackStack<NavKey>,
    selectedTab: BottomNavItems,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (selectedTab) {
            BottomNavItems.Home -> {
                HomeRootSRC(
                    mainPaddingValues = paddingValues,
                    backStack = backStack,
                    toWatchReelsTriggered = {
                        mainViewModel.onEvent(
                            MainEvents.OnTabSelected(
                                BottomNavItems.Reels
                            )
                        )
                    }
                )
            }

            BottomNavItems.Player -> {
                PlayerRootSRC(
                    mainViewModel = mainViewModel,
                    mainPaddingValues = paddingValues,
                    backStack = backStack,
                )
            }

            BottomNavItems.Reels -> {
                ReelsRootSRC(
                    mainViewModel = mainViewModel,
                    mainPaddingValues = paddingValues,
                    backStack = backStack,
                )
            }

            BottomNavItems.Files -> {
                DownloadRootSRC(
                    mainViewModel = mainViewModel,
                    mainPaddingValues = paddingValues,
                    backStack = backStack,
                )
            }

            BottomNavItems.Vault -> {
                VaultRootSRC(
                    mainPaddingValues = paddingValues,
                    onNavigateHome = {
                        mainViewModel.onEvent(
                            MainEvents.OnTabSelected(
                                BottomNavItems.Home
                            )
                        )
                    },
                    backStack = backStack,

                )
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