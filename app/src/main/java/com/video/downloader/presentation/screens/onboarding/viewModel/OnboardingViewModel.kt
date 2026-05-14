package com.video.downloader.presentation.screens.onboarding.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.presentation.screens.onboarding.events.OnboardingEvents
import com.video.downloader.presentation.screens.onboarding.events.OnboardingNavEvents
import com.video.downloader.presentation.screens.onboarding.states.OnboardingStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(OnboardingStates())
    val state: StateFlow<OnboardingStates> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<OnboardingNavEvents>()
    val navEvents: SharedFlow<OnboardingNavEvents> = _navEvents.asSharedFlow()

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
        }
    }

}