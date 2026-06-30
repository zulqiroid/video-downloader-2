package com.core.ads.utils

import android.util.Log

object AdsLogger {

    private const val TAG = "AdsCore"

    var isDebug = true // toggle from config later

    fun d(message: String) {
        if (isDebug) Log.d(TAG, message)
    }

    fun i(message: String) {
        if (isDebug) Log.i(TAG, message)
    }

    fun w(message: String) {
        if (isDebug) Log.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (isDebug) Log.e(TAG, message, throwable)
    }
}