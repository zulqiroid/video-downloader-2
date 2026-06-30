// com/core/ads/domain/init/AdsInitializationState.kt
package com.core.ads.domain.init

data class AdsInitializationState(
    val phase: AdsInitPhase = AdsInitPhase.IDLE,
    val isComplete: Boolean = false,
    val error: String? = null
)

enum class AdsInitPhase {
    IDLE,
    REQUESTING_CONSENT,
    INITIALIZING_SDK,
    PRELOADING_AD,
    COMPLETE
}