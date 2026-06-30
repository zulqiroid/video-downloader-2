package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.screen.MediaPlayerBrightnessVolumeGestureController
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerGestureFeedbackType
import kotlin.math.abs

fun Modifier.mediaPlayerBrightnessVolumeGesture(
    enabled: Boolean,
    controller: MediaPlayerBrightnessVolumeGestureController,
    onFeedbackChanged: (MediaPlayerGestureFeedbackType, Int) -> Unit,
    onGestureFinished: () -> Unit,
): Modifier {
    if (!enabled) return this

    return pointerInput(controller) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)

            if (
                down.position.isInsideVerticalExcludedArea(
                    height = size.height,
                    excludedTopFraction = EXCLUDED_TOP_FRACTION,
                    excludedBottomFraction = EXCLUDED_BOTTOM_FRACTION
                )
            ) {
                awaitGestureEnd()
                return@awaitEachGesture
            }

            val gestureType = down.position.resolveGestureType(
                width = size.width
            )

            val startFraction = when (gestureType) {
                MediaPlayerGestureFeedbackType.Brightness -> {
                    controller.currentBrightnessFraction()
                }

                MediaPlayerGestureFeedbackType.Volume -> {
                    controller.currentVolumeFraction()
                }
            }

            var accumulatedDrag = Offset.Zero
            var totalVerticalFractionDelta = 0f
            var hasVerticalGestureStarted = false
            var shouldIgnoreGesture = false
            var lastFeedbackPercent = -1

            while (true) {
                val event = awaitPointerEvent()

                val change = event.changes.firstOrNull { pointerChange ->
                    pointerChange.id == down.id
                } ?: break

                if (change.changedToUpIgnoreConsumed()) {
                    break
                }

                val dragAmount = change.positionChange()

                if (!hasVerticalGestureStarted) {
                    accumulatedDrag += dragAmount

                    val horizontalAbs = abs(accumulatedDrag.x)
                    val verticalAbs = abs(accumulatedDrag.y)

                    val passedTouchSlop = verticalAbs >= viewConfiguration.touchSlop
                    val isClearlyHorizontal = horizontalAbs > verticalAbs
                    val isVerticalIntent = verticalAbs > horizontalAbs * VERTICAL_INTENT_RATIO

                    if (isClearlyHorizontal && horizontalAbs >= viewConfiguration.touchSlop) {
                        shouldIgnoreGesture = true
                        awaitGestureEnd()
                        break
                    }

                    if (!passedTouchSlop || !isVerticalIntent) {
                        continue
                    }

                    hasVerticalGestureStarted = true
                }

                change.consume()

                totalVerticalFractionDelta += (-dragAmount.y / size.height.coerceAtLeast(1)) *
                        GESTURE_SENSITIVITY

                val targetFraction = (startFraction + totalVerticalFractionDelta)
                    .coerceIn(
                        minimumValue = MIN_FRACTION,
                        maximumValue = MAX_FRACTION
                    )

                val feedbackPercent = when (gestureType) {
                    MediaPlayerGestureFeedbackType.Brightness -> {
                        controller.setBrightnessFraction(targetFraction)
                    }

                    MediaPlayerGestureFeedbackType.Volume -> {
                        controller.setVolumeFraction(targetFraction)
                    }
                }

                if (feedbackPercent != lastFeedbackPercent) {
                    lastFeedbackPercent = feedbackPercent
                    onFeedbackChanged(gestureType, feedbackPercent)
                }
            }

            if (hasVerticalGestureStarted && !shouldIgnoreGesture) {
                onGestureFinished()
            }
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitGestureEnd() {
    while (true) {
        val event = awaitPointerEvent()

        if (event.changes.all { change -> change.changedToUpIgnoreConsumed() }) {
            return
        }
    }
}

private fun Offset.resolveGestureType(
    width: Int,
): MediaPlayerGestureFeedbackType {
    return if (x < width / 2f) {
        MediaPlayerGestureFeedbackType.Brightness
    } else {
        MediaPlayerGestureFeedbackType.Volume
    }
}

private fun Offset.isInsideVerticalExcludedArea(
    height: Int,
    excludedTopFraction: Float,
    excludedBottomFraction: Float,
): Boolean {
    val safeHeight = height.coerceAtLeast(1)
    val topLimit = safeHeight * excludedTopFraction
    val bottomLimit = safeHeight * (1f - excludedBottomFraction)

    return y <= topLimit || y >= bottomLimit
}

private const val MIN_FRACTION = 0f
private const val MAX_FRACTION = 1f

private const val EXCLUDED_TOP_FRACTION = 0.16f
private const val EXCLUDED_BOTTOM_FRACTION = 0.24f

private const val VERTICAL_INTENT_RATIO = 1.45f
private const val GESTURE_SENSITIVITY = 1.08f