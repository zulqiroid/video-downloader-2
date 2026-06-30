package com.core.ads.lifecycle

import android.app.Activity

class DefaultAppOpenAdLifecyclePolicy : AppOpenAdLifecyclePolicy {

    override fun shouldTrackActivity(activity: Activity): Boolean {
        return !activity.isGoogleAdActivity()
    }

    override fun shouldShowAppOpenAd(
        activity: Activity,
        trigger: AppOpenAdTrigger
    ): Boolean {
        return !activity.isGoogleAdActivity()
    }
}