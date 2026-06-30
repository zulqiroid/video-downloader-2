package com.core.ads.domain.banner

import com.core.ads.domain.placement.AdPlacement
import com.core.ads.domain.screen.AdScreenKey
import com.core.ads.domain.screen.AdSlotPosition

object BannerPlacementKeys {

    /**
     * Type-safe version.
     *
     * Preferred for app usage:
     *
     * BannerPlacementKeys.fromScreenSlot(
     *     screenKey = VideoDownloaderAdScreenKeys.HOME,
     *     position = AdSlotPosition.BOTTOM
     * )
     */
    fun fromScreenSlot(
        screenKey: AdScreenKey,
        position: AdSlotPosition
    ): AdPlacement {
        return AdPlacement(
            value = "${screenKey.value}_${position.toRemoteSuffix()}"
        )
    }

    /**
     * String version kept for backward compatibility.
     * It safely normalizes raw screen keys.
     */
    fun fromScreenSlot(
        screenKey: String,
        position: AdSlotPosition
    ): AdPlacement? {
        val normalizedScreenKey = AdScreenKey.from(screenKey)
            ?: return null

        return fromScreenSlot(
            screenKey = normalizedScreenKey,
            position = position
        )
    }

    private fun AdSlotPosition.toRemoteSuffix(): String {
        return when (this) {
            AdSlotPosition.TOP -> "top"
            AdSlotPosition.BOTTOM -> "bottom"
        }
    }
}