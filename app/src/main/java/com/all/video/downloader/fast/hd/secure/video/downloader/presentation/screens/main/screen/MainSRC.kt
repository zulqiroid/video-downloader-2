package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.screen

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
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.screen.DownloadRootSRC
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.screen.HomeRootSRC
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.componants.MainBottomNavBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.events.MainEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.BottomNavItems
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.states.MainStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.screen.PlayerRootSRC
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.screen.ReelsRootSRC
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.screen.VaultRootSRC
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.domain.screen.AdScreenKey
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate

@Composable
fun MainSRC(
    state: MainStates,
    viewModel: MainViewModel,
    backStack: NavBackStack<NavKey>,
    notificationOpenRequestId: Long = 0L,
) {
    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {

                ScreenNativeAd(
                    screenKey =  state.selectedTab.toAdScreenKey(),
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                MainBottomNavBar(
                    state = state,
                    onTabSelected = { selectedTab ->
                        val fromTab = state.selectedTab

                        if (fromTab != selectedTab) {
                            interstitialAdGate?.showForTabSwitch(
                                activity = activity,
                                fromScreenKey = fromTab.toAdScreenKey(),
                                toScreenKey = selectedTab.toAdScreenKey(),
                                onComplete = {
                                    viewModel.onEvent(
                                        MainEvents.OnTabSelected(selectedTab)
                                    )
                                }
                            ) ?: viewModel.onEvent(
                                MainEvents.OnTabSelected(selectedTab)
                            )
                        }
                    }
                )
                ScreenBannerAd(
                    screenKey = state.selectedTab.toAdScreenKey(),
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    destroyCurrentPlacementOnDispose = false,
                    spacingBefore = 8.dp
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
                notificationOpenRequestId = notificationOpenRequestId,
                modifier = Modifier.fillMaxSize(),
                showPremiumLabel = state.showPremiumLabel,
                onPremiumClick = {
                    backStack.add(Screen.Premium)
                }
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
    notificationOpenRequestId: Long = 0L,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
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
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick,
                    toWatchReelsTriggered = {
                        mainViewModel.onEvent(
                            MainEvents.OnTabSelected(BottomNavItems.Reels)
                        )
                    }
                )
            }

            BottomNavItems.Player -> {
                PlayerRootSRC(
                    mainViewModel = mainViewModel,
                    mainPaddingValues = paddingValues,
                    backStack = backStack,
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick
                )
            }

            BottomNavItems.Reels -> {
                ReelsRootSRC(
                    mainViewModel = mainViewModel,
                    mainPaddingValues = paddingValues,
                    backStack = backStack,
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick
                )
            }

            BottomNavItems.Files -> {
                DownloadRootSRC(
                    mainViewModel = mainViewModel,
                    mainPaddingValues = paddingValues,
                    backStack = backStack,
                    notificationOpenRequestId = notificationOpenRequestId,
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick
                )
            }

            BottomNavItems.Vault -> {
                VaultRootSRC(
                    mainPaddingValues = paddingValues,
                    onNavigateHome = {
                        mainViewModel.onEvent(
                            MainEvents.OnTabSelected(BottomNavItems.Home)
                        )
                    },
                    backStack = backStack,
                    showPremiumLabel = showPremiumLabel,
                    onPremiumClick = onPremiumClick
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

private fun BottomNavItems.toAdScreenKey(): AdScreenKey {
    return when (this) {
        BottomNavItems.Home -> VideoDownloaderAdScreenKeys.HOME
        BottomNavItems.Player -> VideoDownloaderAdScreenKeys.PLAYER
        BottomNavItems.Reels -> VideoDownloaderAdScreenKeys.REELS
        BottomNavItems.Files -> VideoDownloaderAdScreenKeys.FILES
        BottomNavItems.Vault -> VideoDownloaderAdScreenKeys.VAULT
    }
}