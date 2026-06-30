package com.core.ads.data.consent

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.core.ads.domain.consent.ConsentInitializer
import com.core.ads.lifecycle.CurrentActivityProvider
import com.core.ads.utils.AdsLogger
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.core.ads.domain.config.AdsConfigStore

class GoogleUmpConsentInitializer(
    private val currentActivityProvider: CurrentActivityProvider,
    private val consentPolicy: UmpConsentPolicy,
    private val configStore: AdsConfigStore
) : ConsentInitializer {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun initialize(onComplete: () -> Unit) {
        waitForActivity(attemptsLeft = 20, onComplete = onComplete)
    }

    private fun waitForActivity(attemptsLeft: Int, onComplete: () -> Unit) {
        val activity = currentActivityProvider.currentActivity
        if (activity == null) {
            if (attemptsLeft <= 0) {
                AdsLogger.w("Consent init skipped: no activity available")
                consentPolicy.update(canRequestAds = false, canRequestPersonalizedAds = false)
                onComplete()
                return
            }

            mainHandler.postDelayed({
                waitForActivity(attemptsLeft - 1, onComplete)
            }, 250)
            return
        }

        startConsentFlow(activity, onComplete)
    }

    private fun startConsentFlow(activity: Activity, onComplete: () -> Unit) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        val paramsBuilder = ConsentRequestParameters.Builder()

        if (configStore.current.isDebug) {
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("YOUR_TEST_DEVICE_HASHED_ID")
                .build()
            paramsBuilder.setConsentDebugSettings(debugSettings)
        }

        consentInformation.requestConsentInfoUpdate(
            activity,
            paramsBuilder.build(),
            {
                if (consentInformation.isConsentFormAvailable) {
                    UserMessagingPlatform.loadConsentForm(
                        activity,
                        { form ->
                            form.show(activity) {
                                syncConsent(consentInformation)
                                onComplete()
                            }
                        },
                        {
                            syncConsent(consentInformation)
                            onComplete()
                        }
                    )
                } else {
                    syncConsent(consentInformation)
                    onComplete()
                }
            },
            {
                consentPolicy.update(canRequestAds = false, canRequestPersonalizedAds = false)
                onComplete()
            }
        )
    }

    private fun syncConsent(consentInformation: ConsentInformation) {
        val canRequest = consentInformation.canRequestAds()
        consentPolicy.update(
            canRequestAds = canRequest,
            canRequestPersonalizedAds = canRequest
        )
        AdsLogger.i("Consent policy updated: canRequestAds=$canRequest")
    }
}