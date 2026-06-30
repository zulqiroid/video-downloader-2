package com.core.ads.domain.init

import kotlinx.coroutines.flow.StateFlow

interface AdsSdkStateProvider {
    val state: StateFlow<AdsSdkState>
    val current: AdsSdkState
}