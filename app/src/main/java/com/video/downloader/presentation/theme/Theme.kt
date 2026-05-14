package com.video.downloader.presentation.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat


object AppGradients {

    val HighlightVertical = Brush.verticalGradient(
        colors = listOf(
            AppColors.HighlightGradientTop,
            AppColors.HighlightGradientBottom
        )
    )

    val SplashBackground = Brush.verticalGradient(
        colors = listOf(
            AppColors.backgroundGradientTop,
            AppColors.backgroundGradientDown
        )
    )
    val dummy = Brush.verticalGradient(
        colors = listOf(
            AppColors.BackgroundDisabled,
            AppColors.BackgroundDisabled
        )
    )
}

/**
 * Reusable modifier for every highlighted gradient background:
 * buttons, selected chips, selected cards, premium banners, etc.
 */
fun Modifier.highlightGradientBackground(
    shape: Shape
): Modifier {
    return this.background(
        brush = AppGradients.HighlightVertical,
        shape = shape
    )
}


private val VideoDownloaderColorScheme = lightColorScheme(
    primary = AppColors.HighlightGradientTop,
    onPrimary = AppColors.OnHighlight,

    secondary = AppColors.HighlightGradientBottom,
    onSecondary = AppColors.OnHighlight,

    tertiary = AppColors.HighlightGradientTop,
    onTertiary = AppColors.OnHighlight,

    background = AppColors.Background,
    onBackground = AppColors.TextEnabled,

    surface = AppColors.Background,
    onSurface = AppColors.TextEnabled,

    surfaceVariant = AppColors.DisabledContainer,
    onSurfaceVariant = AppColors.TextDisabled,

    error = AppColors.Error,
    onError = AppColors.OnHighlight,

    outline = AppColors.TextDisabled,
    outlineVariant = AppColors.BorderLight,

    inverseSurface = AppColors.TextEnabled,
    inverseOnSurface = AppColors.Background,

    scrim = AppColors.Scrim
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

object AppCornerRadius {
    val ExtraSmall = 6.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val Full = 999.dp
}

@Composable
fun VideoDownloaderTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect

        window.statusBarColor = AppColors.Background.toArgb()
        window.navigationBarColor = AppColors.Background.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }

    MaterialTheme(
        colorScheme = VideoDownloaderColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}