package com.core.ads.domain.loading

import kotlinx.coroutines.flow.StateFlow

interface AdLoadingUiController {

    val state: StateFlow<AdLoadingUiState>

    fun show(state: AdLoadingUiState)

    fun hide()
}