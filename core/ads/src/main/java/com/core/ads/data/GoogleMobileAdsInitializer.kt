package com.core.ads.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.core.ads.domain.AdsInitializer
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.init.MutableAdsSdkStateProvider
import com.core.ads.domain.policy.AdsConsentPolicy
import com.core.ads.domain.policy.AdsUserPolicy
import com.core.ads.utils.AdsLogger
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.concurrent.atomic.AtomicBoolean

class GoogleMobileAdsInitializer(
    context: Context,
    private val configStore: AdsConfigStore,
    private val adsUserPolicy: AdsUserPolicy,
    private val adsConsentPolicy: AdsConsentPolicy,
    private val sdkStateProvider: MutableAdsSdkStateProvider
) : AdsInitializer {

    private val appContext: Context = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private val started = AtomicBoolean(false)
    private val callbackLock = Any()
    private val pendingCallbacks = mutableListOf<() -> Unit>()

    override fun initialize(onReady: () -> Unit) {
        runOnMainThread {
            val config = configStore.current

            AdsLogger.d(
                "MobileAds.initialize() check -> " +
                        "adsEnabled=${config.adsEnabled}, " +
                        "canRequestAds=${config.canRequestAds}, " +
                        "userCanShow=${adsUserPolicy.canShowAds()}, " +
                        "consentCanRequest=${adsConsentPolicy.canRequestAds()}"
            )

            if (!config.adsEnabled) {
                val message = "SDK initialization blocked: adsEnabled=false."
                AdsLogger.w(message)
                sdkStateProvider.markBlocked(message)
                onReady()
                return@runOnMainThread
            }

            if (!config.canRequestAds) {
                val message = "SDK initialization blocked: canRequestAds=false."
                AdsLogger.w(message)
                sdkStateProvider.markBlocked(message)
                onReady()
                return@runOnMainThread
            }

            /**
             * Premium/remove-ads user.
             *
             * Important:
             * We do not initialize SDK only for ads if the user should never see ads.
             */
            if (!adsUserPolicy.canShowAds()) {
                val message = "SDK initialization blocked: user is premium/remove-ads or user policy blocked ads."
                AdsLogger.w(message)
                sdkStateProvider.markBlocked(message)
                onReady()
                return@runOnMainThread
            }

            if (!adsConsentPolicy.canRequestAds()) {
                val message = "SDK initialization blocked: consent policy blocked ad requests."
                AdsLogger.w(message)
                sdkStateProvider.markBlocked(message)
                onReady()
                return@runOnMainThread
            }

            if (sdkStateProvider.current.isInitialized) {
                AdsLogger.d("Google Mobile Ads SDK already initialized.")
                onReady()
                return@runOnMainThread
            }

            addPendingCallback(onReady)

            if (!started.compareAndSet(false, true)) {
                AdsLogger.d("Google Mobile Ads SDK initialization already in progress.")
                return@runOnMainThread
            }

            startSdkInitialization()
        }
    }

    private fun startSdkInitialization() {
        runCatching {
            sdkStateProvider.markInitializing("Initializing Google Mobile Ads SDK.")
            AdsLogger.i("Initializing Google Mobile Ads SDK")

            val config = configStore.current

            if (config.isDebug) {
                val testDeviceIds = listOf("A5C452E335A8BB6E22DDB5551C352F80")

                val requestConfig = RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build()

                MobileAds.setRequestConfiguration(requestConfig)
                AdsLogger.d("Test device IDs configured: $testDeviceIds")
            }

            MobileAds.initialize(appContext) {
                runOnMainThread {
                    sdkStateProvider.markInitialized("Google Mobile Ads SDK initialized.")
                    AdsLogger.i("Google Mobile Ads SDK initialized")
                    notifyPendingCallbacks()
                }
            }
        }.onFailure { throwable ->
            runOnMainThread {
                val message = "Google Mobile Ads SDK initialization failed: ${throwable.message}"
                AdsLogger.e(message, throwable)
                sdkStateProvider.markFailed(message)
                started.set(false)
                notifyPendingCallbacks()
            }
        }
    }

    private fun addPendingCallback(callback: () -> Unit) {
        synchronized(callbackLock) {
            pendingCallbacks.add(callback)
        }
    }

    private fun notifyPendingCallbacks() {
        val callbacks = synchronized(callbackLock) {
            pendingCallbacks.toList().also {
                pendingCallbacks.clear()
            }
        }

        callbacks.forEach { callback ->
            runCatching { callback() }
        }
    }

    private fun runOnMainThread(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }
}