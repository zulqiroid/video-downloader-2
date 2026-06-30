package com.core.ads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun BannerDebugLabel(
    placementKey: String,
    status: String,
    unitSource: String?,
    adUnitId: String?,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    val safeAdUnitId = adUnitId.maskAdUnitId()

    val errorPart = errorMessage
        ?.takeIf { it.isNotBlank() }
        ?.let { " | error=${it.take(MAX_ERROR_LENGTH)}" }
        .orEmpty()

    BasicText(
        text = "BANNER[$placementKey] status=$status | source=${unitSource ?: "-"} | unit=$safeAdUnitId$errorPart",
        modifier = modifier
            .fillMaxWidth()
            .background(DEBUG_BACKGROUND_COLOR)
            .border(
                width = 1.dp,
                color = DEBUG_BORDER_COLOR
            )
            .padding(
                horizontal = 8.dp,
                vertical = 5.dp
            ),
        style = TextStyle(
            color = DEBUG_TEXT_COLOR,
            fontSize = 10.sp
        ),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

private fun String?.maskAdUnitId(): String {
    val value = this
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: return "-"

    return if (value.length <= VISIBLE_UNIT_ID_SUFFIX_LENGTH) {
        value
    } else {
        "…${value.takeLast(VISIBLE_UNIT_ID_SUFFIX_LENGTH)}"
    }
}

private const val VISIBLE_UNIT_ID_SUFFIX_LENGTH = 8
private const val MAX_ERROR_LENGTH = 120

private val DEBUG_BACKGROUND_COLOR = Color(0xFFFFF7CC)
private val DEBUG_BORDER_COLOR = Color(0xFFFFC107)
private val DEBUG_TEXT_COLOR = Color(0xFF3D2F00)