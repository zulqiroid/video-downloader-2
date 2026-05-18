package com.video.downloader.presentation.screens.more.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.presentation.screens.more.events.MoreEvents
import com.video.downloader.presentation.screens.more.events.MoreNavEvents
import com.video.downloader.presentation.screens.more.states.MoreStates
import com.video.downloader.presentation.screens.more.states.SettingsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MoreViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MoreStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MoreNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(event: MoreEvents) {
        when (event) {
            MoreEvents.BackClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(MoreNavEvents.NavigateBack)
                }
            }
            is MoreEvents.OnNotificationCheckedChange -> {
                _state.value = _state.value.copy(
                    isNotificationEnabled = event.isChecked
                )
            }

            is MoreEvents.OnSettingItemClicked -> {
                onSettingItemClicked(event.item)
            }
        }

    }

    private fun onSettingItemClicked(item: SettingsItem) {
        when (item) {
            SettingsItem.DownloadGuide -> {

            }

            SettingsItem.DownloadLocation -> {}
            SettingsItem.Language -> {

            }

            SettingsItem.Notification -> {}
            SettingsItem.RateApp -> {

            }

            SettingsItem.ShareApp -> {}
        }
    }

}