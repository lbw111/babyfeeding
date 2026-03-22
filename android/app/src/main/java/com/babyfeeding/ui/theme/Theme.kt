package com.babyfeeding.ui.theme

import androidx.compose.material3.LightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val BabyFeedingColorScheme = LightColorScheme(
    primary = CoralPink,
    onPrimary = WarmWhite,
    secondary = MintGreen,
    onSecondary = DarkBrown,
    background = WarmWhite,
    onBackground = DarkBrown,
    surface = CreamWhite,
    onSurface = DarkBrown,
    surfaceVariant = CreamWhite,
    onSurfaceVariant = MediumBrown
)

@Composable
fun BabyFeedingTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BabyFeedingColorScheme,
        typography = BabyFeedingTypography,
        content = content
    )
}
