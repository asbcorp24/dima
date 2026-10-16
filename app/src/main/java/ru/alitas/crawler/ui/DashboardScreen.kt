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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ConnectionCard(connectionStatus, selectedCrawler)

        Card(shape = RoundedCornerShape(22.dp)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(AlitasBlueDark)
                    .padding(20.dp)
            ) {
                Text("АЛИТАС", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("СОСТОЯНИЕ КРОУЛЕРА", color = Color(0xFFBFD2FF), fontSize = 12.sp)
                Text(
                    text = stateLabel(telemetry.state),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    selectedCrawler?.let { it.name + " · " + it.serialNumber }
                        ?: telemetry.name + " · " + telemetry.serialNumber,
                    color = Color(0xFFDCE7FF)
                )
                Text("RSSI: ${selectedCrawler?.rssi ?: telemetry.bleRssiDbm} dBm", color = Color.White)
            }
        }

        if (!controlsEnabled) {
            Card {
                Text(
                    "Реальное BLE-соединение установлено, но управляющие команды отключены до согласования UUID и протокола Контроллера.",
                    modifier = Modifier.padding(16.dp),
                    color = AlitasDanger,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Metric("Расстояние", "%.1f м".format(telemetry.distanceMeters))
        Metric("Крен / тангаж", "%.1f° / %.1f°".format(telemetry.rollDeg, telemetry.pitchDeg))
        Battery("Ходовой аккумулятор", telemetry.driveBattery)
        Battery("Рентгеновский аккумулятор", telemetry.xrayBattery)

        Text("Датчики безопасности", fontWeight = FontWeight.Black)
        Sensor("Начало трубы", telemetry.sensors.pipeStart)
        Sensor("Конец трубы", telemetry.sensors.pipeEnd)
        Sensor("Вода", telemetry.sensors.water)
        Sensor("Препятствие", telemetry.sensors.obstacle)

        Text("Ручное управление", fontWeight = FontWeight.Black)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HoldButton(
                "↑",
                "Вперёд",
                manualAllowed,
                Modifier.weight(1f),
                { onManualStart(Direction.FORWARD) },
                onManualStop
            )
            HoldButton(
                "↓",
                "Назад",
                manualAllowed,
                Modifier.weight(1f),
                { onManualStart(Direction.BACKWARD) },
                onManualStop
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { onAutoStart(Direction.FORWARD) },
                enabled = autoAllowed,
                modifier = Modifier.weight(1f)
            ) { Text("Авто вперёд") }

            OutlinedButton(
                onClick = { onAutoStart(Direction.BACKWARD) },
                enabled = autoAllowed,
                modifier = Modifier.weight(1f)
            ) { Text("Авто назад") }
        }

        Button(
            onClick = onStop,
            enabled = controlsEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = AlitasDanger),
            modifier = Modifier.fillMaxWidth().height(74.dp)
        ) {
            Text("STOP", fontSize = 28.sp, fontWeight = FontWeight.Black)
        }

        if (selectedCrawler?.isSimulator == true) {
            OutlinedButton(
                onClick = onSimulateLoss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Симулировать потерю BLE")
            }
        }

        OutlinedButton(
            onClick = onChooseCrawler,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отключиться и выбрать кроулер")
        }

        Card {
            Column(Modifier.padding(16.dp)) {
                Text("РЕНТГЕНОВСКИЙ АППАРАТ", color = Color.Gray, fontSize = 12.sp)
                Text("Готов", color = AlitasSuccess, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text(telemetry.xrayKv.toString() + " кВ · " + telemetry.xrayMa + " мА")
                Spacer(Modifier.height(8.dp))
                Text("Экспозиция будет добавлена отдельным этапом с двухшаговым подтверждением.")
            }
        }
    }
}

@Composable
private fun ConnectionCard(
    status: ConnectionStatus,
    crawler: DiscoveredCrawler?
) {
    val text = when (status) {
        ConnectionStatus.DISCONNECTED -> "Отключено"
        ConnectionStatus.SCANNING -> "Поиск..."
        ConnectionStatus.CONNECTING -> "Подключение..."
        ConnectionStatus.CONNECTED -> "BLE подключено"
        ConnectionStatus.RECONNECTING -> "Переподключение..."
        ConnectionStatus.LOST -> "Связь потеряна"
    }

    val color = when (status) {
        ConnectionStatus.CONNECTED -> AlitasSuccess
        ConnectionStatus.LOST, ConnectionStatus.DISCONNECTED -> AlitasDanger
        else -> AlitasBlue
    }

    Card {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text, color = color, fontWeight = FontWeight.Black)
                crawler?.let { Text(it.serialNumber, fontSize = 12.sp) }
            }
            Text(if (crawler?.isSimulator == true) "SIM" else "BLE", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Card {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text(value, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun Battery(title: String, value: BatteryTelemetry) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(value.soc.toString() + "%", color = AlitasBlue, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { value.soc / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "%.1f В · %.1f А · %d °C".format(
                    value.voltage,
                    value.current,
                    value.temperature
                )
            )
        }
    }
}

@Composable
private fun Sensor(label: String, active: Boolean) {
    Card {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(
                        if (active) AlitasDanger else AlitasSuccess,
                        RoundedCornerShape(10.dp)
                    )
            )
            Spacer(Modifier.size(10.dp))
            Text(label, Modifier.weight(1f))
            Text(
                if (active) "СРАБОТАЛ" else "Норма",
                color = if (active) AlitasDanger else AlitasSuccess,
                fontWeight = FontWeight.Bold
            )
        }
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
            .height(126.dp)
            .background(
                if (enabled) AlitasBlue else Color(0xFFB8C0CC),
                RoundedCornerShape(18.dp)
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
            Text(label, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text("УДЕРЖИВАТЬ", color = Color(0xFFD9E5FF), fontSize = 10.sp)
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
