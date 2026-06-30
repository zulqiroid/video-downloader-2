package com.all.video.downloader.fast.hd.secure.video.downloader.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {

     val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

     val LANGUAGE_CODE = stringPreferencesKey("language_code")

     val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

     val DOWNLOAD_LOCATION_TYPE = stringPreferencesKey("download_location_type")
     val DOWNLOAD_LOCATION_TREE_URI = stringPreferencesKey("download_location_tree_uri")

     val VAULT_PIN_HASH = stringPreferencesKey("vault_pin_hash")
     val VAULT_PIN_SALT = stringPreferencesKey("vault_pin_salt")
     val VAULT_BIOMETRIC_ENABLED = booleanPreferencesKey("vault_biometric_enabled")

     val IS_PREMIUM_USER = booleanPreferencesKey("is_premium_user")
     val HAS_LIFETIME_PURCHASE = booleanPreferencesKey("has_lifetime_purchase")
     val PREMIUM_PRODUCT_ID = stringPreferencesKey("premium_product_id")
}