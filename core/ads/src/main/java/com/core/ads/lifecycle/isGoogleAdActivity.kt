package com.core.ads.lifecycle

import android.app.Activity

internal fun Activity.isGoogleAdActivity(): Boolean {
    val name = javaClass.name
    return name == "com.google.android.gms.ads.AdActivity" ||
            name.endsWith(".AdActivity")
}