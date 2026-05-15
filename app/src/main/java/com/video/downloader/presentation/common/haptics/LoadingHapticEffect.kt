package com.video.downloader.presentation.common.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun LoadingHapticEffect(
    isLoading: Boolean,
    hapticFeedback: AppHapticFeedback,
    intervalMillis: Long = 850L,
    maxPulses: Int = 3
) {
    LaunchedEffect(isLoading) {
        if (!isLoading) return@LaunchedEffect

        hapticFeedback.perform(AppHapticType.LoadingStart)

        repeat(maxPulses) {
            delay(intervalMillis)
            hapticFeedback.perform(AppHapticType.LoadingTick)
        }
    }
}