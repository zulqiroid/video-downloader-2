package com.video.downloader.presentation.common.haptics

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import kotlin.math.abs

fun Modifier.hapticDragFeedback(
    hapticFeedback: AppHapticFeedback,
    tickThresholdPx: Float = 80f,
    onDragStarted: () -> Unit = {},
    onDragEnded: () -> Unit = {},
    onDragCancelled: () -> Unit = {}
): Modifier {
    return pointerInput(hapticFeedback, tickThresholdPx) {
        var totalDragDistance = 0f
        var lastTickDistance = 0f

        detectDragGestures(
            onDragStart = {
                totalDragDistance = 0f
                lastTickDistance = 0f

                hapticFeedback.perform(AppHapticType.DragStart)
                onDragStarted()
            },
            onDrag = { change, dragAmount ->
                change.consume()

                totalDragDistance += abs(dragAmount.x) + abs(dragAmount.y)

                if (totalDragDistance - lastTickDistance >= tickThresholdPx) {
                    lastTickDistance = totalDragDistance
                    hapticFeedback.perform(AppHapticType.FrequentTick)
                }
            },
            onDragEnd = {
                hapticFeedback.perform(AppHapticType.GestureEnd)
                onDragEnded()
            },
            onDragCancel = {
                hapticFeedback.perform(AppHapticType.Reject)
                onDragCancelled()
            }
        )
    }
}