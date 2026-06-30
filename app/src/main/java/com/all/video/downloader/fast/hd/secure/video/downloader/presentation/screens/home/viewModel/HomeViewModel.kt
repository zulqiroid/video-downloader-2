package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.connectivity.NetworkMonitor
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.Platforms
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStartRequest
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.DownloadUseCases as FileDownloadUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.download.DownloadUseCases as FetchDownloadUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.events.HomeEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.events.HomeNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.states.HomeFeatures
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.home.states.HomeStates
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
    private val fetchDownloadUseCases: FetchDownloadUseCases,
    private val fileDownloadUseCases: FileDownloadUseCases,
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

        if (_state.value.isDownloadActionLoading) {
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
        Timber.tag(TAG).d(
            """
        startDownloadFlow() started
        sourceUrl=${url.take(LOG_PREVIEW_LIMIT)}
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

        try {
            Timber.tag(TAG).d("Calling fetchDownloadInfo()")

            val fetchResult = fetchDownloadUseCases.fetchDownloadInfo(url)
            val downloadable = fetchResult.bestDownloadable

            Timber.tag(TAG).d(
                """
            fetchDownloadInfo() success
            title=${fetchResult.title}
            platform=${fetchResult.platform}
            downloadables=${fetchResult.downloadables.size}
            bestQuality=${downloadable?.quality.orEmpty()}
            bestUrl=${downloadable?.url.orEmpty().take(LOG_PREVIEW_LIMIT)}
            """.trimIndent()
            )

            if (downloadable == null) {
                Timber.tag(TAG).e("Best downloadable is null")

                _state.update { currentState ->
                    currentState.copy(
                        isFetchingVideo = false,
                        isDownloadLoading = false,
                        showVideoFetchFailedDialog = true
                    )
                }
                return
            }

            _state.update { currentState ->
                currentState.copy(
                    isFetchingVideo = false,
                    isDownloadLoading = true
                )
            }

            val downloadId = fileDownloadUseCases.startDownload(
                DownloadStartRequest(
                    sourceUrl = url,
                    downloadUrl = downloadable.url,
                    title = fetchResult.title,
                    platform = fetchResult.platform,
                    quality = downloadable.quality,
                    thumbnailUrl = fetchResult.thumbnailUrl
                )
            )

            Timber.tag(TAG).d(
                """
            WorkManager download enqueued
            downloadId=$downloadId
            title=${fetchResult.title}
            quality=${downloadable.quality}
            """.trimIndent()
            )

            _state.update { currentState ->
                currentState.copy(
                    isFetchingVideo = false,
                    isDownloadLoading = false,
                    videoUrl = "",
                    error = null
                )
            }
        } catch (throwable: Throwable) {
            Timber.tag(TAG).e(
                throwable,
                """
            startDownloadFlow() failed
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
    private companion object {
        private const val TAG = "HomeDownload"
        private const val LOG_PREVIEW_LIMIT = 120
    }
}