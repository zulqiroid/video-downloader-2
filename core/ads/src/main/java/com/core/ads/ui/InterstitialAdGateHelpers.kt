package com.core.ads.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.core.ads.domain.interstitial.InterstitialAdGate
import com.core.ads.utils.AdsLogger
import org.koin.core.context.GlobalContext
import androidx.compose.runtime.rememberUpdatedState
import com.core.ads.domain.screen.AdScreenKey

@Composable
fun rememberInterstitialAdGate(): InterstitialAdGate? {
    return remember {
        runCatching {
            GlobalContext.get().get<InterstitialAdGate>()
        }.getOrElse { throwable ->
            AdsLogger.w(
                "InterstitialAdGate unavailable. error=${throwable.message}"
            )
            null
        }
    }
}

@Composable
fun rememberAdsActivity(): Activity? {
    val context = LocalContext.current

    return remember(context) {
        context.findActivityOrNull()
    }
}

@Composable
fun rememberInterstitialBackNavigationAction(
    screenKey: AdScreenKey? = null,
    onNavigateBack: () -> Unit
): () -> Unit {
    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()
    val latestOnNavigateBack = rememberUpdatedState(onNavigateBack)

    return remember(
        activity,
        interstitialAdGate,
        screenKey
    ) {
        {
            interstitialAdGate?.showForBackNavigation(
                activity = activity,
                screenKey = screenKey,
                onComplete = {
                    latestOnNavigateBack.value()
                }
            ) ?: latestOnNavigateBack.value()
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