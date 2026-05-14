package com.video.downloader.presentation.screens.splash.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.presentation.screens.splash.events.SplashEvents
import com.video.downloader.presentation.screens.splash.events.SplashNavEvents
import com.video.downloader.presentation.screens.splash.states.SplashStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SplashStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<SplashNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(event: SplashEvents) {
        when (event) {
            SplashEvents.GetStartedClicked -> onGetStartedClicked()
        }
    }

    private fun onGetStartedClicked() {
        viewModelScope.launch {
            _navEvents.emit(SplashNavEvents.NavigateToMain)
        }
    }
}