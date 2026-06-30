package com.core.ads.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.core.ads.domain.appopen.AppOpenAdController
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.display.FullScreenAdTransitionGuard
import com.core.ads.domain.init.AdsInitializationManager
import com.core.ads.utils.AdsLogger
import java.lang.ref.WeakReference
import com.core.ads.domain.loading.AdLoadingUiController

class AppOpenAdLifecycleObserver(
    private val appOpenAdController: AppOpenAdController,
    private val configStore: AdsConfigStore,
    private val currentActivityProvider: CurrentActivityProvider,
    private val adsInitializationManager: AdsInitializationManager,
    private val lifecyclePolicy: AppOpenAdLifecyclePolicy,
    private val fullScreenAdTransitionGuard: FullScreenAdTransitionGuard,
    private val adLoadingUiController: AdLoadingUiController
) : Application.ActivityLifecycleCallbacks {

    private val mainHandler = Handler(Looper.getMainLooper())

    private var startedActivityCount = 0
    private var isFirstForeground = true
    private var isShowRequestInProgress = false
    private var lastBackgroundAtMillis: Long = 0L

    private var pendingForegroundRunnable: Runnable? = null

    private val config get() = configStore.current

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit

    override fun onActivityStarted(activity: Activity) {
        if (!lifecyclePolicy.shouldTrackActivity(activity)) return

        startedActivityCount++

        if (startedActivityCount == 1) {
            val trigger = if (isFirstForeground) {
                isFirstForeground = false
                AppOpenAdTrigger.COLD_START
            } else {
                AppOpenAdTrigger.APP_FOREGROUND
            }

            onAppForeground(
                activity = activity,
                trigger = trigger
            )
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (!lifecyclePolicy.shouldTrackActivity(activity)) return
        currentActivityProvider.updateIfAllowed(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        if (!lifecyclePolicy.shouldTrackActivity(activity)) return

        startedActivityCount = maxOf(0, startedActivityCount - 1)

        if (startedActivityCount == 0) {
            onAppBackground()
        }
    }

    private fun onAppForeground(
        activity: Activity,
        trigger: AppOpenAdTrigger
    ) {
        val appOpenConfig = config.appOpenAdConfig

        if (!config.canUseAppOpenAd) {
            AdsLogger.d("AppOpen foreground ignored: config disabled or unusable.")
            return
        }

        if (!adsInitializationManager.state.value.isComplete) {
            AdsLogger.d("AppOpen foreground ignored: ads initialization not complete.")
            return
        }

        if (fullScreenAdTransitionGuard.isAppOpenSuppressed()) {
            AdsLogger.d(
                "AppOpen foreground ignored: suppressed after full-screen ad. " +
                        "remaining=${fullScreenAdTransitionGuard.getRemainingSuppressionMillis()}ms"
            )
            appOpenAdController.preload()
            return
        }

        if (isShowRequestInProgress) {
            AdsLogger.d("AppOpen foreground ignored: show request already in progress.")
            return
        }

        if (appOpenAdController.state.value.isShowing) {
            AdsLogger.d("AppOpen foreground ignored: AppOpen already showing.")
            return
        }

        if (!lifecyclePolicy.shouldShowAppOpenAd(activity, trigger)) {
            AdsLogger.d("AppOpen foreground blocked by lifecycle policy. trigger=$trigger")
            appOpenAdController.preload()
            return
        }

        val shouldShowByTrigger = when (trigger) {
            AppOpenAdTrigger.COLD_START -> appOpenConfig.showOnColdStart
            AppOpenAdTrigger.APP_FOREGROUND -> appOpenConfig.showOnAppForeground
        }

        if (!shouldShowByTrigger) {
            AdsLogger.d("AppOpen show disabled for trigger=$trigger. Preloading only.")
            appOpenAdController.preload()
            return
        }

        if (trigger == AppOpenAdTrigger.APP_FOREGROUND) {
            val backgroundDuration = currentTimeMillis() - lastBackgroundAtMillis
            val requiredBackgroundDuration =
                appOpenConfig.minBackgroundDurationBeforeShowMillis

            if (backgroundDuration < requiredBackgroundDuration) {
                AdsLogger.d(
                    "AppOpen foreground ignored: background duration too short. " +
                            "actual=${backgroundDuration}ms, required=${requiredBackgroundDuration}ms"
                )
                appOpenAdController.preload()
                return
            }
        }

        if (!appOpenAdController.state.value.isAvailable) {
            AdsLogger.d("AppOpen not available on foreground. Preloading.")
            appOpenAdController.preload()
            return
        }

        scheduleForegroundShow(
            activity = activity,
            trigger = trigger,
            delayMillis = appOpenConfig.foregroundShowDelayMillis
        )
    }

    private fun onAppBackground() {
        cancelPendingForegroundShow()

        lastBackgroundAtMillis = currentTimeMillis()

        if (!config.canUseAppOpenAd) {
            AdsLogger.d("AppOpen background ignored: config disabled.")
            return
        }

        if (!adsInitializationManager.state.value.isComplete) {
            AdsLogger.d("AppOpen background ignored: ads initialization not complete.")
            return
        }

        AdsLogger.d("App entered background. Preloading AppOpen.")
        appOpenAdController.preload()
    }

    private fun scheduleForegroundShow(
        activity: Activity,
        trigger: AppOpenAdTrigger,
        delayMillis: Long
    ) {
        cancelPendingForegroundShow()

        val activityReference = WeakReference(activity)

        val runnable = Runnable {
            val latestActivity =
                currentActivityProvider.currentActivity
                    ?: activityReference.get()
                    ?: return@Runnable

            showAdIfPossible(
                activity = latestActivity,
                trigger = trigger
            )
        }

        pendingForegroundRunnable = runnable

        if (delayMillis <= 0L) {
            mainHandler.post(runnable)
        } else {
            mainHandler.postDelayed(runnable, delayMillis)
        }
    }

    private fun showAdIfPossible(
        activity: Activity,
        trigger: AppOpenAdTrigger
    ) {
        pendingForegroundRunnable = null

        if (!config.canUseAppOpenAd) {
            AdsLogger.d("AppOpen show skipped: config disabled before delayed show.")
            return
        }

        if (!adsInitializationManager.state.value.isComplete) {
            AdsLogger.d("AppOpen show skipped: initialization incomplete before delayed show.")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            AdsLogger.d("AppOpen show skipped: Activity invalid.")
            appOpenAdController.preload()
            return
        }

        if (!lifecyclePolicy.shouldShowAppOpenAd(activity, trigger)) {
            AdsLogger.d("AppOpen show skipped by lifecycle policy after delay.")
            appOpenAdController.preload()
            return
        }

        if (isShowRequestInProgress) {
            AdsLogger.d("AppOpen show skipped: request already in progress.")
            return
        }

        if (!appOpenAdController.state.value.isAvailable) {
            AdsLogger.d("AppOpen show skipped: ad no longer available.")
            appOpenAdController.preload()
            return
        }


        if (fullScreenAdTransitionGuard.isAppOpenSuppressed()) {
            AdsLogger.d(
                "AppOpen show skipped: suppressed after full-screen ad. " +
                        "remaining=${fullScreenAdTransitionGuard.getRemainingSuppressionMillis()}ms"
            )
            appOpenAdController.preload()
            return
        }

        isShowRequestInProgress = true

        showAppOpenWithOptionalLoading(
            activity = activity,
            trigger = trigger
        )
    }

    private fun showAppOpenWithOptionalLoading(
        activity: Activity,
        trigger: AppOpenAdTrigger
    ) {
        val loadingUiState =
            config.adLoadingDialogConfig.appOpenUiStateOrNull()

        if (loadingUiState == null) {
            showAppOpenNow(
                activity = activity,
                trigger = trigger
            )
            return
        }

        AdsLogger.d(
            "Showing AppOpen loading before ad. duration=${loadingUiState.durationMs}ms"
        )

        adLoadingUiController.show(loadingUiState)

        val delayedShowRunnable = Runnable {
            adLoadingUiController.hide()

            if (!canStillShowAppOpenAfterLoading(activity, trigger)) {
                isShowRequestInProgress = false
                appOpenAdController.preload()
                return@Runnable
            }

            showAppOpenNow(
                activity = activity,
                trigger = trigger
            )
        }

        pendingForegroundRunnable = delayedShowRunnable
        mainHandler.postDelayed(
            delayedShowRunnable,
            loadingUiState.durationMs
        )
    }

    private fun showAppOpenNow(
        activity: Activity,
        trigger: AppOpenAdTrigger
    ) {
        AdsLogger.i("Showing AppOpen ad. trigger=$trigger")

        appOpenAdController.showIfAvailable(activity) { result ->
            isShowRequestInProgress = false

            AdsLogger.d("AppOpen lifecycle show result: $result")

            /**
             * Preload next AppOpen after completion.
             * Controller itself intentionally does not preload immediately.
             */
            appOpenAdController.preload()
        }
    }

    private fun canStillShowAppOpenAfterLoading(
        activity: Activity,
        trigger: AppOpenAdTrigger
    ): Boolean {
        if (!config.canUseAppOpenAd) {
            AdsLogger.d("AppOpen show cancelled after loading: config disabled.")
            return false
        }

        if (!adsInitializationManager.state.value.isComplete) {
            AdsLogger.d("AppOpen show cancelled after loading: initialization incomplete.")
            return false
        }

        if (activity.isFinishing || activity.isDestroyed) {
            AdsLogger.d("AppOpen show cancelled after loading: Activity invalid.")
            return false
        }

        if (!lifecyclePolicy.shouldShowAppOpenAd(activity, trigger)) {
            AdsLogger.d("AppOpen show cancelled after loading by lifecycle policy.")
            return false
        }

        if (!appOpenAdController.state.value.isAvailable) {
            AdsLogger.d("AppOpen show cancelled after loading: ad no longer available.")
            return false
        }

        if (fullScreenAdTransitionGuard.isAppOpenSuppressed()) {
            AdsLogger.d(
                "AppOpen show cancelled after loading: suppressed after full-screen ad. " +
                        "remaining=${fullScreenAdTransitionGuard.getRemainingSuppressionMillis()}ms"
            )
            return false
        }

        return true
    }


    private fun cancelPendingForegroundShow() {
        pendingForegroundRunnable?.let { runnable ->
            mainHandler.removeCallbacks(runnable)
        }
        pendingForegroundRunnable = null
        adLoadingUiController.hide()
    }

    private fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}