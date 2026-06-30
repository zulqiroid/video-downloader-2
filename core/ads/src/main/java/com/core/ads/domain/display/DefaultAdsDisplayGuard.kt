package com.core.ads.domain.display

import com.core.ads.domain.display.AdsDisplayGuard
import com.core.ads.domain.display.AdsDisplayState
import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement
import com.core.ads.utils.AdsLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultAdsDisplayGuard : AdsDisplayGuard {

    private val mutableState = MutableStateFlow(AdsDisplayState())

    override val state: StateFlow<AdsDisplayState> = mutableState.asStateFlow()

    override fun canShow(
        format: AdFormat,
        placement: AdPlacement
    ): Boolean {
        val current = mutableState.value

        if (!isFullScreenFormat(format)) {
            return true
        }

        return !current.isAnyFullScreenAdShowing
    }

    override fun markShowing(
        format: AdFormat,
        placement: AdPlacement
    ): Boolean {
        if (!isFullScreenFormat(format)) {
            return true
        }

        val current = mutableState.value

        if (current.isAnyFullScreenAdShowing) {
            AdsLogger.w(
                "Full-screen ad blocked by display guard. " +
                        "Requested=$format/${placement.value}, " +
                        "Current=${current.currentFormat}/${current.currentPlacement?.value}"
            )
            return false
        }

        mutableState.value = AdsDisplayState(
            isAnyFullScreenAdShowing = true,
            currentFormat = format,
            currentPlacement = placement,
            startedAtMillis = System.currentTimeMillis()
        )

        AdsLogger.d("Display guard marked showing: $format/${placement.value}")

        return true
    }

    override fun markFinished(
        format: AdFormat,
        placement: AdPlacement
    ) {
        val current = mutableState.value

        /**
         * Only the currently showing ad should clear the guard.
         *
         * This prevents an old/delayed callback from clearing the guard
         * while another ad has already started.
         */
        val isSameAd =
            current.currentFormat == format &&
                    current.currentPlacement == placement

        if (isSameAd) {
            mutableState.value = AdsDisplayState()
            AdsLogger.d("Display guard cleared: $format/${placement.value}")
        } else {
            AdsLogger.w(
                "Display guard finish ignored. " +
                        "Finished=$format/${placement.value}, " +
                        "Current=${current.currentFormat}/${current.currentPlacement?.value}"
            )
        }
    }

    override fun clear() {
        mutableState.value = AdsDisplayState()
        AdsLogger.d("Display guard force cleared")
    }

    private fun isFullScreenFormat(format: AdFormat): Boolean {
        return format == AdFormat.APP_OPEN ||
                format == AdFormat.INTERSTITIAL ||
                format == AdFormat.REWARDED
    }
}