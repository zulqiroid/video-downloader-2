package com.video.downloader.presentation.common.haptics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

fun Modifier.hapticClickable(
    hapticFeedback: AppHapticFeedback,
    enabled: Boolean = true,
    hapticType: AppHapticType = AppHapticType.Click,
    role: Role? = Role.Button,
    onClick: () -> Unit
): Modifier {
    return composed {
        val interactionSource = remember { MutableInteractionSource() }

        clickable(
            enabled = enabled,
            role = role,
            interactionSource = interactionSource,
            indication = null
        ) {
            hapticFeedback.perform(hapticType)
            onClick()
        }
    }
}