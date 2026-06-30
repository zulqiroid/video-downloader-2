package com.core.ads.domain.config

import com.core.ads.domain.AdsCoreConfig
import kotlinx.coroutines.flow.StateFlow



interface AdsConfigStore {
    val current: AdsCoreConfig
    val state: StateFlow<AdsCoreConfig>
    fun update(config: AdsCoreConfig)
}