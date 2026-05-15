package com.video.downloader.presentation.common.haptics

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

class AppHapticFeedback(
    private val view: View
) {

    fun perform(type: AppHapticType) {
        val feedbackConstant = type.toPlatformFeedbackConstant()

        view.performHapticFeedback(feedbackConstant)
    }

    private fun AppHapticType.toPlatformFeedbackConstant(): Int {
        return when (this) {
            AppHapticType.Click -> HapticFeedbackConstants.VIRTUAL_KEY

            AppHapticType.Confirm -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.VIRTUAL_KEY
                }
            }

            AppHapticType.Reject -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.REJECT
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
            }

            AppHapticType.Tick -> {
                if (Build.VERSION.SDK_INT >= 34) {
                    HapticFeedbackConstants.SEGMENT_TICK
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
            }

            AppHapticType.FrequentTick -> {
                if (Build.VERSION.SDK_INT >= 34) {
                    HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
            }

            AppHapticType.ToggleOn -> {
                if (Build.VERSION.SDK_INT >= 34) {
                    HapticFeedbackConstants.TOGGLE_ON
                } else {
                    HapticFeedbackConstants.VIRTUAL_KEY
                }
            }

            AppHapticType.ToggleOff -> {
                if (Build.VERSION.SDK_INT >= 34) {
                    HapticFeedbackConstants.TOGGLE_OFF
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
            }

            AppHapticType.GestureStart -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.GESTURE_START
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
            }

            AppHapticType.GestureEnd -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.GESTURE_END
                } else {
                    HapticFeedbackConstants.VIRTUAL_KEY
                }
            }

            AppHapticType.DragStart -> {
                if (Build.VERSION.SDK_INT >= 34) {
                    HapticFeedbackConstants.DRAG_START
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
            }

            AppHapticType.LoadingStart -> HapticFeedbackConstants.CLOCK_TICK

            AppHapticType.LoadingTick -> {
                if (Build.VERSION.SDK_INT >= 34) {
                    HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
            }
        }
    }
}

@Composable
fun rememberAppHapticFeedback(): AppHapticFeedback {
    val view = LocalView.current

    return remember(view) {
        AppHapticFeedback(view = view)
    }
}