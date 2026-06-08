package com.video.downloader.presentation.screens.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.domain.connectivity.NetworkMonitor
import com.video.downloader.domain.models.Platforms
import com.video.downloader.domain.usecases.download.DownloadUseCases
import com.video.downloader.presentation.screens.home.events.HomeEvents
import com.video.downloader.presentation.screens.home.events.HomeNavEvents
import com.video.downloader.presentation.screens.home.states.HomeFeatures
import com.video.downloader.presentation.screens.home.states.HomeStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val downloadUseCases: DownloadUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(HomeStates())
    val state: StateFlow<HomeStates> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<HomeNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(event: HomeEvents) {
        when (event) {
            is HomeEvents.VideoUrlChanged -> onVideoUrlChanged(event.value)

            is HomeEvents.PasteClicked -> onPasteClicked(event.clipboardText)

            HomeEvents.DownloadClicked -> onDownloadClicked()

            HomeEvents.NoInternetRetryClicked -> onNoInternetRetryClicked()

            HomeEvents.NoInternetCancelClicked -> onNoInternetCancelClicked()

            HomeEvents.WatchTrendingReelsClicked -> onWatchTrendingReelsClicked()

            is HomeEvents.PlatformClicked -> onPlatformClicked(event.platform)

            is HomeEvents.FeatureClicked -> onFeatureClicked(event.feature)

            HomeEvents.HowToDownloadVideosClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(HomeNavEvents.NavigateToDownloadGuide)
                }
            }

            HomeEvents.SettingClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(HomeNavEvents.NavigateToMore)
                }
            }

            HomeEvents.VideoFetchRetryClicked -> onVideoFetchRetryClicked()

            HomeEvents.VideoFetchCancelClicked -> onVideoFetchCancelClicked()
        }
    }

    private fun onVideoUrlChanged(value: String) {
        _state.update { currentState ->
            currentState.copy(
                videoUrl = value,
                error = null
            )
        }
    }

    private fun onPasteClicked(clipboardText: String) {
        _state.update { currentState ->
            currentState.copy(
                videoUrl = clipboardText.trim(),
                error = null
            )
        }
    }

    private fun onDownloadClicked() {
        val currentUrl = _state.value.videoUrl.trim()

        if (currentUrl.isBlank()) {
            _state.update { currentState ->
                currentState.copy(
                    error = "Please paste a video link first",
                    showNoInternetDialog = false
                )
            }
            return
        }

        if (_state.value.isDownloadLoading || _state.value.isCheckingInternet) {
            return
        }

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isCheckingInternet = true,
                    error = null,
                    showNoInternetDialog = false
                )
            }

            val hasInternet = networkMonitor.isInternetAvailable()

            if (!hasInternet) {
                _state.update { currentState ->
                    currentState.copy(
                        isCheckingInternet = false,
                        isDownloadLoading = false,
                        showNoInternetDialog = true,
                        error = null
                    )
                }
                return@launch
            }

            _state.update { currentState ->
                currentState.copy(
                    isCheckingInternet = false
                )
            }

            startDownloadFlow(currentUrl)
        }
    }

    private suspend fun startDownloadFlow(url: String) {
        Timber.tag("HomeDownload").d(
            """
        startDownloadFlow() started
        url=${url.take(120)}
        """.trimIndent()
        )

        _state.update { currentState ->
            currentState.copy(
                isFetchingVideo = true,
                isDownloadLoading = false,
                error = null,
                showNoInternetDialog = false,
                showVideoFetchFailedDialog = false
            )
        }

        runCatching {
            Timber.tag("HomeDownload").d("Calling fetchDownloadInfo()")

            downloadUseCases.fetchDownloadInfo(url)
        }.onSuccess { fetchResult ->
            val downloadable = fetchResult.bestDownloadable

            Timber.tag("HomeDownload").d(
                """
            fetchDownloadInfo() success
            title=${fetchResult.title}
            platform=${fetchResult.platform}
            downloadables=${fetchResult.downloadables.size}
            bestQuality=${downloadable?.quality.orEmpty()}
            bestUrl=${downloadable?.url.orEmpty().take(120)}
            """.trimIndent()
            )

            if (downloadable == null) {
                Timber.tag("HomeDownload").e("Best downloadable is null")

                _state.update { currentState ->
                    currentState.copy(
                        isFetchingVideo = false,
                        showVideoFetchFailedDialog = true
                    )
                }
                return
            }

            /*
             * Phase 2:
             * WorkManager download will start here.
             */

            _state.update { currentState ->
                currentState.copy(
                    isFetchingVideo = false,
                    isDownloadLoading = false,
                    error = null
                )
            }
        }.onFailure { throwable ->
            Timber.tag("HomeDownload").e(
                throwable,
                """
            fetchDownloadInfo() failed
            message=${throwable.message}
            """.trimIndent()
            )

            _state.update { currentState ->
                currentState.copy(
                    isFetchingVideo = false,
                    isDownloadLoading = false,
                    showVideoFetchFailedDialog = true,
                    error = null
                )
            }
        }
    }

    private fun onNoInternetRetryClicked() {
        _state.update { currentState ->
            currentState.copy(
                showNoInternetDialog = false,
                error = null
            )
        }

        onDownloadClicked()
    }

    private fun onNoInternetCancelClicked() {
        _state.update { currentState ->
            currentState.copy(
                showNoInternetDialog = false,
                isCheckingInternet = false,
                isDownloadLoading = false
            )
        }
    }

    private fun onWatchTrendingReelsClicked() {
        viewModelScope.launch {
            _navEvents.emit(HomeNavEvents.NavigateToWatchReels)
        }
    }

    private fun onPlatformClicked(platform: Platforms) {
        viewModelScope.launch {
            _navEvents.emit(
                HomeNavEvents.NavigateToPlatformDetail(
                    platform = platform
                )
            )
        }
    }

    private fun onFeatureClicked(feature: HomeFeatures) {
        viewModelScope.launch {
            when (feature) {
                HomeFeatures.VideoToAudio -> {
                    _navEvents.emit(HomeNavEvents.NavigateToVideoToMp3)
                }

                HomeFeatures.VideoSplitter -> {
                    _navEvents.emit(HomeNavEvents.NavigateToVideoSplitter)
                }

                HomeFeatures.ScreenCasting -> {
                    _navEvents.emit(HomeNavEvents.NavigateToScreenCasting)
                }
            }
        }
    }

    private fun onVideoFetchRetryClicked() {
        _state.update { currentState ->
            currentState.copy(
                showVideoFetchFailedDialog = false,
                error = null
            )
        }

        onDownloadClicked()
    }

    private fun onVideoFetchCancelClicked() {
        _state.update { currentState ->
            currentState.copy(
                showVideoFetchFailedDialog = false,
                isFetchingVideo = false,
                isDownloadLoading = false
            )
        }
    }
}