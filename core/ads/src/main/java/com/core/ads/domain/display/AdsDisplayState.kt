package com.core.ads.domain.display

import com.core.ads.domain.placement.AdFormat
import com.core.ads.domain.placement.AdPlacement

data class AdsDisplayState(
    val isAnyFullScreenAdShowing: Boolean = false,
    val currentFormat: AdFormat? = null,
    val currentPlacement: AdPlacement? = null,
    val startedAtMillis: Long? = null
)