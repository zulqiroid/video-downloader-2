package com.core.ads.data.loading

import com.core.ads.domain.loading.AdLoadingUiController
import com.core.ads.domain.loading.AdLoadingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultAdLoadingUiController : AdLoadingUiController {

    private val mutableState = MutableStateFlow(AdLoadingUiState())

    override val state: StateFlow<AdLoadingUiState> =
        mutableState.asStateFlow()

    override fun show(state: AdLoadingUiState) {
        mutableState.value = state.copy(isVisible = true)
    }

    override fun hide() {
        mutableState.value = AdLoadingUiState(isVisible = false)
    }
}