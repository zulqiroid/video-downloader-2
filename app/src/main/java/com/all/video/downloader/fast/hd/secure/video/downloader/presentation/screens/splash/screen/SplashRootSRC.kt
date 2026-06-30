package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.core.ads.domain.appopen.AppOpenAdShowResult
import com.core.ads.domain.manager.AdsManager
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.events.SplashNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.states.SplashStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.viewModel.SplashViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.context.GlobalContext
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun SplashRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latestState by rememberUpdatedState(state)

    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val adsManager = remember {
        GlobalContext.get().get<AdsManager>()
    }

    val navigationConsumed = remember {
        AtomicBoolean(false)
    }

    LaunchedEffect(viewModel, lifecycleOwner.lifecycle, adsManager, activity) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navEvents.collect { navEvent ->
                when (navEvent) {
                    SplashNavEvents.NavigateToMain -> {
                        if (!navigationConsumed.compareAndSet(false, true)) {
                            Timber.d("Splash navigation already consumed. Ignoring duplicate event.")
                            return@collect
                        }

                        showSplashAppOpenThenNavigate(
                            adsManager = adsManager,
                            activity = activity,
                            onNavigate = {
                                navigateAfterSplash(
                                    state = state,
                                    backStack = backStack,
                                    isOnBoardingCompleted = latestState.isOnBoardingCompleted
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    SplashSRC(
        state = state,
        onEvent = viewModel::onEvent
    )
}

private suspend fun showSplashAppOpenThenNavigate(
    adsManager: AdsManager,
    activity: Activity?,
    onNavigate: () -> Unit
) {
    val adsConfig = adsManager.currentConfig
    val appOpenConfig = adsConfig.appOpenAdConfig

    val canTrySplashAppOpen =
        adsConfig.canUseAppOpenAd &&
                appOpenConfig.showOnSplash

    if (!canTrySplashAppOpen) {
        Timber.d(
            "Splash AppOpen skipped. canUseAppOpenAd=%s, showOnSplash=%s",
            adsConfig.canUseAppOpenAd,
            appOpenConfig.showOnSplash
        )
        onNavigate()
        return
    }

    if (activity == null || activity.isFinishing || activity.isDestroyed) {
        Timber.d("Splash AppOpen skipped. Activity is not available or finishing.")
        onNavigate()
        return
    }

    waitForSplashAppOpenReadiness(
        adsManager = adsManager,
        timeoutMillis = SPLASH_APP_OPEN_WAIT_TIMEOUT_MS
    )

    adsManager.showSplashAppOpenIfAvailable(
        activity = activity,
        onComplete = { result ->
            when (result) {
                AppOpenAdShowResult.Shown -> {
                    Timber.d("Splash AppOpen shown and completed.")
                }

                AppOpenAdShowResult.NotAvailable -> {
                    Timber.d("Splash AppOpen not available after wait. Continuing navigation.")
                }

                is AppOpenAdShowResult.Skipped -> {
                    Timber.d("Splash AppOpen skipped. reason=%s", result.reason)
                }

                is AppOpenAdShowResult.Failed -> {
                    Timber.w("Splash AppOpen failed. reason=%s", result.reason)
                }
            }

            onNavigate()
        }
    )
}

private suspend fun waitForSplashAppOpenReadiness(
    adsManager: AdsManager,
    timeoutMillis: Long
) {
    val currentState = adsManager.appOpenState.value

    if (currentState.isAvailable) {
        Timber.d("Splash AppOpen already available. No wait needed.")
        return
    }

    Timber.d("Splash AppOpen not available. Preloading and waiting up to %sms.", timeoutMillis)

    adsManager.preloadSplashAppOpen()

    val loadedWithinTimeout = withTimeoutOrNull(timeoutMillis) {
        adsManager.appOpenState
            .filter { state ->
                state.isAvailable || (!state.isLoading && state.lastErrorMessage != null)
            }
            .first()
    }

    if (loadedWithinTimeout?.isAvailable == true) {
        Timber.d("Splash AppOpen became available within wait timeout.")
    } else {
        Timber.d("Splash AppOpen wait timeout reached. Continuing without blocking.")
    }
}

private fun navigateAfterSplash(
    backStack: NavBackStack<NavKey>,
    isOnBoardingCompleted: Boolean,
    state: SplashStates
) {
    backStack.removeLastOrNull()

    if (isOnBoardingCompleted) {
        backStack.add(Screen.Main)
        if (state.isPremiumEnable){
            backStack.add(Screen.Premium)
        }
    } else {
        backStack.add(Screen.AppLanguage)
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private const val SPLASH_APP_OPEN_WAIT_TIMEOUT_MS = 2_500L