package com.video.downloader.presentation.screens.main.viewModel

import androidx.lifecycle.ViewModel
import com.video.downloader.presentation.screens.main.events.MainEvents
import com.video.downloader.presentation.screens.main.events.MainNavEvents
import com.video.downloader.presentation.screens.main.states.MainStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel()  {

    private val _state = MutableStateFlow(MainStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MainNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(event: MainEvents){
        when(event){
            is MainEvents.OnTabSelected -> {
                _state.value = _state.value.copy(
                    selectedTab = event.tab
                )
            }
        }

    }

}