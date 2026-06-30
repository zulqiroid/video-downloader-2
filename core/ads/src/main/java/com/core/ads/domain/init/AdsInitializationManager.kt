// com/core/ads/domain/init/AdsInitializationManager.kt
package com.core.ads.domain.init

import kotlinx.coroutines.flow.StateFlow

interface AdsInitializationManager {
    val state: StateFlow<AdsInitializationState>
    fun start()
}