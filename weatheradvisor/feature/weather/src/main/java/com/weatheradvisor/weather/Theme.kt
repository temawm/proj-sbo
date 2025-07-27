package com.weatheradvisor.weather

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val onPrimary = Color(0xFFF3F3F3)

val MyLightColorScheme = lightColorScheme(
    onPrimary = onPrimary
)

val MyDarkColorScheme = darkColorScheme(
    onPrimary = onPrimary,
)


@Composable
fun WeatherAdvisorColorScheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) MyDarkColorScheme else MyLightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}


