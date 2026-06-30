package com.core.ads.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.core.ads.domain.nativead.NativeAdBinding
import com.core.ads.domain.nativead.NativeAdController
import com.core.ads.domain.placement.AdPlacement
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun NativeAd(
    nativeAdController: NativeAdController,
    placement: AdPlacement = AdPlacement.DEFAULT_NATIVE,
    modifier: Modifier = Modifier,
    fillContainer: Boolean = false,
    loadingContent: (@Composable () -> Unit)? = null,
    errorContent: (@Composable (String) -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() } ?: return

    var binding by remember(nativeAdController, placement.value) {
        mutableStateOf<NativeAdBinding?>(null)
    }

    var isLoading by remember(nativeAdController, placement.value) {
        mutableStateOf(false)
    }

    var errorMessage by remember(nativeAdController, placement.value) {
        mutableStateOf<String?>(null)
    }

    val isDisposed = remember(nativeAdController, placement.value) {
        AtomicBoolean(false)
    }

    val latestBinding by rememberUpdatedState(binding)

    DisposableEffect(nativeAdController, placement.value) {
        isDisposed.set(false)

        onDispose {
            isDisposed.set(true)

            nativeAdController.cancel(placement)

            latestBinding?.destroy()
        }
    }

    LaunchedEffect(nativeAdController, placement.value, activity) {
        isLoading = true
        errorMessage = null

        nativeAdController.loadAd(
            activity = activity,
            placement = placement,
            onLoaded = { loadedBinding ->
                if (isDisposed.get()) {
                    loadedBinding.destroy()
                    return@loadAd
                }

                binding?.destroy()
                binding = loadedBinding

                isLoading = false
                errorMessage = null
            },
            onError = { error ->
                if (isDisposed.get()) return@loadAd

                binding?.destroy()
                binding = null

                isLoading = false
                errorMessage = error
            }
        )
    }

    when {
        binding != null -> {
            NativeAdAndroidView(
                binding = binding,
                fillContainer = fillContainer,
                modifier = modifier
            )
        }

        isLoading && loadingContent != null -> {
            loadingContent()
        }

        errorMessage != null && errorContent != null -> {
            errorContent(errorMessage.orEmpty())
        }

        else -> {
            Box(modifier = modifier)
        }
    }
}

@Composable
private fun NativeAdAndroidView(
    binding: NativeAdBinding?,
    fillContainer: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            FrameLayout(context)
        },
        modifier = modifier,
        update = { container ->
            val adView = binding?.getAdView()

            if (adView == null) {
                container.removeAllViews()
                return@AndroidView
            }

            val currentParent = adView.parent as? ViewGroup

            if (currentParent === container) {
                return@AndroidView
            }

            currentParent?.removeView(adView)
            container.removeAllViews()

            container.addView(
                adView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    if (fillContainer) {
                        ViewGroup.LayoutParams.MATCH_PARENT
                    } else {
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                )
            )
        }
    )
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}