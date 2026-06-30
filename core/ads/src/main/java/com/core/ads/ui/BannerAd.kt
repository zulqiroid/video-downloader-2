package com.core.ads.ui

import android.app.Activity
import android.widget.FrameLayout
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.core.ads.domain.banner.BannerAdController
import com.core.ads.domain.placement.AdPlacement

private const val MIN_BANNER_WIDTH_DP = 320
private const val BANNER_MIN_HEIGHT_DP = 50
private const val BANNER_MAX_HEIGHT_DP = 100

@Composable
fun BannerAd(
    bannerAdController: BannerAdController,
    placement: AdPlacement = AdPlacement.DEFAULT_BANNER,
    modifier: Modifier = Modifier,
    destroyOnDispose: Boolean = false
) {
    val activity = LocalContext.current as? Activity ?: return
    val lifecycleOwner = LocalLifecycleOwner.current

    /**
     * Container is stable for this placement instance.
     */
    val container = remember(
        activity,
        placement.value
    ) {
        FrameLayout(activity)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(
                min = BANNER_MIN_HEIGHT_DP.dp,
                max = BANNER_MAX_HEIGHT_DP.dp
            )
    ) {
        val adWidthDp = maxWidth.value
            .toInt()
            .coerceAtLeast(MIN_BANNER_WIDTH_DP)

        DisposableEffect(
            bannerAdController,
            lifecycleOwner,
            placement.value,
            container
        ) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        bannerAdController.resume(placement)
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        bannerAdController.pause(placement)
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        bannerAdController.destroy(placement)
                    }

                    else -> Unit
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)

                if (destroyOnDispose) {
                    bannerAdController.destroy(placement)
                } else {
                    /**
                     * Very important:
                     * detach only if this exact container is still attached.
                     */
                    bannerAdController.detach(
                        placement = placement,
                        container = container
                    )
                }
            }
        }

        LaunchedEffect(
            bannerAdController,
            placement.value,
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

        AndroidView(
            factory = { container },
            modifier = Modifier.fillMaxWidth()
        )
    }
}