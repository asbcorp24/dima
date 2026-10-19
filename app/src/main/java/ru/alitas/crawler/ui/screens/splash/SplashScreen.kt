package ru.alitas.crawler.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.alitas.crawler.ui.components.LitasLogo
import ru.alitas.crawler.ui.theme.LitasBlue
import ru.alitas.crawler.ui.theme.LitasCyan
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Анимированная стартовая заставка ЛИТАС.
 *
 * После завершения анимации вызывает onFinished().
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.70f) }
    val titleAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(
        label = "splashAnimation"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(600)
            )
        }

        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            )
        )

        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(700)
        )

        delay(1700)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF010612),
                        Color(0xFF03132D),
                        Color(0xFF041D4B)
                    )
                )
            )
    ) {
        // Нижнее голубое свечение.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LitasBlue.copy(alpha = 0.15f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Пульсирующее свечение вокруг логотипа.
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(glowScale)
                        .alpha(glowAlpha)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    LitasCyan.copy(alpha = 0.50f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                LitasLogo(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "ЛИТАС",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "УПРАВЛЕНИЕ  |  ДИАГНОСТИКА  |  КОНТРОЛЬ",
                color = Color(0xFF9FBDE7),
                fontSize = 11.sp,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(Modifier.height(42.dp))

            LinearProgressIndicator(
                progress = { 0.82f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),
                color = LitasCyan,
                trackColor = Color(0xFF102C55)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Загрузка...",
                color = Color(0xFF8EA8CF),
                fontSize = 13.sp
            )
        }
    }
}

@Preview(
    name = "Splash — телефон",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun SplashPreview() {
    LitasTheme {
        SplashScreen(onFinished = {})
    }
}
