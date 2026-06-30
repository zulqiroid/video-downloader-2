package com.core.ads.domain.consent

import android.app.Activity
import android.app.Application

/**
 * ConsentInitializer UMP ya kisi bhi consent SDK ko initialize karne ka contract hai.
 *
 * App module ko iska implementation provide nahi karna padega,
 * Ads module khud ise provide karega.
 */
interface ConsentInitializer {
    /**
     * Consent flow initialize karega aur zaroorat hone par consent form dikhayega.
     *
      * @param onComplete Consent ready hone ke baad callback.
     */
    fun initialize(onComplete: () -> Unit)
}