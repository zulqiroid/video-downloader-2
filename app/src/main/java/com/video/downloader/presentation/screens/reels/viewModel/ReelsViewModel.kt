package com.video.downloader.presentation.screens.reels.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.video.downloader.domain.models.Reel
import com.video.downloader.domain.models.ReelCategory
import com.video.downloader.presentation.screens.reels.events.ReelsEvents
import com.video.downloader.presentation.screens.reels.events.ReelsNavEvents
import com.video.downloader.presentation.screens.reels.states.ReelUi
import com.video.downloader.presentation.screens.reels.states.ReelsStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReelsViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(ReelsStates())
    val state = _state.asStateFlow()

    private val _navEvents  = MutableSharedFlow<ReelsNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

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
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }

//            val categories = getReelsUseCase()
            val categories = emptyList<ReelCategory>()

            val likedIds = _state.value.likedReelIds

            val reels = categories
                .flatMap { category -> category.reels }
                .distinctBy { reel -> reel.id }
                .map { reel ->
                    val isLiked = likedIds.contains(reel.id)

                    ReelUi(
                        id = reel.id,
                        videoUrl = reel.videoUrl,
                        username = "User",
                        caption = "",
                        isLiked = isLiked,
                        likeCount = if (isLiked) DEFAULT_LIKE_COUNT + 1 else DEFAULT_LIKE_COUNT
                    )
                }

            _state.update { currentState ->
                currentState.copy(
                    reels = reels,
                    isLoading = false,
                    currentIndex = currentState.currentIndex.coerceIn(
                        minimumValue = 0,
                        maximumValue = (reels.size - 1).coerceAtLeast(0)
                    )
                )
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

    companion object {
        private const val DEFAULT_LIKE_COUNT = 105
    }

}