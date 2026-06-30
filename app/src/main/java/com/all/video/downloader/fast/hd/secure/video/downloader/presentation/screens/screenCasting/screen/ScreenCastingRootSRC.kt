package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.events.ScreenCastingEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.events.ScreenCastingNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.viewModel.ScreenCastingViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.activity.compose.BackHandler
import com.core.ads.ui.rememberInterstitialBackNavigationAction
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys

@Composable
fun ScreenCastingRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: ScreenCastingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navigateBackWithInterstitial = rememberInterstitialBackNavigationAction(
        screenKey = VideoDownloaderAdScreenKeys.SCREEN_CASTING
    ) {
        backStack.removeLastOrNull()
    }

    BackHandler {
        viewModel.onEvent(ScreenCastingEvents.BackClicked)
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            ScreenCastingEvents.LocalNetworkStateChanged(
                connected = context.isLocalNetworkConnected()
            )
        )
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collectLatest { event ->
            when (event) {
                ScreenCastingNavEvents.NavigateBack -> {
                    navigateBackWithInterstitial()
                }

                ScreenCastingNavEvents.OpenSystemCastSettings -> {
                    val opened = context.openSystemCastSettings()

                    if (!opened) {
                        viewModel.onSystemCastPanelUnavailable()
                    }
                }

                ScreenCastingNavEvents.OpenWifiSettings -> {
                    context.openWifiSettings()
                }
            }
        }
    }

    DisposableEffect(
        lifecycleOwner,
        state.awaitingSystemCastReturn
    ) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(
                    ScreenCastingEvents.LocalNetworkStateChanged(
                        connected = context.isLocalNetworkConnected()
                    )
                )

                if (state.awaitingSystemCastReturn) {
                    viewModel.onEvent(
                        ScreenCastingEvents.ReturnedFromSystemCastPanel
                    )
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ScreenCastingSRC(
        state = state,
        onEvent = viewModel::onEvent
    )
}

private fun Context.openSystemCastSettings(): Boolean {
    return runCatching {
        val intent = Intent(Settings.ACTION_CAST_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
        true
    }.getOrElse { throwable ->
        throwable !is ActivityNotFoundException && false
    }
}

private fun Context.openWifiSettings() {
    runCatching {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
    }
}

private fun Context.isLocalNetworkConnected(): Boolean {
    return runCatching {
        val connectivityManager = getSystemService(
            ConnectivityManager::class.java
        ) ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val capabilities = connectivityManager.getNetworkCapabilities(
            activeNetwork
        ) ?: return false

        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }.getOrDefault(false)
}