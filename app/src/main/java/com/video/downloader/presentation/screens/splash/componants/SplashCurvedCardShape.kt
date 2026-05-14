package com.video.downloader.presentation.screens.splash.componants

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class SplashCurvedCardShape(
    private val curveHeight: Dp = 68.dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val curveHeightPx = with(density) { curveHeight.toPx() }

        val path = Path().apply {
            moveTo(0f, curveHeightPx)

            cubicTo(
                size.width * 0.22f,
                curveHeightPx * 0.04f,
                size.width * 0.78f,
                curveHeightPx * 0.04f,
                size.width,
                curveHeightPx
            )

            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        return Outline.Generic(path)
    }
}