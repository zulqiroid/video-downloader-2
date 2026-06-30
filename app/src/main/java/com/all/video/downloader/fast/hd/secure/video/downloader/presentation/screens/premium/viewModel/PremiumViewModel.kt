package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumPlanType
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumBillingRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumBillingResult
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumEntitlementRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events.PremiumEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events.PremiumNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events.PremiumUiEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.states.PremiumPlanUi
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.states.PremiumStates
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.PremiumPlanKey
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository,
    private val premiumBillingRepository: PremiumBillingRepository,
    private val premiumEntitlementRepository: PremiumEntitlementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PremiumStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<PremiumNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private val _uiEffects = MutableSharedFlow<PremiumUiEffect>(
        extraBufferCapacity = 1
    )
    val uiEffects = _uiEffects.asSharedFlow()

    init {
        observePremium()
        loadProducts()
    }

    fun onEvent(event: PremiumEvents) {
        when (event) {
            is PremiumEvents.PlanSelected -> {
                _state.update { currentState ->
                    currentState.copy(selectedPlan = event.type)
                }
            }

            is PremiumEvents.ContinueClicked -> {
                purchaseSelectedPlan(event.activity)
            }

            PremiumEvents.RestoreClicked -> {
                restorePurchases()
            }

            PremiumEvents.CloseClicked -> {
                emitNav(PremiumNavEvents.NavigateBack)
            }

            PremiumEvents.PrivacyPolicyClicked -> {
                val url = remoteConfigRepository
                    .current()
                    .appConfig
                    .global
                    .privacyPolicyUrl
                    .orEmpty()

                emitEffect(
                    PremiumUiEffect.OpenUrl(
                        url = url,
                        fallbackMessage = "Privacy Policy is not available."
                    )
                )
            }

            PremiumEvents.TermsClicked -> {
                val url = remoteConfigRepository
                    .current()
                    .appConfig
                    .global
                    .termsUrl
                    .orEmpty()

                emitEffect(
                    PremiumUiEffect.OpenUrl(
                        url = url,
                        fallbackMessage = "Terms are not available."
                    )
                )
            }

            PremiumEvents.CancelSubscriptionClicked -> {
                emitEffect(
                    PremiumUiEffect.OpenUrl(
                        url = PLAY_SUBSCRIPTIONS_URL,
                        fallbackMessage = "Unable to open subscription settings."
                    )
                )
            }
        }
    }

    private fun observePremium() {
        viewModelScope.launch {
            combine(
                premiumEntitlementRepository.observeEntitlement(),
                remoteConfigRepository.configState
            ) { entitlement, snapshot ->
                entitlement to snapshot.appConfig.premium
            }.collect { (entitlement, premiumConfig) ->
                _state.update { currentState ->
                    currentState.copy(
                        isPremiumUser = entitlement.isPremiumUser || entitlement.hasLifetimePurchase,
                        headline = premiumConfig.headline,
                        subtitle = premiumConfig.subtitle,
                        selectedPlan = if (currentState.plans.isEmpty()) {
                            premiumConfig.defaultSelectedPlan.toPremiumPlanType()
                        } else {
                            currentState.selectedPlan
                        }
                    )
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val premiumConfig = remoteConfigRepository
                .current()
                .appConfig
                .premium

            if (!premiumConfig.enabled || !premiumConfig.showPremiumScreen) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Premium is not available right now."
                    )
                }
                return@launch
            }

            runCatching {
                premiumBillingRepository.loadProducts(premiumConfig)
            }.onSuccess { products ->
                val plans = products.map { product ->
                    PremiumPlanUi(
                        type = product.type,
                        productId = product.productId,
                        title = product.title,
                        price = product.price,
                        periodLabel = product.periodLabel
                    )
                }

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        plans = plans,
                        selectedPlan = plans.firstOrNull {
                            it.type == premiumConfig.defaultSelectedPlan.toPremiumPlanType()
                        }?.type ?: plans.firstOrNull()?.type ?: PremiumPlanType.YEARLY,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load premium plans."
                    )
                }
            }
        }
    }

    private fun purchaseSelectedPlan(activity: android.app.Activity?) {
        if (activity == null) {
            emitEffect(PremiumUiEffect.ShowMessage("Purchase is not available right now."))
            return
        }

        val selectedProductId = _state.value.plans
            .firstOrNull { it.type == _state.value.selectedPlan }
            ?.productId

        if (selectedProductId.isNullOrBlank()) {
            emitEffect(PremiumUiEffect.ShowMessage("Please select a valid plan."))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isPurchasing = true) }

            when (
                val result = premiumBillingRepository.launchPurchase(
                    activity = activity,
                    productId = selectedProductId
                )
            ) {
                PremiumBillingResult.FlowStarted -> {
                    _state.update { it.copy(isPurchasing = false) }
                }

                PremiumBillingResult.Success -> {
                    _state.update { it.copy(isPurchasing = false) }
                    emitEffect(PremiumUiEffect.ShowMessage("Premium activated."))
                }

                is PremiumBillingResult.Failure -> {
                    _state.update { it.copy(isPurchasing = false) }
                    emitEffect(PremiumUiEffect.ShowMessage(result.message))
                }
            }
        }
    }

    private fun restorePurchases() {
        viewModelScope.launch {
            _state.update { it.copy(isPurchasing = true) }

            when (val result = premiumBillingRepository.restorePurchases()) {
                PremiumBillingResult.Success -> {
                    _state.update { it.copy(isPurchasing = false) }
                    emitEffect(PremiumUiEffect.ShowMessage("Purchase restored."))
                }

                PremiumBillingResult.FlowStarted -> {
                    _state.update { it.copy(isPurchasing = false) }
                }

                is PremiumBillingResult.Failure -> {
                    _state.update { it.copy(isPurchasing = false) }
                    emitEffect(PremiumUiEffect.ShowMessage(result.message))
                }
            }
        }
    }

    private fun PremiumPlanKey.toPremiumPlanType(): PremiumPlanType {
        return when (this) {
            PremiumPlanKey.WEEKLY -> PremiumPlanType.WEEKLY
            PremiumPlanKey.MONTHLY -> PremiumPlanType.MONTHLY
            PremiumPlanKey.YEARLY -> PremiumPlanType.YEARLY
            PremiumPlanKey.LIFETIME -> PremiumPlanType.LIFETIME
        }
    }

    private fun emitNav(navEvent: PremiumNavEvents) {
        viewModelScope.launch {
            _navEvents.emit(navEvent)
        }
    }

    private fun emitEffect(effect: PremiumUiEffect) {
        viewModelScope.launch {
            _uiEffects.emit(effect)
        }
    }

    private companion object {
        private const val PLAY_SUBSCRIPTIONS_URL =
            "https://play.google.com/store/account/subscriptions"
    }
}