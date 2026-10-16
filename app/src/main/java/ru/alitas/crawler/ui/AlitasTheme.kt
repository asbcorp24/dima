package ru.alitas.crawler.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AlitasBlue = Color(0xFF1F56B3)
val AlitasBlueDark = Color(0xFF123B82)
val AlitasDanger = Color(0xFFC83434)
val AlitasSuccess = Color(0xFF16835D)

private val Colors = lightColorScheme(
    primary = AlitasBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAF1FF),
    background = Color(0xFFF4F7FB),
    surface = Color.White,
    onBackground = Color(0xFF0F1E33),
    onSurface = Color(0xFF0F1E33),
    error = AlitasDanger
)

@Composable
fun AlitasTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, content = content)
}
