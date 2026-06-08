package com.video.downloader.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {

     val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

     val LANGUAGE_CODE = stringPreferencesKey("language_code")


     val VAULT_PIN_HASH = stringPreferencesKey("vault_pin_hash")
     val VAULT_PIN_SALT = stringPreferencesKey("vault_pin_salt")
     val VAULT_BIOMETRIC_ENABLED = booleanPreferencesKey("vault_biometric_enabled")

}