package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.ui.ScreenBannerAd
import com.core.ads.ui.ScreenNativeAd
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingActiveSessionCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingGuidelinesCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingHeroCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingManageReturnDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingNetworkCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingNetworkWarningDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingStartReturnDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingStatusCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingTopBar
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.ScreenCastingTroubleshootingCard
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants.StartScreenCastingDialog
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.events.ScreenCastingEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.states.ScreenCastingStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.states.ScreenCastingStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys

@Composable
fun ScreenCastingSRC(
    state: ScreenCastingStates,
    onEvent: (ScreenCastingEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.SCREEN_CASTING,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    ScreenCastingTopBar(
                        onBackClick = {
                            onEvent(ScreenCastingEvents.BackClicked)
                        }
                    )
                }
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.SCREEN_CASTING,
                    position = AdSlotPosition.TOP,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                ScreenNativeAd(
                    screenKey = VideoDownloaderAdScreenKeys.SCREEN_CASTING,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingAfter = 10.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    AppGradientButton(
                        text = state.primaryButtonText,
                        enabled = !state.isPrimaryButtonLoading,
                        isLoading = state.isPrimaryButtonLoading,
                        onClick = {
                            if (state.castStatus == ScreenCastingStatus.CastingActive) {
                                onEvent(ScreenCastingEvents.StopCastingClicked)
                            } else {
                                interstitialAdGate?.showForFeature(
                                    activity = activity,
                                    featureKey = VideoDownloaderAdFeatureKeys.SCREEN_CASTING_ENTRY,
                                    onComplete = {
                                        onEvent(ScreenCastingEvents.StartCastingClicked)
                                    }
                                ) ?: onEvent(ScreenCastingEvents.StartCastingClicked)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(6.dp))

                    AppOutlinedButton(
                        text = "Open Wi-Fi Settings",
                        onClick = {
                            onEvent(ScreenCastingEvents.OpenWifiSettingsClicked)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        borderBrush = AppGradients.HighlightVertical,
                        textBrush = AppGradients.HighlightVertical
                    )
                }
                ScreenBannerAd(
                    screenKey = VideoDownloaderAdScreenKeys.SCREEN_CASTING,
                    position = AdSlotPosition.BOTTOM,
                    modifier = Modifier.fillMaxWidth(),
                    spacingBefore = 10.dp
                )

                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ScreenCastingHeroCard(
                modifier = Modifier.fillMaxWidth()
            )

            ScreenCastingStatusCard(
                status = state.castStatus,
                title = state.statusTitle,
                description = state.statusDescription,
                modifier = Modifier.fillMaxWidth()
            )

            ScreenCastingNetworkCard(
                connected = state.isLocalNetworkConnected,
                title = state.networkTitle,
                description = state.networkDescription,
                modifier = Modifier.fillMaxWidth()
            )

            ScreenCastingGuidelinesCard(
                modifier = Modifier.fillMaxWidth()
            )

            ScreenCastingTroubleshootingCard(
                onClick = {
                    onEvent(ScreenCastingEvents.TroubleshootingClicked)
                },
                modifier = Modifier.fillMaxWidth()
            )

            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    style = AppTextStyles.bodySmall.copy(
                        color = AppColors.Error
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "Casting status is based on your confirmation after returning from Android cast panel. Android controls the actual mirroring session.",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            )

            Spacer(modifier = Modifier.padding(bottom = 20.dp))
        }
    }

    if (state.showStartCastingDialog) {
        StartScreenCastingDialog(
            onDismiss = {
                onEvent(ScreenCastingEvents.StartCastingDialogDismissed)
            },
            onContinueClick = {
                onEvent(ScreenCastingEvents.ConfirmStartCastingClicked)
            }
        )
    }

    if (state.showStartReturnConfirmationDialog) {
        ScreenCastingStartReturnDialog(
            onCastingStartedClick = {
                onEvent(ScreenCastingEvents.CastingStartedConfirmed)
            },
            onCastingNotStartedClick = {
                onEvent(ScreenCastingEvents.CastingNotStartedConfirmed)
            },
            onDismiss = {
                onEvent(ScreenCastingEvents.StartReturnConfirmationDismissed)
            }
        )
    }

    if (state.showManageReturnConfirmationDialog) {
        ScreenCastingManageReturnDialog(
            onStillCastingClick = {
                onEvent(ScreenCastingEvents.CastingStillActiveConfirmed)
            },
            onCastingStoppedClick = {
                onEvent(ScreenCastingEvents.CastingStoppedConfirmed)
            },
            onDismiss = {
                onEvent(ScreenCastingEvents.ManageReturnConfirmationDismissed)
            }
        )
    }
    if (state.showNetworkWarningDialog) {
        ScreenCastingNetworkWarningDialog(
            onOpenWifiClick = {
                onEvent(ScreenCastingEvents.OpenWifiSettingsClicked)
            },
            onContinueAnywayClick = {
                onEvent(ScreenCastingEvents.ContinueWithoutNetworkClicked)
            },
            onDismiss = {
                onEvent(ScreenCastingEvents.NetworkWarningDismissed)
            }
        )
    }
    if (state.isCastingActive) {
        ScreenCastingActiveSessionCard(
            onManageClick = {
                onEvent(ScreenCastingEvents.StopCastingClicked)
            },
            onMarkStoppedClick = {
                onEvent(ScreenCastingEvents.MarkCastingStoppedClicked)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}