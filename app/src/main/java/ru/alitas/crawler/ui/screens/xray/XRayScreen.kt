package ru.alitas.crawler.ui.screens.xray

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerState
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.XRayState
import ru.alitas.crawler.ui.components.HomeHeader
import ru.alitas.crawler.ui.preview.PreviewTelemetry
import ru.alitas.crawler.ui.theme.LitasDanger
import ru.alitas.crawler.ui.theme.LitasSuccess
import ru.alitas.crawler.ui.theme.LitasTheme
import ru.alitas.crawler.ui.theme.LitasWarning

/**
 * Экран рентгеновского узла.
 *
 * ВАЖНО:
 * - приложение не принимает решение о радиационной безопасности;
 * - реальный запуск экспозиции должен подтверждаться Контроллером;
 * - до согласования протокола реальная команда экспозиции здесь не отправляется;
 * - интерактивный отсчёт ниже доступен только симулятору и служит для проверки UI.
 */
@Composable
fun XRayScreen(
    telemetry: CrawlerTelemetry,
    connectionStatus: ConnectionStatus,
    simulatorMode: Boolean,
    onStop: () -> Unit,
    onBack: () -> Unit
) {
    var confirmationStep by remember { mutableIntStateOf(0) }
    var countdown by remember { mutableIntStateOf(0) }
    var trainingMode by remember { mutableStateOf(true) }

    val controllerAllowsExposure =
        connectionStatus == ConnectionStatus.CONNECTED &&
            telemetry.state == CrawlerState.STOP &&
            telemetry.xrayState == XRayState.READY

    val simulatedExposureAllowed = simulatorMode && controllerAllowsExposure

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1_000)
            countdown -= 1
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF030817)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF020612),
                            Color(0xFF120B16),
                            Color(0xFF07152C)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HomeHeader(connectionStatus)

            Text(
                "Рентгеновский аппарат",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "Контроллер остаётся единственным источником разрешения на экспозицию.",
                color = Color(0xFF8198B8),
                fontSize = 13.sp
            )

            RadiationStatusCard(
                telemetry = telemetry,
                controllerAllowsExposure = controllerAllowsExposure
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                XRayMetricCard(
                    title = "Напряжение",
                    value = telemetry.xrayKv.toString(),
                    unit = "кВ",
                    modifier = Modifier.weight(1f)
                )
                XRayMetricCard(
                    title = "Ток",
                    value = "%.1f".format(telemetry.xrayMa),
                    unit = "мА",
                    modifier = Modifier.weight(1f)
                )
            }

            XRayMetricCard(
                title = "Аккумулятор рентгена",
                value = telemetry.xrayBattery.soc.toString(),
                unit = "%",
                modifier = Modifier.fillMaxWidth()
            )

            SafetyChecklist(
                connected = connectionStatus == ConnectionStatus.CONNECTED,
                stopped = telemetry.state == CrawlerState.STOP,
                xrayReady = telemetry.xrayState == XRayState.READY
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Режим проверки интерфейса",
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        if (simulatorMode) {
                            "Симулятор активен. Можно проверить двухэтапное подтверждение и обратный отсчёт без реального излучения."
                        } else {
                            "Реальная команда экспозиции отключена до согласования протокола, UUID, ACK и блокировок Контроллера."
                        },
                        color = Color(0xFF8198B8),
                        fontSize = 12.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ModeButton(
                            text = "Учебный",
                            selected = trainingMode,
                            modifier = Modifier.weight(1f)
                        ) {
                            trainingMode = true
                            confirmationStep = 0
                        }

                        ModeButton(
                            text = "Экспозиция",
                            selected = !trainingMode,
                            modifier = Modifier.weight(1f)
                        ) {
                            trainingMode = false
                            confirmationStep = 0
                        }
                    }
                }
            }

            when {
                countdown > 0 -> {
                    CountdownCard(
                        seconds = countdown,
                        trainingMode = trainingMode
                    )
                }

                confirmationStep == 0 -> {
                    Button(
                        onClick = { confirmationStep = 1 },
                        enabled = simulatedExposureAllowed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            if (trainingMode) "Начать учебный цикл" else "Подготовить экспозицию",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                confirmationStep == 1 -> {
                    WarningConfirmCard(
                        trainingMode = trainingMode,
                        onCancel = { confirmationStep = 0 },
                        onContinue = { confirmationStep = 2 }
                    )
                }

                else -> {
                    FinalConfirmCard(
                        trainingMode = trainingMode,
                        onCancel = { confirmationStep = 0 },
                        onConfirm = {
                            // Только UI-симуляция. Реальная команда здесь не отправляется.
                            countdown = 5
                            confirmationStep = 0
                        }
                    )
                }
            }

            Button(
                onClick = {
                    countdown = 0
                    confirmationStep = 0
                    onStop()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LitasDanger),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "STOP",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("← Назад к управлению")
            }
        }
    }
}

@Composable
private fun RadiationStatusCard(
    telemetry: CrawlerTelemetry,
    controllerAllowsExposure: Boolean
) {
    val stateText = when (telemetry.xrayState) {
        XRayState.READY -> "Готов"
        XRayState.TRAINING_REQUIRED -> "Требуется обучение"
        XRayState.ERROR -> "Ошибка"
        XRayState.EXPOSURE_FORBIDDEN -> "Экспозиция запрещена"
    }

    val stateColor = when (telemetry.xrayState) {
        XRayState.READY -> LitasSuccess
        XRayState.TRAINING_REQUIRED -> LitasWarning
        XRayState.ERROR,
        XRayState.EXPOSURE_FORBIDDEN -> LitasDanger
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101522)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "СОСТОЯНИЕ РЕНТГЕНОВСКОГО УЗЛА",
                color = Color(0xFF7489A8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                stateText,
                color = stateColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                if (controllerAllowsExposure) {
                    "Предварительные условия отображаются как выполненные."
                } else {
                    "Запуск недоступен. Проверьте состояние кроулера и ответ Контроллера."
                },
                color = Color(0xFF9CB0CA),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun XRayMetricCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(15.dp)) {
            Text(title, color = Color(0xFF7188A8), fontSize = 11.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    " $unit",
                    color = Color(0xFF8198B8),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SafetyChecklist(
    connected: Boolean,
    stopped: Boolean,
    xrayReady: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "ПРОВЕРКА УСЛОВИЙ",
                color = Color(0xFF7188A8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            SafetyRow("BLE-соединение", connected)
            SafetyRow("Кроулер остановлен", stopped)
            SafetyRow("Контроллер: рентген готов", xrayReady)
        }
    }
}

@Composable
private fun SafetyRow(label: String, ok: Boolean) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White)
        Text(
            if (ok) "✓ OK" else "✕ НЕТ",
            color = if (ok) LitasSuccess else LitasDanger,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun ModeButton(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text)
        }
    }
}

@Composable
private fun WarningConfirmCard(
    trainingMode: Boolean,
    onCancel: () -> Unit,
    onContinue: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2110)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Подтверждение 1 из 2",
                color = LitasWarning,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )

            Text(
                if (trainingMode) {
                    "Подтвердите запуск учебного цикла. Излучение не формируется."
                } else {
                    "Подтвердите подготовку экспозиции. Реальный запуск возможен только после разрешения Контроллера."
                },
                color = Color(0xFFE9D8B4)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отмена")
                }

                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Продолжить")
                }
            }
        }
    }
}

@Composable
private fun FinalConfirmCard(
    trainingMode: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF33141B)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Подтверждение 2 из 2",
                color = Color(0xFFFF7782),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )

            Text(
                if (trainingMode) {
                    "Запустить учебный обратный отсчёт?"
                } else {
                    "Подтвердите намерение начать экспозицию. В этой версии выполняется только симуляция интерфейса."
                },
                color = Color(0xFFF0C8CE)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отмена")
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = LitasDanger),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Подтвердить")
                }
            }
        }
    }
}

@Composable
private fun CountdownCard(
    seconds: Int,
    trainingMode: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (trainingMode) Color(0xFF0B2434) else Color(0xFF351218)
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                if (trainingMode) "УЧЕБНЫЙ ЦИКЛ" else "СИМУЛЯЦИЯ ЭКСПОЗИЦИИ",
                color = if (trainingMode) Color(0xFF55C8FF) else Color(0xFFFF7782),
                fontWeight = FontWeight.Black
            )

            Text(
                seconds.toString(),
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "STOP доступен в любой момент",
                color = Color(0xFF9BB0CA),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(
    name = "Рентген — симулятор",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun XRayPreview() {
    LitasTheme {
        XRayScreen(
            telemetry = PreviewTelemetry,
            connectionStatus = ConnectionStatus.CONNECTED,
            simulatorMode = true,
            onStop = {},
            onBack = {}
        )
    }
}
