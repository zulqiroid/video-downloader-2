package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.firstOpen.FirstOpenUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.events.SplashEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.events.SplashNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.splash.states.SplashStates
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firstOpenUseCases: FirstOpenUseCases,
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository,
    ) : ViewModel() {

    private val _state = MutableStateFlow(SplashStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<SplashNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            firstOpenUseCases.isOnBoardingCompletedUseCase().collectLatest { flag ->
                _state.update {
                    it.copy(
                        isOnBoardingCompleted = flag
                    )
                }
            }
        }
        viewModelScope.launch {
            val isPremium = remoteConfigRepository.current().appConfig.premium.enabled

            _state.update {
                it.copy(
                    isPremiumEnable = isPremium
                )
            }
        }
    }

    fun onEvent(event: SplashEvents) {
        when (event) {
            SplashEvents.GetStartedClicked -> onGetStartedClicked()
        }
    }

    private fun onGetStartedClicked() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            _navEvents.emit(SplashNavEvents.NavigateToMain)
        }
    }
}