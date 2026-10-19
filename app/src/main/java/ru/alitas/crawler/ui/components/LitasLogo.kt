package ru.alitas.crawler.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import ru.alitas.crawler.ui.theme.LitasBlue
import ru.alitas.crawler.ui.theme.LitasCyan

/**
 * Программный векторный логотип ЛИТАС.
 *
 * Рисуется через Canvas, поэтому масштабируется без потери качества
 * и может использоваться на Splash, Home и других экранах.
 */
@Composable
fun LitasLogo(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.12f

        val brush = Brush.linearGradient(
            colors = listOf(
                LitasCyan,
                LitasBlue
            )
        )

        // Верхняя половина знака.
        drawLine(
            brush = brush,
            start = Offset(size.width * 0.20f, size.height * 0.43f),
            end = Offset(size.width * 0.43f, size.height * 0.20f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            brush = brush,
            start = Offset(size.width * 0.43f, size.height * 0.20f),
            end = Offset(size.width * 0.57f, size.height * 0.20f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            brush = brush,
            start = Offset(size.width * 0.57f, size.height * 0.20f),
            end = Offset(size.width * 0.80f, size.height * 0.43f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Нижняя половина знака.
        drawLine(
            brush = brush,
            start = Offset(size.width * 0.20f, size.height * 0.57f),
            end = Offset(size.width * 0.43f, size.height * 0.80f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            brush = brush,
            start = Offset(size.width * 0.43f, size.height * 0.80f),
            end = Offset(size.width * 0.57f, size.height * 0.80f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            brush = brush,
            start = Offset(size.width * 0.57f, size.height * 0.80f),
            end = Offset(size.width * 0.80f, size.height * 0.57f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
