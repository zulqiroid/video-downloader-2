package com.core.ads.data.config

import com.core.ads.domain.AdsCoreConfig
import com.core.ads.domain.config.AdsConfigStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultAdsConfigStore(
    initialConfig: AdsCoreConfig = AdsCoreConfig(adsEnabled = false)
) : AdsConfigStore {

    private val mutableState = MutableStateFlow(initialConfig)

    override val state: StateFlow<AdsCoreConfig> = mutableState.asStateFlow()

    override val current: AdsCoreConfig
        get() = mutableState.value

    override fun update(config: AdsCoreConfig) {
        mutableState.value = config
    }

}