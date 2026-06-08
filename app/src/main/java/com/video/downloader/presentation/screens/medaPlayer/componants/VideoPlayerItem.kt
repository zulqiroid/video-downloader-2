package com.video.downloader.presentation.screens.medaPlayer.componants

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.video.downloader.domain.models.MediaFile
import com.video.downloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerItem(
    media: MediaFile,
    viewModel: MediaPlayerViewModel,
    isLandscape: Boolean,
    onBack: () -> Unit,
    onRotateClick: () -> Unit,
    onThreeDotsClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var areControlsVisible by remember(media.id) {
        mutableStateOf(true)
    }

    var showInitialCenterControls by remember(media.id) {
        mutableStateOf(true)
    }

    var isVideoZoomed by remember(media.id) {
        mutableStateOf(false)
    }

    var lastInteractionTime by remember(media.id) {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var areControlsLocked by remember(media.id) {
        mutableStateOf(false)
    }

    fun markUserInteraction() {
        areControlsVisible = true
        lastInteractionTime = System.currentTimeMillis()
    }

    LaunchedEffect(media.id) {
        showInitialCenterControls = true
        delay(INITIAL_CENTER_CONTROLS_VISIBLE_MS)
        showInitialCenterControls = false
    }

    LaunchedEffect(
        areControlsVisible,
        lastInteractionTime,
        state.isPlaying,
        state.showBottomSheet,
        state.showPlaybackSpeedDialog,
        state.showFileInfoDialog,
        state.showRenameFileDialog,
        state.showDeleteFileDialog,
        state.showSetAsRingtoneDialog
    ) {
        val shouldKeepControlsVisible =
            areControlsLocked ||
                    !state.isPlaying ||
                    state.showBottomSheet ||
                    state.showPlaybackSpeedDialog ||
                    state.showFileInfoDialog ||
                    state.showRenameFileDialog ||
                    state.showDeleteFileDialog ||
                    state.showSetAsRingtoneDialog

        if (!areControlsVisible || shouldKeepControlsVisible) {
            return@LaunchedEffect
        }

        val interactionSnapshot = lastInteractionTime

        delay(CONTROLS_AUTO_HIDE_DELAY_MS)

        if (
            interactionSnapshot == lastInteractionTime &&
            state.isPlaying &&
            !state.showBottomSheet &&
            !state.showPlaybackSpeedDialog &&
            !state.showFileInfoDialog &&
            !state.showRenameFileDialog &&
            !state.showDeleteFileDialog &&
            !state.showSetAsRingtoneDialog
        ) {
            areControlsVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(
                state.isPlaying,
                areControlsVisible
            ) {
                detectTapGestures(
                    onTap = {
                        if (areControlsLocked) return@detectTapGestures

                        if (areControlsVisible && state.isPlaying) {
                            areControlsVisible = false
                        } else {
                            markUserInteraction()
                        }
                    },
                    onDoubleTap = { offset ->
                        if (areControlsLocked) return@detectTapGestures

                        markUserInteraction()

                        val tappedOnLeftSide = offset.x < size.width / 2f

                        if (tappedOnLeftSide) {
                            viewModel.onEvent(MediaPlayerEvent.OnRewindClicked)
                        } else {
                            viewModel.onEvent(MediaPlayerEvent.OnForwardClicked)
                        }
                    }
                )
            }
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = viewModel.player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            update = { playerView ->
                playerView.player = viewModel.player
                playerView.resizeMode = if (isVideoZoomed) {
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                } else {
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VideoControlsOverlay(
                media = media,
                state = state,
                showCenterControls = showInitialCenterControls && !areControlsLocked,
                isVideoZoomed = isVideoZoomed,
                isLandscape = isLandscape,
                isControlsLocked = areControlsLocked,
                onBack = {
                    markUserInteraction()
                    onBack()
                },
                onPlayPause = {
                    markUserInteraction()
                    viewModel.onEvent(MediaPlayerEvent.OnPlayPauseClicked)
                },
                onForward = {
                    markUserInteraction()
                    viewModel.onEvent(MediaPlayerEvent.OnForwardClicked)
                },
                onRewind = {
                    markUserInteraction()
                    viewModel.onEvent(MediaPlayerEvent.OnRewindClicked)
                },
                onNext = {
                    markUserInteraction()
                    viewModel.onEvent(MediaPlayerEvent.OnNextClicked)
                },
                onPrevious = {
                    markUserInteraction()
                    viewModel.onEvent(MediaPlayerEvent.OnPreviousClicked)
                },
                onSeek = { position ->
                    markUserInteraction()
                    viewModel.onEvent(MediaPlayerEvent.OnSeek(position))
                },
                onMuteToggle = {
                    markUserInteraction()
                    viewModel.onEvent(MediaPlayerEvent.OnMuteToggleClicked)
                },
                onResizeToggle = {
                    markUserInteraction()
                    isVideoZoomed = !isVideoZoomed
                },
                onRotateClick = {
                    markUserInteraction()
                    onRotateClick()
                },
                onLockToggle = {
                    areControlsLocked = !areControlsLocked
                    areControlsVisible = true
                    lastInteractionTime = System.currentTimeMillis()
                },
                onThreeDotsClick = {
                    markUserInteraction()
                    onThreeDotsClick()
                }
            )
        }
    }
}

private const val CONTROLS_AUTO_HIDE_DELAY_MS = 3_000L
private const val INITIAL_CENTER_CONTROLS_VISIBLE_MS = 3_000L