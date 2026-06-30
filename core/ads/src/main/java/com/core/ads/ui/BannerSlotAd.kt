package com.core.ads.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.core.ads.domain.banner.BannerAdController
import com.core.ads.domain.banner.BannerAdState
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.utils.AdsLogger

private const val MIN_BANNER_WIDTH_DP = 320
private const val BANNER_MIN_HEIGHT_DP = 50
private const val BANNER_MAX_HEIGHT_DP = 100

@Composable
fun BannerSlotAd(
    bannerAdController: BannerAdController,
    slotKey: String,
    placement: AdPlacement,
    modifier: Modifier = Modifier,
    destroyCurrentPlacementOnDispose: Boolean = false,
    showPlaceholder: Boolean = false,
    placeholderHeightDp: Int = 60,
    keepPlaceholderOnFailure: Boolean = false
) {
    val context = LocalContext.current

    val activity = remember(context) {
        context.findActivityOrNull()
    } ?: run {
        AdsLogger.w(
            "BannerSlotAd skipped because Activity is unavailable. placement=${placement.value}"
        )
        return
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val placementKey = placement.value

    val states by bannerAdController.states.collectAsState()
    val bannerState = states[placementKey] ?: BannerAdState()

    val hasFailed =
        bannerState.errorMessage != null &&
                !bannerState.isLoaded

    val shouldCollapse =
        hasFailed &&
                !keepPlaceholderOnFailure &&
                !bannerState.isLoaded

    val shouldShowPlaceholder =
        showPlaceholder &&
                !bannerState.isLoaded &&
                (!hasFailed || keepPlaceholderOnFailure)

    /**
     * Important:
     * Container must be tied to placementKey.
     *
     * In Compose tab switching, same composable position is reused:
     * home_bottom -> player_bottom -> home_bottom
     *
     * If container is not keyed by placement, AndroidView can keep showing
     * an old empty host while controller attaches AdView into a newer host.
     */
    val container = remember(
        activity,
        slotKey,
        placementKey
    ) {
        FrameLayout(activity).apply {
            clipChildren = false
            clipToPadding = false
        }
    }

    BoxWithConstraints(
        modifier = if (shouldCollapse) {
            modifier.fillMaxWidth()
        } else {
            modifier
                .fillMaxWidth()
                .heightIn(
                    min = BANNER_MIN_HEIGHT_DP.dp,
                    max = BANNER_MAX_HEIGHT_DP.dp
                )
        }
    ) {
        val adWidthDp = maxWidth.value
            .toInt()
            .coerceAtLeast(MIN_BANNER_WIDTH_DP)

        /**
         * Capture exact placement for this effect.
         *
         * Do NOT use rememberUpdatedState for cleanup here.
         * onDispose should detach the placement that this effect attached,
         * not whatever placement became latest after tab switch.
         */
        DisposableEffect(
            bannerAdController,
            lifecycleOwner,
            placementKey,
            container
        ) {
            val effectPlacement = placement

            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        bannerAdController.resume(effectPlacement)
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        bannerAdController.pause(effectPlacement)
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        bannerAdController.destroy(effectPlacement)
                    }

                    else -> Unit
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)

                if (destroyCurrentPlacementOnDispose) {
                    bannerAdController.destroy(effectPlacement)
                } else {
                    bannerAdController.detach(
                        placement = effectPlacement,
                        container = container
                    )
                }
            }
        }

        LaunchedEffect(
            bannerAdController,
            placementKey,
            adWidthDp,
            activity,
            container
        ) {
            bannerAdController.loadAndAttach(
                activity = activity,
                container = container,
                placement = placement,
                adWidthDp = adWidthDp
            )
        }

        if (!shouldCollapse) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (shouldShowPlaceholder) {
                    BannerAdPlaceholder(
                        heightDp = placeholderHeightDp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                /**
                 * Critical fix:
                 * AndroidView must be keyed by placementKey + container.
                 *
                 * Without this, Compose may reuse old AndroidView host when tab changes,
                 * causing controller to attach AdView to one container while UI displays
                 * another old/empty container.
                 */
                key(placementKey, container) {
                    AndroidView(
                        factory = {
                            container
                        },
                        modifier = Modifier.fillMaxWidth(),
                        update = {
                            /**
                             * No-op.
                             *
                             * Controller owns adding/removing the actual AdView.
                             * This AndroidView only hosts the placement container.
                             */
                        }
                    )
                }
            }
        }
    }
}

private tailrec fun Context.findActivityOrNull(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivityOrNull()
        else -> null
    }
}