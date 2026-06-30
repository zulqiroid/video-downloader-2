package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStartRequest
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.Reel
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.DownloadUseCases as FileDownloadUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.reels.ReelsUseCases
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events.ReelsEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events.ReelsNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events.ReelsUiEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.states.ReelUi
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.states.ReelsStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val reelsUseCases: ReelsUseCases,
    private val fileDownloadUseCases: FileDownloadUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ReelsStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<ReelsNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private val _uiEffects = MutableSharedFlow<ReelsUiEffect>(
        extraBufferCapacity = 1
    )
    val uiEffects = _uiEffects.asSharedFlow()

    private var loadReelsJob: Job? = null

    init {
        loadReels()
    }

    fun onEvent(event: ReelsEvents) {
        when (event) {
            ReelsEvents.LoadReels -> {
                loadReels()
            }

            is ReelsEvents.OnPageChanged -> {
                onPageChanged(event.index)
            }

            is ReelsEvents.OnLikeClicked -> {
                onLikeClicked(event.reelId)
            }

            is ReelsEvents.OnShareClicked -> {
                onShareClicked(event.reelId)
            }

            is ReelsEvents.OnDownloadClicked -> {
                onDownloadClicked(event.reelId)
            }

            ReelsEvents.OnSettingCLicked -> {
                viewModelScope.launch {
                    _navEvents.emit(ReelsNavEvents.NavigateToSettingScreen)
                }
            }
        }
    }

    private fun onPageChanged(index: Int) {
        _state.update { currentState ->
            currentState.copy(
                currentIndex = index.coerceIn(
                    minimumValue = 0,
                    maximumValue = (currentState.reels.size - 1).coerceAtLeast(0)
                )
            )
        }
    }

    private fun loadReels() {
        if (loadReelsJob?.isActive == true) return

        loadReelsJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                reelsUseCases.fetchReelCategories()
            }.onSuccess { categories ->
                val likedIds = _state.value.likedReelIds

                val reels = categories
                    .flatMap { category -> category.reels }
                    .distinctBy { reel -> reel.id }
                    .map { reel -> reel.toUi(likedIds) }

                _state.update { currentState ->
                    currentState.copy(
                        reels = reels,
                        isLoading = false,
                        errorMessage = null,
                        currentIndex = currentState.currentIndex.coerceIn(
                            minimumValue = 0,
                            maximumValue = (reels.size - 1).coerceAtLeast(0)
                        )
                    )
                }
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Failed to load reels")

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                            ?: "Unable to load reels. Please try again."
                    )
                }
            }
        }
    }

    private fun onLikeClicked(reelId: String) {
        _state.update { currentState ->
            val wasLiked = currentState.likedReelIds.contains(reelId)

            val updatedLikedIds = if (wasLiked) {
                currentState.likedReelIds - reelId
            } else {
                currentState.likedReelIds + reelId
            }

            val updatedReels = currentState.reels.map { reel ->
                if (reel.id != reelId) {
                    reel
                } else {
                    reel.copy(
                        isLiked = !wasLiked,
                        likeCount = if (wasLiked) {
                            (reel.likeCount - 1).coerceAtLeast(0)
                        } else {
                            reel.likeCount + 1
                        }
                    )
                }
            }

            currentState.copy(
                likedReelIds = updatedLikedIds,
                reels = updatedReels
            )
        }
    }

    private fun onShareClicked(reelId: String) {
        val reel = findReelById(reelId)

        if (reel == null) {
            emitMessage("Unable to share this reel.")
            return
        }

        viewModelScope.launch {
            _uiEffects.emit(
                ReelsUiEffect.ShareReel(
                    title = "Share reel",
                    text = buildShareText(reel)
                )
            )
        }
    }

    private fun onDownloadClicked(reelId: String) {
        val reel = findReelById(reelId)

        if (reel == null) {
            emitMessage("Unable to download this reel.")
            return
        }

        if (_state.value.downloadingReelIds.contains(reelId)) {
            return
        }

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    downloadingReelIds = currentState.downloadingReelIds + reelId
                )
            }

            runCatching {
                fileDownloadUseCases.startDownload(
                    DownloadStartRequest(
                        sourceUrl = reel.videoUrl,
                        downloadUrl = reel.videoUrl,
                        title = buildReelDownloadTitle(reel),
                        platform = REELS_PLATFORM,
                        quality = REELS_QUALITY,
                        thumbnailUrl = null
                    )
                )
            }.onSuccess { downloadId ->
                Timber.tag(TAG).d(
                    """
                    Reel download enqueued
                    downloadId=$downloadId
                    reelId=${reel.id}
                    url=${reel.videoUrl.take(LOG_PREVIEW_LIMIT)}
                    """.trimIndent()
                )

                _uiEffects.emit(
                    ReelsUiEffect.ShowMessage(
                        message = "Reel download started."
                    )
                )
            }.onFailure { throwable ->
                Timber.tag(TAG).e(
                    throwable,
                    "Failed to start reel download. reelId=${reel.id}"
                )

                _uiEffects.emit(
                    ReelsUiEffect.ShowMessage(
                        message = "Unable to start download. Please try again."
                    )
                )
            }

            _state.update { currentState ->
                currentState.copy(
                    downloadingReelIds = currentState.downloadingReelIds - reelId
                )
            }
        }
    }

    fun setCurrentIndex(selectedReel: Reel?) {
        if (selectedReel == null) return

        val index = _state.value.reels.indexOfFirst { reel ->
            reel.id == selectedReel.id
        }

        if (index != -1) {
            _state.update { currentState ->
                currentState.copy(currentIndex = index)
            }
        }
    }

    private fun findReelById(reelId: String): ReelUi? {
        return _state.value.reels.firstOrNull { reel ->
            reel.id == reelId
        }
    }

    private fun Reel.toUi(
        likedIds: Set<String>
    ): ReelUi {
        val isLiked = likedIds.contains(id)

        return ReelUi(
            id = id,
            videoUrl = videoUrl,
            username = categoryName.ifBlank { DEFAULT_USERNAME },
            caption = createdAt?.let { "Added $it" }.orEmpty(),
            isLiked = isLiked,
            likeCount = if (isLiked) DEFAULT_LIKE_COUNT + 1 else DEFAULT_LIKE_COUNT
        )
    }

    private fun buildShareText(
        reel: ReelUi
    ): String {
        return buildString {
            appendLine("Check out this reel:")
            append(reel.videoUrl)
        }
    }

    private fun buildReelDownloadTitle(
        reel: ReelUi
    ): String {
        return "Reel_${reel.id}"
    }

    private fun emitMessage(message: String) {
        viewModelScope.launch {
            _uiEffects.emit(
                ReelsUiEffect.ShowMessage(message)
            )
        }
    }

    companion object {
        private const val TAG = "ReelsViewModel"
        private const val DEFAULT_USERNAME = "Reels"
        private const val DEFAULT_LIKE_COUNT = 105
        private const val REELS_PLATFORM = "Reels"
        private const val REELS_QUALITY = "Original"
        private const val LOG_PREVIEW_LIMIT = 120
    }
}