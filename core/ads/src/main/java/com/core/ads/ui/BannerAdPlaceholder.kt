package com.core.ads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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

@Composable
fun BannerAdPlaceholder(
    heightDp: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .background(Color.DarkGray) ,
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = "Ad Loading",
            style = TextStyle(
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
        )
    }
}