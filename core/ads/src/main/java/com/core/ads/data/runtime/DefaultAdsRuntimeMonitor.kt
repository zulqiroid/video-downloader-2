package com.core.ads.data.runtime

import com.core.ads.domain.AdsCoreConfig
import com.core.ads.domain.config.AdsConfigStore
import com.core.ads.domain.init.AdsSdkStateProvider
import com.core.ads.domain.init.AdsSdkStatus
import com.core.ads.domain.policy.AdsConsentPolicy
import com.core.ads.domain.policy.AdsUserPolicy
import com.core.ads.domain.runtime.AdsCleanupReason
import com.core.ads.domain.runtime.AdsRuntimeCleaner
import com.core.ads.domain.runtime.AdsRuntimeMonitor
import com.core.ads.utils.AdsLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicBoolean

class DefaultAdsRuntimeMonitor(
    private val configStore: AdsConfigStore,
    private val adsUserPolicy: AdsUserPolicy,
    private val adsConsentPolicy: AdsConsentPolicy,
    private val adsSdkStateProvider: AdsSdkStateProvider,
    private val adsRuntimeCleaner: AdsRuntimeCleaner
) : AdsRuntimeMonitor {

    private val started = AtomicBoolean(false)

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private var lastConfig: AdsCoreConfig? = null

    override fun start() {
        if (!started.compareAndSet(false, true)) {
            AdsLogger.d("AdsRuntimeMonitor already started.")
            return
        }

        AdsLogger.i("AdsRuntimeMonitor started.")

        observeConfigChanges()
        observeUserPolicyChanges()
        observeConsentChanges()
        observeSdkStateChanges()
    }

    override fun stop() {
        scope.cancel()
        started.set(false)
        AdsLogger.i("AdsRuntimeMonitor stopped.")
    }

    private fun observeConfigChanges() {
        configStore.state
            .onEach { newConfig ->
                val oldConfig = lastConfig

                if (oldConfig == null) {
                    lastConfig = newConfig
                    handleInitialConfig(newConfig)
                    return@onEach
                }

                lastConfig = newConfig

                if (!newConfig.adsEnabled) {
                    adsRuntimeCleaner.clearAll(
                        reason = AdsCleanupReason.ADS_DISABLED,
                        message = "Remote Config disabled ads."
                    )
                    return@onEach
                }

                if (!newConfig.canRequestAds) {
                    adsRuntimeCleaner.clearAll(
                        reason = AdsCleanupReason.REQUESTS_DISABLED,
                        message = "Remote Config disabled ad requests."
                    )
                    return@onEach
                }

                /**
                 * If config changed while ads are allowed, clear old caches.
                 *
                 * Reason:
                 * - ad unit IDs may have changed
                 * - placement enabled flags may have changed
                 * - cache age/cooldown policy may have changed
                 * - native size/banner behavior may have changed
                 */
                if (oldConfig != newConfig) {
                    adsRuntimeCleaner.clearAll(
                        reason = AdsCleanupReason.CONFIG_CHANGED,
                        message = "Ads config changed. Clearing old cached ads."
                    )
                }
            }
            .launchIn(scope)
    }

    private fun handleInitialConfig(config: AdsCoreConfig) {
        if (!config.adsEnabled) {
            adsRuntimeCleaner.clearAll(
                reason = AdsCleanupReason.ADS_DISABLED,
                message = "Initial config has ads disabled."
            )
            return
        }

        if (!config.canRequestAds) {
            adsRuntimeCleaner.clearAll(
                reason = AdsCleanupReason.REQUESTS_DISABLED,
                message = "Initial config has ad requests disabled."
            )
        }
    }

    private fun observeUserPolicyChanges() {
        adsUserPolicy.state
            .drop(1)
            .onEach { userState ->
                if (userState.canShowAds) return@onEach

                val reason = when {
                    userState.isPremiumUser -> AdsCleanupReason.USER_BECAME_PREMIUM
                    userState.hasRemoveAdsPurchase -> AdsCleanupReason.REMOVE_ADS_PURCHASED
                    else -> AdsCleanupReason.USER_POLICY_BLOCKED
                }

                adsRuntimeCleaner.clearAll(
                    reason = reason,
                    message = userState.reason
                        ?: "User policy changed and ads are no longer allowed."
                )
            }
            .launchIn(scope)
    }

    private fun observeConsentChanges() {
        adsConsentPolicy.state
            .drop(1)
            .onEach { consentState ->
                if (consentState.canRequestAds) return@onEach

                adsRuntimeCleaner.clearAll(
                    reason = AdsCleanupReason.CONSENT_REVOKED,
                    message = "Consent changed. User can no longer request ads."
                )
            }
            .launchIn(scope)
    }

    private fun observeSdkStateChanges() {
        adsSdkStateProvider.state
            .drop(1)
            .onEach { sdkState ->
                when (sdkState.status) {
                    AdsSdkStatus.BLOCKED -> {
                        adsRuntimeCleaner.clearAll(
                            reason = AdsCleanupReason.SDK_BLOCKED,
                            message = sdkState.message
                        )
                    }

                    AdsSdkStatus.FAILED -> {
                        adsRuntimeCleaner.clearAll(
                            reason = AdsCleanupReason.SDK_FAILED,
                            message = sdkState.message
                        )
                    }

                    AdsSdkStatus.NOT_INITIALIZED,
                    AdsSdkStatus.INITIALIZING,
                    AdsSdkStatus.INITIALIZED -> Unit
                }
            }
            .launchIn(scope)
    }
}