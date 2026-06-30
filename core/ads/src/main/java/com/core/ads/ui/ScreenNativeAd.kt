package com.core.ads.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.core.ads.domain.AdContentPosition
import com.core.ads.domain.NativeAdSize
import com.core.ads.domain.ResolvedNativeAdConfig
import com.core.ads.domain.manager.AdsManager
import com.core.ads.domain.nativead.NativeAdController
import com.core.ads.domain.nativead.NativeAdPlacementKeys
import com.core.ads.domain.screen.AdScreenKey
import com.core.ads.domain.screen.AdSlotPosition
import com.core.ads.utils.AdsLogger
import org.koin.core.context.GlobalContext

/**
 * Inline native ad host.
 *
 * Use this for normal app screens:
 * - Home
 * - Player
 * - Files
 * - Vault
 * - More
 * - Tool screens
 * - Result screens
 *
 * Remote Config decides:
 * - Top / Bottom
 * - Small / Medium
 */
@Composable
fun ScreenNativeAd(
    screenKey: AdScreenKey,
    position: AdSlotPosition,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    spacingBefore: Dp = 0.dp,
    spacingAfter: Dp = 0.dp
) {
    ScreenNativeAd(
        placementKey = screenKey.value,
        position = position,
        modifier = modifier,
        visible = visible,
        spacingBefore = spacingBefore,
        spacingAfter = spacingAfter
    )
}

@Composable
fun ScreenNativeAd(
    placementKey: String,
    position: AdSlotPosition,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    spacingBefore: Dp = 0.dp,
    spacingAfter: Dp = 0.dp
) {
    if (!visible) return

    val dependencies = rememberScreenNativeAdDependencies()
        ?: return

    val adsConfig by dependencies.adsManager.configState.collectAsState()
    val initializationState by dependencies.adsManager.initializationState.collectAsState()

    val placement = remember(placementKey) {
        NativeAdPlacementKeys.fromRaw(placementKey)
    } ?: return

    val resolvedNativeConfig = remember(
        adsConfig,
        placement
    ) {
        adsConfig.resolveNativeConfig(placement)
    } ?: return

    if (!resolvedNativeConfig.isInlinePlacement(position)) {
        return
    }

    if (!initializationState.isComplete) {
        if (resolvedNativeConfig.showPlaceholder) {
            ScreenNativeAdContainer(
                modifier = modifier,
                spacingBefore = spacingBefore,
                spacingAfter = spacingAfter
            ) {
                NativeAdPlaceholder(
                    size = resolvedNativeConfig.size,
                    styleConfig = resolvedNativeConfig.styleConfig,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        return
    }

    ScreenNativeAdContainer(
        modifier = modifier,
        spacingBefore = spacingBefore,
        spacingAfter = spacingAfter
    ) {
        NativeAd(
            nativeAdController = dependencies.nativeAdController,
            placement = placement,
            modifier = Modifier.fillMaxWidth(),
            fillContainer = false,
            loadingContent = if (resolvedNativeConfig.showPlaceholder) {
                {
                    NativeAdPlaceholder(
                        size = resolvedNativeConfig.size,
                        styleConfig = resolvedNativeConfig.styleConfig,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                null
            },
            errorContent = null
        )
    }
}

/**
 * Full-page native ad host.
 *
 * Use this only for onboarding last page.
 *
 * Remote Config should be:
 * position = FullPage
 * style = Large
 */
@Composable
fun ScreenNativeFullPageAd(
    screenKey: AdScreenKey,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    ScreenNativeFullPageAd(
        placementKey = screenKey.value,
        modifier = modifier,
        visible = visible
    )
}

@Composable
fun ScreenNativeFullPageAd(
    placementKey: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    if (!visible) return

    val dependencies = rememberScreenNativeAdDependencies()
        ?: return

    val adsConfig by dependencies.adsManager.configState.collectAsState()
    val initializationState by dependencies.adsManager.initializationState.collectAsState()

    val placement = remember(placementKey) {
        NativeAdPlacementKeys.fromRaw(placementKey)
    } ?: return

    val resolvedNativeConfig = remember(
        adsConfig,
        placement
    ) {
        adsConfig.resolveNativeConfig(placement)
    } ?: return

    if (!resolvedNativeConfig.isFullPagePlacement()) {
        return
    }

    if (!initializationState.isComplete) {
        if (resolvedNativeConfig.showPlaceholder) {
            NativeAdPlaceholder(
                size = NativeAdSize.LARGE,
                styleConfig = resolvedNativeConfig.styleConfig,
                fillContainer = true,
                modifier = modifier.fillMaxSize()
            )
        }

        return
    }

    NativeAd(
        nativeAdController = dependencies.nativeAdController,
        placement = placement,
        modifier = modifier.fillMaxSize(),
        fillContainer = true,
        loadingContent = if (resolvedNativeConfig.showPlaceholder) {
            {
                NativeAdPlaceholder(
                    size = NativeAdSize.LARGE,
                    styleConfig = resolvedNativeConfig.styleConfig,
                    fillContainer = true,
                    modifier = modifier.fillMaxSize()
                )
            }
        } else {
            null
        },
        errorContent = null
    )
}

@Composable
private fun ScreenNativeAdContainer(
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
private fun rememberScreenNativeAdDependencies(): ScreenNativeAdDependencies? {
    return remember {
        runCatching {
            val koin = GlobalContext.get()

            ScreenNativeAdDependencies(
                adsManager = koin.get(),
                nativeAdController = koin.get()
            )
        }.getOrElse { throwable ->
            AdsLogger.w(
                "ScreenNativeAd skipped because Koin dependencies are unavailable. " +
                        "error=${throwable.message}"
            )
            null
        }
    }
}

private fun ResolvedNativeAdConfig.isInlinePlacement(
    requestedPosition: AdSlotPosition
): Boolean {
    val resolvedPosition = when (position) {
        AdContentPosition.TOP -> AdSlotPosition.TOP
        AdContentPosition.BOTTOM -> AdSlotPosition.BOTTOM
        AdContentPosition.FULL_PAGE -> return false
    }

    if (resolvedPosition != requestedPosition) {
        return false
    }

    return size == NativeAdSize.SMALL ||
            size == NativeAdSize.MEDIUM
}

private fun ResolvedNativeAdConfig.isFullPagePlacement(): Boolean {
    return position == AdContentPosition.FULL_PAGE &&
            size == NativeAdSize.LARGE
}

private data class ScreenNativeAdDependencies(
    val adsManager: AdsManager,
    val nativeAdController: NativeAdController
)