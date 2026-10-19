package ru.alitas.crawler.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.BatteryTelemetry
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerState
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.Direction
import ru.alitas.crawler.domain.DiscoveredCrawler

@Composable
fun DashboardScreen(
    telemetry: CrawlerTelemetry,
    connectionStatus: ConnectionStatus,
    selectedCrawler: DiscoveredCrawler?,
    controlsEnabled: Boolean,
    onManualStart: (Direction) -> Unit,
    onManualStop: () -> Unit,
    onAutoStart: (Direction) -> Unit,
    onStop: () -> Unit,
    onSimulateLoss: () -> Unit,
    onChooseCrawler: () -> Unit
) {
    val manualAllowed = controlsEnabled &&
        (telemetry.state == CrawlerState.WAITING || telemetry.state == CrawlerState.STOP)
    val autoAllowed = controlsEnabled && telemetry.state == CrawlerState.STOP

    Surface(color = LitasBackground, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HeroHeader(telemetry, connectionStatus, selectedCrawler)

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (!controlsEnabled) {
                    WarningCard(
                        "Реальное BLE-соединение установлено",
                        "Управляющие команды отключены до согласования UUID и протокола Контроллера."
                    )
                }

                SectionTitle("ТЕЛЕМЕТРИЯ")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        title = "Расстояние",
                        value = "%.1f".format(telemetry.distanceMeters),
                        unit = "м",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Крен",
                        value = "%.1f".format(telemetry.rollDeg),
                        unit = "°",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Тангаж",
                        value = "%.1f".format(telemetry.pitchDeg),
                        unit = "°",
                        modifier = Modifier.weight(1f)
                    )
                }

                SectionTitle("ПИТАНИЕ")
                BatteryCard("Ходовой аккумулятор", telemetry.driveBattery)
                BatteryCard("Рентгеновский аккумулятор", telemetry.xrayBattery)

                SectionTitle("ДАТЧИКИ БЕЗОПАСНОСТИ")
                SensorGrid(telemetry)

                SectionTitle("УПРАВЛЕНИЕ")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Ручное движение",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Движение выполняется только пока кнопка удерживается.",
                            color = LitasMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HoldButton(
                                symbol = "↑",
                                label = "Вперёд",
                                enabled = manualAllowed,
                                modifier = Modifier.weight(1f),
                                onPress = { onManualStart(Direction.FORWARD) },
                                onRelease = onManualStop
                            )
                            HoldButton(
                                symbol = "↓",
                                label = "Назад",
                                enabled = manualAllowed,
                                modifier = Modifier.weight(1f),
                                onPress = { onManualStart(Direction.BACKWARD) },
                                onRelease = onManualStop
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { onAutoStart(Direction.FORWARD) },
                                enabled = autoAllowed,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Авто вперёд") }

                            OutlinedButton(
                                onClick = { onAutoStart(Direction.BACKWARD) },
                                enabled = autoAllowed,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Авто назад") }
                        }

                        Button(
                            onClick = onStop,
                            enabled = controlsEnabled,
                            colors = ButtonDefaults.buttonColors(containerColor = LitasDanger),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().height(72.dp)
                        ) {
                            Text("STOP", fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                SectionTitle("РЕНТГЕНОВСКИЙ АППАРАТ")
                XRayCard(telemetry)

                if (selectedCrawler?.isSimulator == true) {
                    OutlinedButton(
                        onClick = onSimulateLoss,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Симулировать потерю BLE") }
                }

                OutlinedButton(
                    onClick = onChooseCrawler,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Отключиться и выбрать кроулер")
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(
    telemetry: CrawlerTelemetry,
    connectionStatus: ConnectionStatus,
    selectedCrawler: DiscoveredCrawler?
) {
    val connected = connectionStatus == ConnectionStatus.CONNECTED
    val stateColor = when (telemetry.state) {
        CrawlerState.STOP -> LitasDanger
        CrawlerState.MOVING -> LitasCyan
        CrawlerState.EXPOSURE -> LitasWarning
        CrawlerState.CALIBRATION -> Color(0xFF7D5CE7)
        CrawlerState.WAITING -> Color(0xFF91A3BD)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(LitasBlueDark, Color(0xFF123F8D))
                )
            )
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "ЛИТАС",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        selectedCrawler?.name ?: telemetry.name,
                        color = Color(0xFFD5E3FF),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        selectedCrawler?.serialNumber ?: telemetry.serialNumber,
                        color = Color(0xFFAFC6F4),
                        fontSize = 12.sp
                    )
                }

                ConnectionPill(
                    connectionStatus,
                    selectedCrawler?.rssi ?: telemetry.bleRssiDbm
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier
                        .size(14.dp)
                        .background(stateColor, RoundedCornerShape(14.dp))
                )
                Column {
                    Text(
                        "СОСТОЯНИЕ КРОУЛЕРА",
                        color = Color(0xFFAFC6F4),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stateLabel(telemetry.state),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (!connected) {
                Text(
                    connectionLabel(connectionStatus),
                    color = Color(0xFFFFD29B),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ConnectionPill(status: ConnectionStatus, rssi: Int) {
    val connected = status == ConnectionStatus.CONNECTED
    val bg = if (connected) Color(0xFF103D34) else Color(0xFF5D2C32)
    val dot = if (connected) Color(0xFF63E6BE) else Color(0xFFFF8A8A)

    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50.dp))
            .padding(horizontal = 11.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(Modifier.size(8.dp).background(dot, RoundedCornerShape(8.dp)))
        Text(
            if (connected) "$rssi dBm" else connectionLabel(status),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = LitasMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(title, color = LitasMuted, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = LitasInk)
                Spacer(Modifier.width(3.dp))
                Text(unit, color = LitasMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun BatteryCard(title: String, value: BatteryTelemetry) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(title, fontWeight = FontWeight.Black)
                    Text(
                        "%.1f В · %.1f А · %d °C".format(
                            value.voltage,
                            value.current,
                            value.temperature
                        ),
                        color = LitasMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    "${value.soc}%",
                    color = batteryColor(value.soc),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            LinearProgressIndicator(
                progress = { value.soc.coerceIn(0, 100) / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = batteryColor(value.soc),
                trackColor = Color(0xFFE8EDF5)
            )
        }
    }
}

@Composable
private fun SensorGrid(telemetry: CrawlerTelemetry) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SensorCard("Начало трубы", telemetry.sensors.pipeStart, Modifier.weight(1f))
            SensorCard("Конец трубы", telemetry.sensors.pipeEnd, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SensorCard("Вода", telemetry.sensors.water, Modifier.weight(1f))
            SensorCard("Препятствие", telemetry.sensors.obstacle, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SensorCard(label: String, active: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (active) Color(0xFFFFEEEE) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                Modifier
                    .size(11.dp)
                    .background(
                        if (active) LitasDanger else LitasSuccess,
                        RoundedCornerShape(11.dp)
                    )
            )
            Column {
                Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    if (active) "СРАБОТАЛ" else "Норма",
                    color = if (active) LitasDanger else LitasSuccess,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun WarningCard(title: String, message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E8)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(15.dp)) {
            Text(title, color = LitasWarning, fontWeight = FontWeight.Black)
            Text(message, color = LitasInk, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun XRayCard(telemetry: CrawlerTelemetry) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Состояние", color = LitasMuted, fontSize = 12.sp)
                    Text(
                        "Готов",
                        color = LitasSuccess,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniValue("${telemetry.xrayKv}", "кВ")
                    MiniValue("${telemetry.xrayMa}", "мА")
                }
            }

            Text(
                "Экспозиция будет добавлена отдельным этапом с двухшаговым подтверждением.",
                color = LitasMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MiniValue(value: String, unit: String) {
    Column(horizontalAlignment = Alignment.End) {
        Text(value, fontSize = 19.sp, fontWeight = FontWeight.Black)
        Text(unit, color = LitasMuted, fontSize = 11.sp)
    }
}

@Composable
private fun HoldButton(
    symbol: String,
    label: String,
    enabled: Boolean,
    modifier: Modifier,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    Box(
        modifier = modifier
            .height(118.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .background(
                brush = if (enabled) {
                    Brush.verticalGradient(listOf(LitasBlue, Color(0xFF164DB1)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFB9C2D0), Color(0xFFAAB5C4)))
                },
                shape = RoundedCornerShape(18.dp)
            )
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        onPress()
                        try {
                            tryAwaitRelease()
                        } finally {
                            onRelease()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
            Text(label, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text("УДЕРЖИВАТЬ", color = Color(0xFFD8E6FF), fontSize = 10.sp)
        }
    }
}

private fun stateLabel(state: CrawlerState): String = when (state) {
    CrawlerState.WAITING -> "Ожидание"
    CrawlerState.STOP -> "Стоп"
    CrawlerState.MOVING -> "Движение"
    CrawlerState.EXPOSURE -> "Экспозиция"
    CrawlerState.CALIBRATION -> "Калибровка"
}

private fun connectionLabel(status: ConnectionStatus): String = when (status) {
    ConnectionStatus.DISCONNECTED -> "Отключено"
    ConnectionStatus.SCANNING -> "Поиск"
    ConnectionStatus.CONNECTING -> "Подключение"
    ConnectionStatus.CONNECTED -> "Подключено"
    ConnectionStatus.RECONNECTING -> "Переподключение"
    ConnectionStatus.LOST -> "Связь потеряна"
}

private fun batteryColor(soc: Int): Color = when {
    soc >= 50 -> LitasSuccess
    soc >= 20 -> LitasWarning
    else -> LitasDanger
}
