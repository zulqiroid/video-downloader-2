package com.video.downloader.presentation.screens.downloadGuide.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.presentation.screens.downloadGuide.events.DownloadGuideEvents
import com.video.downloader.presentation.screens.downloadGuide.events.DownloadGuideNavEvents
import com.video.downloader.presentation.screens.downloadGuide.states.DownloadGuideStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadGuideViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(DownloadGuideStates())
    val state: StateFlow<DownloadGuideStates> = _state.asStateFlow()

    private val _navEvents = Channel<DownloadGuideNavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    fun onEvent(event: DownloadGuideEvents) {
        when (event) {
            DownloadGuideEvents.BackClicked -> onBackClicked()
            DownloadGuideEvents.TryNowClicked -> onTryNowClicked()
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _navEvents.send(DownloadGuideNavEvents.NavigateBack)
        }
    }

    private fun onTryNowClicked() {
        viewModelScope.launch {
            _navEvents.send(DownloadGuideNavEvents.NavigateToHome)
        }
    }
}