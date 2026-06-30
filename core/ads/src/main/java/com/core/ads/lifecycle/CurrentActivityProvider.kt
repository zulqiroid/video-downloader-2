package com.core.ads.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

class CurrentActivityProvider(
    private val lifecyclePolicy: AppOpenAdLifecyclePolicy
) : Application.ActivityLifecycleCallbacks {

    private var currentActivityReference: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = currentActivityReference?.get()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityStarted(activity: Activity) {
        updateIfAllowed(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        updateIfAllowed(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        clearIfCurrentActivity(activity)
    }

    fun updateIfAllowed(activity: Activity) {
        if (!lifecyclePolicy.shouldTrackActivity(activity)) return
        currentActivityReference = WeakReference(activity)
    }

    private fun clearIfCurrentActivity(activity: Activity) {
        val current = currentActivityReference?.get()
        if (current === activity) {
            currentActivityReference = null
        }
    }
}