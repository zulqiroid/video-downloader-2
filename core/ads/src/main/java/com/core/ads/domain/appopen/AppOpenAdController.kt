package com.core.ads.domain.appopen

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

/**
 * AppOpen ad is app-level.
 *
 * There are two startup paths:
 * - Normal resume/background → foreground AppOpen
 * - Splash/startup AppOpen
 */
interface AppOpenAdController {

    val state: StateFlow<AppOpenAdState>

    /**
     * Preloads normal resume/background-to-foreground AppOpen ad.
     */
    fun preload()

    /**
     * Preloads splash/startup AppOpen ad.
     *
     * Uses splashAdUnitId when available, otherwise falls back to normal adUnitId.
     */
    fun preloadForSplash()

    fun showIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit
    )

    /**
     * Shows splash/startup AppOpen only when the cached ad matches splash/startup unit.
     *
     * Important:
     * If splash ad is not ready, this must not block navigation.
     */
    fun showSplashIfAvailable(
        activity: Activity,
        onComplete: (AppOpenAdShowResult) -> Unit
    )

    fun clear()
}