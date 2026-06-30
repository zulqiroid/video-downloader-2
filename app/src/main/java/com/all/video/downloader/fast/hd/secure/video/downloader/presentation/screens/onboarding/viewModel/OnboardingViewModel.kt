package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.firstOpen.FirstOpenUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.events.OnboardingEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.events.OnboardingNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.onboarding.states.OnboardingStates
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val firstOpenUseCases: FirstOpenUseCases,
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository,
    ) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingStates())
    val state: StateFlow<OnboardingStates> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<OnboardingNavEvents>()
    val navEvents: SharedFlow<OnboardingNavEvents> = _navEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            val isPremium = remoteConfigRepository.current().appConfig.premium.enabled

            _state.update {
                it.copy(
                    isPremiumEnable = isPremium
                )
            }
        }
    }

    fun onEvent(event: OnboardingEvents) {
        when (event) {
            is OnboardingEvents.PageChanged -> {
                _state.value = _state.value.copy(
                    currentPage = event.page
                )
            }

            OnboardingEvents.NextClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(OnboardingNavEvents.NavigateNext)
                }
            }

            OnboardingEvents.FinishOnBoarding -> {

                viewModelScope.launch {

                    firstOpenUseCases.setOnBoardingCompletedFlagUseCase()

                    _navEvents.emit(OnboardingNavEvents.NavigateToMain)
                }
            }
        }
    }

}