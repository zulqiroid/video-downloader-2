package com.video.downloader.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.video.downloader.R

val PlusJakartaSans = FontFamily(
    Font(
        resId = R.font.plus_jakarta_sans_extra_light,
        weight = FontWeight.ExtraLight
    ),
    Font(
        resId = R.font.plus_jakarta_sans_extra_light,
        weight = FontWeight.Light
    ),
    Font(
        resId = R.font.plus_jakarta_sans_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.plus_jakarta_sans_medium,
        weight = FontWeight.Medium
    ),
    Font(
        resId = R.font.plus_jakarta_sans_semi_bold,
        weight = FontWeight.SemiBold
    ),
    Font(
        resId = R.font.plus_jakarta_sans_bold,
        weight = FontWeight.Bold
    ),
    Font(
        resId = R.font.plus_jakarta_sans_extra_bold,
        weight = FontWeight.ExtraBold
    )
)

private fun appTextStyle(
    fontWeight: FontWeight,
    fontSize: Int,
    lineHeight: Int,
    letterSpacing: Double = 0.0,
): TextStyle {
    return TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = fontWeight,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = letterSpacing.sp
    )
}

val AppTypography = Typography(
    displayLarge = appTextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 57,
        lineHeight = 64,
        letterSpacing = -0.25
    ),
    displayMedium = appTextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 45,
        lineHeight = 52
    ),
    displaySmall = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36,
        lineHeight = 44
    ),

    headlineLarge = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32,
        lineHeight = 40
    ),
    headlineMedium = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28,
        lineHeight = 36
    ),
    headlineSmall = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24,
        lineHeight = 32
    ),

    titleLarge = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22,
        lineHeight = 28
    ),
    titleMedium = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16,
        lineHeight = 24,
        letterSpacing = 0.15
    ),
    titleSmall = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14,
        lineHeight = 20,
        letterSpacing = 0.1
    ),

    bodyLarge = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16,
        lineHeight = 24,
        letterSpacing = 0.15
    ),
    bodyMedium = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14,
        lineHeight = 20,
        letterSpacing = 0.25
    ),
    bodySmall = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12,
        lineHeight = 16,
        letterSpacing = 0.4
    ),

    labelLarge = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14,
        lineHeight = 20,
        letterSpacing = 0.1
    ),
    labelMedium = appTextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12,
        lineHeight = 16,
        letterSpacing = 0.5
    ),
    labelSmall = appTextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11,
        lineHeight = 16,
        letterSpacing = 0.5
    )
)

/**
 * Extra app-specific typography tokens.
 *
 * Material3 gives us 15 default styles, but real production apps usually need
 * more semantic text styles so screens do not hardcode font sizes everywhere.
 */
object AppTextStyles {


    val titleLarge = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22,
        lineHeight = 28
    )

    val titleMedium = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16,
        lineHeight = 24,
        letterSpacing = 0.15
    )

    val titleSmall = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14,
        lineHeight = 20,
        letterSpacing = 0.1
    )


    val bodyLarge = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18,
        lineHeight = 24,
        letterSpacing = 0.15
    )

    val bodyMedium = appTextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 16,
        lineHeight = 18
    )

    val bodySmall = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14,
        lineHeight = 20,
        letterSpacing = 0.25
    )


    val splashTitle = appTextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34,
        lineHeight = 42
    )

    val screenTitle = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 26,
        lineHeight = 34
    )

    val screenSubtitle = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15,
        lineHeight = 23
    )

    val sectionTitle = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20,
        lineHeight = 28
    )

    val cardTitle = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 17,
        lineHeight = 24
    )

    val cardSubtitle = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13,
        lineHeight = 20
    )

    val buttonLarge = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18,
        lineHeight = 24
    )

    val buttonMedium = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14,
        lineHeight = 20
    )

    val buttonSmall = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12,
        lineHeight = 16
    )

    val inputText = appTextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 15,
        lineHeight = 22
    )

    val inputPlaceholder = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15,
        lineHeight = 22
    )

    val bottomNavLabel = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 11,
        lineHeight = 14
    )

    val tabSelected = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14,
        lineHeight = 20
    )

    val tabUnselected = appTextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14,
        lineHeight = 20
    )

    val chipSelected = appTextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 13,
        lineHeight = 18
    )

    val chipUnselected = appTextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13,
        lineHeight = 18
    )

    val dialogTitle = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22,
        lineHeight = 30
    )

    val dialogMessage = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14,
        lineHeight = 22
    )

    val error = appTextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12,
        lineHeight = 18
    )

    val caption = appTextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12,
        lineHeight = 16
    )

    val overline = appTextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 10,
        lineHeight = 14,
        letterSpacing = 0.8
    )
}