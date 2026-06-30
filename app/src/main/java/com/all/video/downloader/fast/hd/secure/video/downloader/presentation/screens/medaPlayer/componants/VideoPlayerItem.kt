package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants

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
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.screen.MediaPlayerBrightnessVolumeGestureController
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSeekFeedbackDirection

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerItem(
    media: MediaFile,
    viewModel: MediaPlayerViewModel,
    isLandscape: Boolean,
    onBack: () -> Unit,
    onRotateClick: () -> Unit,
    onPictureInPictureClick: () -> Unit,
    onThreeDotsClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = LocalActivity.current

    val brightnessVolumeGestureController = remember(
        context,
        activity
    ) {
        MediaPlayerBrightnessVolumeGestureController(
            context = context,
            activity = activity
        )
    }

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
        state.showEqualizerDialog,
        state.showSubtitleDialog,
        state.isInPictureInPictureMode,
        state.showFileInfoDialog,
        state.showRenameFileDialog,
        state.showDeleteFileDialog,
        state.isPlaybackEnded,
        state.showSetAsRingtoneDialog
    ) {
        val shouldKeepControlsVisible =
            state.isInPictureInPictureMode ||
                    state.isPlaybackEnded ||
                    areControlsLocked ||
                    !state.isPlaying ||
                    state.showBottomSheet ||
                    state.showPlaybackSpeedDialog ||
                    state.showEqualizerDialog ||
                    state.showSubtitleDialog ||
                    state.showFileInfoDialog ||
                    state.showRenameFileDialog ||
                    state.showDeleteFileDialog ||
                    state.showSetAsRingtoneDialog

        if (!areControlsVisible || shouldKeepControlsVisible) {
            return@LaunchedEffect
        }

        val interactionSnapshot = lastInteractionTime

        delay(CONTROLS_AUTO_HIDE_DELAY_MS.milliseconds)

        if (
            interactionSnapshot == lastInteractionTime &&
            state.isPlaying &&
            !state.isPlaybackEnded &&
            !state.showBottomSheet &&
            !state.showPlaybackSpeedDialog &&
            !state.showEqualizerDialog &&
            !state.showSubtitleDialog &&
            !state.showFileInfoDialog &&
            !state.showRenameFileDialog &&
            !state.showDeleteFileDialog &&
            !state.showSetAsRingtoneDialog
        ) {
            areControlsVisible = false
        }
    }

    val arePlayerTransientSurfacesOpen =
        state.showBottomSheet ||
                state.showPlaybackSpeedDialog ||
                state.showEqualizerDialog ||
                state.showSubtitleDialog ||
                state.showFileInfoDialog ||
                state.showRenameFileDialog ||
                state.showDeleteFileDialog ||
                state.showSetAsRingtoneDialog

    val canHandlePlayerSurfaceGestures =
        !state.isInPictureInPictureMode &&
                !arePlayerTransientSurfacesOpen &&
                !areControlsLocked

    val isBrightnessVolumeGestureEnabled =
        canHandlePlayerSurfaceGestures &&
                !state.isPlaybackEnded

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(
                state.isPlaying,
                areControlsVisible,
                canHandlePlayerSurfaceGestures
            )  {
                detectTapGestures(
                    onTap = {
                        if (!canHandlePlayerSurfaceGestures) return@detectTapGestures

                        if (areControlsVisible && state.isPlaying) {
                            areControlsVisible = false
                        } else {
                            markUserInteraction()
                        }
                    },
                    onDoubleTap = { offset ->
                        if (!canHandlePlayerSurfaceGestures) return@detectTapGestures

                        markUserInteraction()

                        val tappedOnLeftSide = offset.x < size.width / 2f

                        if (tappedOnLeftSide) {
                            viewModel.onEvent(
                                MediaPlayerEvent.OnSeekFeedbackRequested(
                                    MediaPlayerSeekFeedbackDirection.Rewind
                                )
                            )
                            viewModel.onEvent(MediaPlayerEvent.OnRewindClicked)
                        } else {
                            viewModel.onEvent(
                                MediaPlayerEvent.OnSeekFeedbackRequested(
                                    MediaPlayerSeekFeedbackDirection.Forward
                                )
                            )
                            viewModel.onEvent(MediaPlayerEvent.OnForwardClicked)
                        }
                    }
                )
            }
            .mediaPlayerBrightnessVolumeGesture(
                enabled = isBrightnessVolumeGestureEnabled,
                controller = brightnessVolumeGestureController,
                onFeedbackChanged = { type, percent ->
                    viewModel.onEvent(
                        MediaPlayerEvent.OnGestureFeedbackChanged(
                            type = type,
                            percent = percent
                        )
                    )
                },
                onGestureFinished = {
                    viewModel.onEvent(MediaPlayerEvent.OnGestureFeedbackFinished)
                }
            )
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
            visible = areControlsVisible && !state.isInPictureInPictureMode,
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
                onPictureInPictureClick = {
                    markUserInteraction()
                    onPictureInPictureClick()
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
        MediaPlayerSeekFeedbackOverlay(
            state = state.seekFeedbackState,
            modifier = Modifier.align(Alignment.Center)
        )

        MediaPlayerGestureFeedbackOverlay(
            state = state.gestureFeedbackState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

private const val CONTROLS_AUTO_HIDE_DELAY_MS = 3_000L
private const val INITIAL_CENTER_CONTROLS_VISIBLE_MS = 3_000L