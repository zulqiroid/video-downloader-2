package com.core.ads.data.init

import com.core.ads.domain.AdsInitializer
import com.core.ads.domain.appopen.AppOpenAdController
import com.core.ads.domain.consent.ConsentInitializer
import com.core.ads.domain.init.AdsInitializationManager
import com.core.ads.domain.init.AdsInitializationState
import com.core.ads.domain.init.AdsInitPhase
import com.core.ads.domain.init.AdsSdkStateProvider
import com.core.ads.domain.runtime.AdsRuntimeMonitor
import com.core.ads.utils.AdsLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

class DefaultAdsInitializationManager(
    private val consentInitializer: ConsentInitializer,
    private val adsInitializer: AdsInitializer,
    private val appOpenAdController: AppOpenAdController,
    private val adsSdkStateProvider: AdsSdkStateProvider,
    private val adsRuntimeMonitor: AdsRuntimeMonitor
) : AdsInitializationManager {

    private val _state = MutableStateFlow(AdsInitializationState())
    override val state: StateFlow<AdsInitializationState> = _state.asStateFlow()

    private val started = AtomicBoolean(false)

    override fun start() {
        if (!started.compareAndSet(false, true)) {
            AdsLogger.d("AdsInitializationManager already started, skipping")
            return
        }

        adsRuntimeMonitor.start()

        AdsLogger.i("Pipeline: requesting consent")
        setPhase(AdsInitPhase.REQUESTING_CONSENT)

        consentInitializer.initialize {
            AdsLogger.i("Pipeline: initializing SDK")
            setPhase(AdsInitPhase.INITIALIZING_SDK)

            adsInitializer.initialize {
                val sdkState = adsSdkStateProvider.current

                if (!sdkState.isInitialized) {
                    AdsLogger.w(
                        "Pipeline complete without SDK initialization. " +
                                "status=${sdkState.status}, message=${sdkState.message}"
                    )

                    setPhase(AdsInitPhase.COMPLETE)
                    _state.update {
                        it.copy(
                            isComplete = true,
                            error = sdkState.message
                        )
                    }
                    return@initialize
                }

                AdsLogger.i("Pipeline: preloading startup AppOpen ad")
                setPhase(AdsInitPhase.PRELOADING_AD)

                appOpenAdController.preloadForSplash()

                setPhase(AdsInitPhase.COMPLETE)
                _state.update { it.copy(isComplete = true) }

                AdsLogger.i("Pipeline: complete")
            }
        }
    }

    private fun setPhase(phase: AdsInitPhase) {
        _state.update { it.copy(phase = phase) }
    }
}