package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.GetAudiosPageUseCase
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.GetVideosPageUseCase
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.ObserveAudioChangesUseCase
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.ObserveVideoChangesUseCase
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.events.PlayerEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.events.PlayerNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states.PlayerStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.player.states.PlayerTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getVideosPage: GetVideosPageUseCase,
    private val getAudiosPage: GetAudiosPageUseCase,
    private val observeVideoChanges: ObserveVideoChangesUseCase,
    private val observeAudioChanges: ObserveAudioChangesUseCase,
) : ViewModel() {

    private val videoPaginationMutex = Mutex()
    private val audioPaginationMutex = Mutex()

    private val _state = MutableStateFlow(PlayerStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<PlayerNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private var observeMediaJob: Job? = null

    fun onEvent(event: PlayerEvents) {
        when (event) {
            is PlayerEvents.OnMediaPermissionResult -> {
                _state.update { current ->
                    current.copy(
                        isMediaPermissionGranted = event.granted,
                        showMediaPermissionDialog = !event.granted,
                        shouldOpenMediaPermissionSettings = event.permanentlyDenied
                    )
                }
            }

            is PlayerEvents.SendToMedia -> {
                viewModelScope.launch {
                    _navEvents.emit(
                        PlayerNavEvents.SendToMedia(
                            mediaList = event.mediaList,
                            startIndex = event.startIndex
                        )
                    )
                }
            }

            is PlayerEvents.OnLoadNextMediaPage -> {
                when (event.tab) {
                    PlayerTab.VIDEO -> loadNextVideosPage()
                    PlayerTab.AUDIO -> loadNextAudiosPage()
                }
            }

            PlayerEvents.OnSettingClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(
                        PlayerNavEvents.NavigateToSettingScreen
                    )
                }
            }
        }
    }

    fun onMediaPermissionGranted() {
        if (observeMediaJob?.isActive == true) {
            return
        }

        observeMediaJob = viewModelScope.launch {
            launch {
                observeVideoChanges().collect {
                    refreshVisibleVideosWindow()
                }
            }

            launch {
                observeAudioChanges().collect {
                    refreshVisibleAudiosWindow()
                }
            }
        }
    }

    fun onMediaPermissionDenied() {
        observeMediaJob?.cancel()
        observeMediaJob = null

        _state.update { current ->
            current.copy(
                videos = emptyList(),
                audios = emptyList(),
                isLoading = false,

                isVideoInitialLoading = false,
                isAudioInitialLoading = false,

                isVideoPageLoading = false,
                isAudioPageLoading = false,

                hasMoreVideos = true,
                hasMoreAudios = true,

                videoErrorMessage = null,
                audioErrorMessage = null
            )
        }
    }

    fun onTabChange(tab: PlayerTab) {
        _state.update { current ->
            current.copy(selectedTab = tab)
        }

        when (tab) {
            PlayerTab.VIDEO -> {
                if (_state.value.videos.isEmpty() && !_state.value.isVideoInitialLoading) {
                    refreshVisibleVideosWindow()
                }
            }

            PlayerTab.AUDIO -> {
                if (_state.value.audios.isEmpty() && !_state.value.isAudioInitialLoading) {
                    refreshVisibleAudiosWindow()
                }
            }
        }
    }

    private fun refreshVisibleVideosWindow() {
        viewModelScope.launch {
            if (!videoPaginationMutex.tryLock()) {
                return@launch
            }

            try {
                val visibleLimit = maxOf(
                    PLAYER_PAGE_SIZE,
                    _state.value.videos.size
                )

                val requestLimit = visibleLimit + EXTRA_ITEM_FOR_HAS_MORE

                _state.update { current ->
                    current.copy(
                        isVideoInitialLoading = current.videos.isEmpty(),
                        isVideoPageLoading = false,
                        videoErrorMessage = null,
                        isLoading = current.videos.isEmpty() &&
                                current.selectedTab == PlayerTab.VIDEO
                    )
                }

                val rawVideos = getVideosPage(
                    offset = 0,
                    limit = requestLimit
                )

                val hasMore = rawVideos.size > visibleLimit

                val videos = rawVideos
                    .take(visibleLimit)
                    .distinctBy { item -> item.id }

                _state.update { current ->
                    current.copy(
                        videos = videos,
                        hasMoreVideos = hasMore,
                        isVideoInitialLoading = false,
                        isVideoPageLoading = false,
                        videoErrorMessage = null,
                        isLoading = false
                    )
                }
            } catch (throwable: Throwable) {
                _state.update { current ->
                    current.copy(
                        isVideoInitialLoading = false,
                        isVideoPageLoading = false,
                        isLoading = false,
                        videoErrorMessage = throwable.message
                    )
                }
            } finally {
                videoPaginationMutex.unlock()
            }
        }
    }

    private fun refreshVisibleAudiosWindow() {
        viewModelScope.launch {
            if (!audioPaginationMutex.tryLock()) {
                return@launch
            }

            try {
                val visibleLimit = maxOf(
                    PLAYER_PAGE_SIZE,
                    _state.value.audios.size
                )

                val requestLimit = visibleLimit + EXTRA_ITEM_FOR_HAS_MORE

                _state.update { current ->
                    current.copy(
                        isAudioInitialLoading = current.audios.isEmpty(),
                        isAudioPageLoading = false,
                        audioErrorMessage = null,
                        isLoading = current.audios.isEmpty() &&
                                current.selectedTab == PlayerTab.AUDIO
                    )
                }

                val rawAudios = getAudiosPage(
                    offset = 0,
                    limit = requestLimit
                )

                val hasMore = rawAudios.size > visibleLimit

                val audios = rawAudios
                    .take(visibleLimit)
                    .distinctBy { item -> item.id }

                _state.update { current ->
                    current.copy(
                        audios = audios,
                        hasMoreAudios = hasMore,
                        isAudioInitialLoading = false,
                        isAudioPageLoading = false,
                        audioErrorMessage = null,
                        isLoading = false
                    )
                }
            } catch (throwable: Throwable) {
                _state.update { current ->
                    current.copy(
                        isAudioInitialLoading = false,
                        isAudioPageLoading = false,
                        isLoading = false,
                        audioErrorMessage = throwable.message
                    )
                }
            } finally {
                audioPaginationMutex.unlock()
            }
        }
    }

    private fun loadNextVideosPage() {
        viewModelScope.launch {
            val snapshot = _state.value

            if (snapshot.isVideoInitialLoading ||
                snapshot.isVideoPageLoading ||
                !snapshot.hasMoreVideos
            ) {
                return@launch
            }

            if (!videoPaginationMutex.tryLock()) {
                return@launch
            }

            try {
                val offset = _state.value.videos.size

                _state.update { current ->
                    current.copy(
                        isVideoPageLoading = true,
                        videoErrorMessage = null
                    )
                }

                val rawVideos = getVideosPage(
                    offset = offset,
                    limit = PLAYER_PAGE_SIZE + EXTRA_ITEM_FOR_HAS_MORE
                )

                val hasMore = rawVideos.size > PLAYER_PAGE_SIZE

                val nextPage = rawVideos
                    .take(PLAYER_PAGE_SIZE)

                _state.update { current ->
                    val mergedVideos = (current.videos + nextPage)
                        .distinctBy { item -> item.id }

                    current.copy(
                        videos = mergedVideos,
                        hasMoreVideos = hasMore,
                        isVideoPageLoading = false,
                        isVideoInitialLoading = false,
                        videoErrorMessage = null
                    )
                }
            } catch (throwable: Throwable) {
                _state.update { current ->
                    current.copy(
                        isVideoPageLoading = false,
                        videoErrorMessage = throwable.message
                    )
                }
            } finally {
                videoPaginationMutex.unlock()
            }
        }
    }

    private fun loadNextAudiosPage() {
        viewModelScope.launch {
            val snapshot = _state.value

            if (snapshot.isAudioInitialLoading ||
                snapshot.isAudioPageLoading ||
                !snapshot.hasMoreAudios
            ) {
                return@launch
            }

            if (!audioPaginationMutex.tryLock()) {
                return@launch
            }

            try {
                val offset = _state.value.audios.size

                _state.update { current ->
                    current.copy(
                        isAudioPageLoading = true,
                        audioErrorMessage = null
                    )
                }

                val rawAudios = getAudiosPage(
                    offset = offset,
                    limit = PLAYER_PAGE_SIZE + EXTRA_ITEM_FOR_HAS_MORE
                )

                val hasMore = rawAudios.size > PLAYER_PAGE_SIZE

                val nextPage = rawAudios
                    .take(PLAYER_PAGE_SIZE)

                _state.update { current ->
                    val mergedAudios = (current.audios + nextPage)
                        .distinctBy { item -> item.id }

                    current.copy(
                        audios = mergedAudios,
                        hasMoreAudios = hasMore,
                        isAudioPageLoading = false,
                        isAudioInitialLoading = false,
                        audioErrorMessage = null
                    )
                }
            } catch (throwable: Throwable) {
                _state.update { current ->
                    current.copy(
                        isAudioPageLoading = false,
                        audioErrorMessage = throwable.message
                    )
                }
            } finally {
                audioPaginationMutex.unlock()
            }
        }
    }

    private companion object {
        private const val PLAYER_PAGE_SIZE = 20
        private const val EXTRA_ITEM_FOR_HAS_MORE = 1
    }
}