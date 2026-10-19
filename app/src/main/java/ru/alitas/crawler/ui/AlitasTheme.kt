package ru.alitas.crawler.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AlitasBlue = Color(0xFF1B5ED7)
val AlitasBlueDark = Color(0xFF0B2E68)
val AlitasBlueSoft = Color(0xFFEAF2FF)
val AlitasCyan = Color(0xFF25A7E8)
val AlitasDanger = Color(0xFFD63B3B)
val AlitasWarning = Color(0xFFE59A19)
val AlitasSuccess = Color(0xFF148760)
val AlitasInk = Color(0xFF11233F)
val AlitasMuted = Color(0xFF64748B)
val AlitasBorder = Color(0xFFDCE4F0)
val AlitasBackground = Color(0xFFF3F6FB)

private val Colors = lightColorScheme(
    primary = AlitasBlue,
    onPrimary = Color.White,
    primaryContainer = AlitasBlueSoft,
    onPrimaryContainer = AlitasBlueDark,
    secondary = AlitasCyan,
    background = AlitasBackground,
    surface = Color.White,
    surfaceVariant = Color(0xFFF7F9FC),
    onBackground = AlitasInk,
    onSurface = AlitasInk,
    onSurfaceVariant = AlitasMuted,
    error = AlitasDanger
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black),
    headlineMedium = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
)

@Composable
fun AlitasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colors,
        typography = AppTypography,
        content = content
    )
}
