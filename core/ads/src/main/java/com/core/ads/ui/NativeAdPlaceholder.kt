package com.core.ads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.ads.domain.NativeAdSize
import com.core.ads.domain.NativeAdStyleConfig

@Composable
fun NativeAdPlaceholder(
    size: NativeAdSize,
    styleConfig: NativeAdStyleConfig,
    modifier: Modifier = Modifier,
    fillContainer: Boolean = false
) {
    val height = when (size) {
        NativeAdSize.SMALL -> 92.dp
        NativeAdSize.MEDIUM -> 140.dp
        NativeAdSize.LARGE -> 520.dp
    }

    val shape = RoundedCornerShape(
        styleConfig.cornerRadiusDp.dp
    )

    val sizedModifier = if (fillContainer) {
        modifier.fillMaxSize()
    } else {
        modifier
            .fillMaxWidth()
            .height(height)
    }

    Box(
        modifier = sizedModifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(shape)
            .background(
                parseComposeColor(
                    value = styleConfig.containerBackgroundColor,
                    fallback = Color(0xFFF8FAFC)
                )
            )
            .border(
                width = styleConfig.containerBorderWidthDp.dp,
                color = parseComposeColor(
                    value = styleConfig.containerBorderColor,
                    fallback = Color(0xFFE5E7EB)
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BasicText(
                text = "Ad Loading...",
                style = TextStyle(
                    color = parseComposeColor(
                        value = styleConfig.bodyTextColor,
                        fallback = Color(0xFF475569)
                    ),
                    fontSize = 13.sp
                )
            )
        }
    }
}

private fun parseComposeColor(
    value: String,
    fallback: Color
): Color {
    return runCatching {
        Color(android.graphics.Color.parseColor(value))
    }.getOrElse {
        fallback
    }
}