package com.core.ads.domain.init

data class AdsSdkState(
    val status: AdsSdkStatus = AdsSdkStatus.NOT_INITIALIZED,
    val message: String? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val isInitialized: Boolean
        get() = status == AdsSdkStatus.INITIALIZED

    val isTerminal: Boolean
        get() = status == AdsSdkStatus.INITIALIZED ||
                status == AdsSdkStatus.BLOCKED ||
                status == AdsSdkStatus.FAILED
}