package com.core.ads.data.init

import com.core.ads.domain.init.AdsSdkState
import com.core.ads.domain.init.AdsSdkStatus
import com.core.ads.domain.init.MutableAdsSdkStateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultAdsSdkStateProvider : MutableAdsSdkStateProvider {

    private val mutableState = MutableStateFlow(AdsSdkState())

    override val state: StateFlow<AdsSdkState> = mutableState.asStateFlow()

    override val current: AdsSdkState
        get() = mutableState.value

    override fun markNotInitialized(message: String?) {
        update(
            status = AdsSdkStatus.NOT_INITIALIZED,
            message = message
        )
    }

    override fun markInitializing(message: String?) {
        update(
            status = AdsSdkStatus.INITIALIZING,
            message = message
        )
    }

    override fun markInitialized(message: String?) {
        update(
            status = AdsSdkStatus.INITIALIZED,
            message = message
        )
    }

    override fun markBlocked(message: String) {
        update(
            status = AdsSdkStatus.BLOCKED,
            message = message
        )
    }

    override fun markFailed(message: String) {
        update(
            status = AdsSdkStatus.FAILED,
            message = message
        )
    }

    private fun update(
        status: AdsSdkStatus,
        message: String?
    ) {
        mutableState.value = AdsSdkState(
            status = status,
            message = message,
            updatedAtMillis = System.currentTimeMillis()
        )
    }
}