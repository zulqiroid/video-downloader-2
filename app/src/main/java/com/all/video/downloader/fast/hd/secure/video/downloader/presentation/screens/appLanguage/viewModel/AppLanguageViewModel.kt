package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.localization.AppLocale
import com.all.video.downloader.fast.hd.secure.video.downloader.localization.AppLocaleController
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.events.AppLanguageEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.states.AppLanguageStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLanguageViewModel @Inject constructor(
    private val localDataStoreRepository: LocalDataStoreRepository,
    private val appLocaleController: AppLocaleController
) : ViewModel() {

    private val _state = MutableStateFlow(AppLanguageStates())
    val state: StateFlow<AppLanguageStates> = _state.asStateFlow()

    private val _navEvents = Channel<AppLanguageNavEvents>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        observeSelectedLanguage()
    }

    fun onEvent(event: AppLanguageEvents) {
        when (event) {
            AppLanguageEvents.BackClicked -> onBackClicked()

            is AppLanguageEvents.SearchQueryChanged -> onSearchQueryChanged(
                value = event.value
            )

            is AppLanguageEvents.LanguageSelected -> onLanguageSelected(
                languageCode = event.languageCode
            )

            AppLanguageEvents.ApplyLanguageClicked -> onApplyLanguageClicked()
        }
    }

    private fun observeSelectedLanguage() {
        viewModelScope.launch {
            localDataStoreRepository.getSelectedLanguageCode().collect { code ->
                if (code.isNullOrBlank()) return@collect

                _state.update { currentState ->
                    currentState.copy(
                        selectedLanguageCode = AppLocale
                            .fromLanguageCode(code)
                            .languageCode
                    )
                }
            }
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _navEvents.send(AppLanguageNavEvents.NavigateBack)
        }
    }

    private fun onSearchQueryChanged(value: String) {
        _state.update { currentState ->
            currentState.copy(searchQuery = value)
        }
    }

    private fun onLanguageSelected(languageCode: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedLanguageCode = AppLocale
                    .fromLanguageCode(languageCode)
                    .languageCode
            )
        }
    }

    private fun onApplyLanguageClicked() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }

            appLocaleController.setAppLocale(
                appLocale = AppLocale.fromLanguageCode(
                    _state.value.selectedLanguageCode
                )
            )

            _state.update { currentState ->
                currentState.copy(isLoading = false)
            }

            _navEvents.send(AppLanguageNavEvents.NavigateToMain)
        }
    }
}