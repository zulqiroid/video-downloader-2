package com.core.ads.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.core.ads.domain.banner.BannerAdController
import com.core.ads.domain.banner.BannerPlacementKeys
import com.core.ads.domain.manager.AdsManager
import com.core.ads.domain.screen.AdScreenKey
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.utils.AdsLogger
import org.koin.core.context.GlobalContext

@Composable
fun ScreenBannerAd(
    screenKey: AdScreenKey,
    position: AdSlotPosition,
    modifier: Modifier = Modifier,
    destroyCurrentPlacementOnDispose: Boolean = false,
    visible: Boolean = true,
    spacingBefore: Dp = 0.dp,
    spacingAfter: Dp = 0.dp
) {
    ScreenBannerAd(
        screenKey = screenKey.value,
        position = position,
        modifier = modifier,
        destroyCurrentPlacementOnDispose = destroyCurrentPlacementOnDispose,
        visible = visible,
        spacingBefore = spacingBefore,
        spacingAfter = spacingAfter
    )
}

@Composable
fun ScreenBannerAd(
    screenKey: String,
    position: AdSlotPosition,
    modifier: Modifier = Modifier,
    destroyCurrentPlacementOnDispose: Boolean = false,
    visible: Boolean = true,
    spacingBefore: Dp = 0.dp,
    spacingAfter: Dp = 0.dp
) {
    if (!visible) return

    val dependencies = rememberScreenBannerDependencies()
        ?: return

    val adsConfig by dependencies.adsManager.configState.collectAsState()
    val initializationState by dependencies.adsManager.initializationState.collectAsState()
    val bannerStates by dependencies.bannerAdController.states.collectAsState()

    val placement = remember(
        screenKey,
        position
    ) {
        BannerPlacementKeys.fromScreenSlot(
            screenKey = screenKey,
            position = position
        )
    } ?: return

    val bannerConfig = adsConfig.bannerAdConfig

    val shouldShowDebugLabel =
        adsConfig.isDebug &&
                bannerConfig.debugLabelEnabled

    val resolvedBannerConfig = remember(
        adsConfig,
        placement
    ) {
        adsConfig.resolveBannerConfig(placement)
    }

    val bannerState = bannerStates[placement.value]

    /**
     * If Remote Config / policy does not resolve this placement, normally we render nothing.
     * But in debug mode, show tiny diagnostic label so QA can identify why nothing appears.
     */
    if (resolvedBannerConfig == null) {
        if (shouldShowDebugLabel) {
            ScreenBannerContainer(
                modifier = modifier,
                spacingBefore = spacingBefore,
                spacingAfter = spacingAfter
            ) {
                BannerDebugLabel(
                    placementKey = placement.value,
                    status = "NOT_RESOLVED",
                    unitSource = null,
                    adUnitId = null,
                    errorMessage = "Check ads_enabled, can_request_ads, banner enabled, placement enabled, or unit id."
                )
            }
        }

        return
    }

    val unitSource = if (resolvedBannerConfig.isUsingPlacementAdUnitId) {
        "PLACEMENT"
    } else {
        "GLOBAL"
    }

    if (!initializationState.isComplete) {
        if (bannerConfig.showPlaceholder || shouldShowDebugLabel) {
            ScreenBannerContainer(
                modifier = modifier,
                spacingBefore = spacingBefore,
                spacingAfter = spacingAfter
            ) {
                if (bannerConfig.showPlaceholder) {
                    BannerAdPlaceholder(
                        heightDp = bannerConfig.placeholderHeightDp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (shouldShowDebugLabel) {
                    BannerDebugLabel(
                        placementKey = placement.value,
                        status = "WAITING_INIT",
                        unitSource = unitSource,
                        adUnitId = resolvedBannerConfig.adUnitId,
                        errorMessage = null
                    )
                }
            }
        }

        return
    }

    if (resolvedBannerConfig.adUnitId.isBlank()) {
        if (shouldShowDebugLabel) {
            ScreenBannerContainer(
                modifier = modifier,
                spacingBefore = spacingBefore,
                spacingAfter = spacingAfter
            ) {
                BannerDebugLabel(
                    placementKey = placement.value,
                    status = "UNIT_ID_BLANK",
                    unitSource = unitSource,
                    adUnitId = null,
                    errorMessage = "Resolved config exists but adUnitId is blank."
                )
            }
        }

        return
    }

    val status = when {
        bannerState?.isLoaded == true -> "LOADED"
        bannerState?.isLoading == true -> "LOADING"
        bannerState?.errorMessage != null -> "ERROR"
        else -> "REQUESTING"
    }

    ScreenBannerContainer(
        modifier = modifier,
        spacingBefore = spacingBefore,
        spacingAfter = spacingAfter
    ) {
        BannerSlotAd(
            bannerAdController = dependencies.bannerAdController,
            slotKey = placement.value,
            placement = placement,
            modifier = Modifier.fillMaxWidth(),
            destroyCurrentPlacementOnDispose = destroyCurrentPlacementOnDispose,
            showPlaceholder = bannerConfig.showPlaceholder,
            placeholderHeightDp = bannerConfig.placeholderHeightDp,
            keepPlaceholderOnFailure = bannerConfig.keepPlaceholderOnFailure
        )

        if (shouldShowDebugLabel) {
            BannerDebugLabel(
                placementKey = placement.value,
                status = status,
                unitSource = unitSource,
                adUnitId = resolvedBannerConfig.adUnitId,
                errorMessage = bannerState?.errorMessage
            )
        }
    }
}

@Composable
private fun ScreenBannerContainer(
    modifier: Modifier,
    spacingBefore: Dp,
    spacingAfter: Dp,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (spacingBefore > 0.dp) {
            Spacer(modifier = Modifier.height(spacingBefore))
        }

        content()

        if (spacingAfter > 0.dp) {
            Spacer(modifier = Modifier.height(spacingAfter))
        }
    }
}

@Composable
private fun rememberScreenBannerDependencies(): ScreenBannerDependencies? {
    return remember {
        runCatching {
            val koin = GlobalContext.get()

            ScreenBannerDependencies(
                adsManager = koin.get(),
                bannerAdController = koin.get()
            )
        }.getOrElse { throwable ->
            AdsLogger.w(
                "ScreenBannerAd skipped because Koin dependencies are unavailable. " +
                        "error=${throwable.message}"
            )
            null
        }
    }
}

private data class ScreenBannerDependencies(
    val adsManager: AdsManager,
    val bannerAdController: BannerAdController
)