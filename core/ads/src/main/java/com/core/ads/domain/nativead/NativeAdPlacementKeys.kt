package com.core.ads.domain.nativead

import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.screen.AdScreenKey

object NativeAdPlacementKeys {

    fun fromScreen(
        screenKey: AdScreenKey
    ): AdPlacement {
        return AdPlacement(
            value = screenKey.value
        )
    }

    fun fromRaw(
        rawPlacementKey: String
    ): AdPlacement? {
        return AdScreenKey
            .from(rawPlacementKey)
            ?.let { normalizedKey ->
                AdPlacement(normalizedKey.value)
            }
    }
}