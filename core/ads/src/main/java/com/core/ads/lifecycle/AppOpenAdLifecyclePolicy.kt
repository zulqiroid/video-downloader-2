package com.core.ads.lifecycle

import android.app.Activity

interface AppOpenAdLifecyclePolicy {

    /**
     * Whether this Activity should be tracked as current activity.
     */
    fun shouldTrackActivity(activity: Activity): Boolean

    /**
     * Whether lifecycle observer is allowed to request AppOpen show.
     *
     * App module can override this later if needed.
     */
    fun shouldShowAppOpenAd(
        activity: Activity,
        trigger: AppOpenAdTrigger
    ): Boolean
}