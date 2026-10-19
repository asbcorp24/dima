package ru.alitas.crawler.ui.screens.diagnostics

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.ui.components.HomeHeader
import ru.alitas.crawler.ui.preview.PreviewTelemetry
import ru.alitas.crawler.ui.theme.LitasSuccess
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Экран диагностики.
 *
 * Показывает только уже доступную локальную телеметрию.
 * Отчёты/исторические графики можно подключить позже к реальному журналу.
 */
@Composable
fun DiagnosticsScreen(
    telemetry: CrawlerTelemetry,
    connectionStatus: ConnectionStatus,
    onBack: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = Color(0xFF030817)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF020612), Color(0xFF061329))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HomeHeader(connectionStatus)

            Text("Диагностика", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(
                "Состояние контроллера, датчиков и питания",
                color = Color(0xFF7F98BA),
                fontSize = 13.sp
            )

            StatusCard(
                title = "Состояние системы",
                value = if (telemetry.connected) "Нормальная работа" else "Связь отсутствует",
                good = telemetry.connected
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DiagnosticMetric("Температура", "${telemetry.driveBattery.temperature} °C", Modifier.weight(1f))
                DiagnosticMetric("Напряжение", "%.1f В".format(telemetry.driveBattery.voltage), Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DiagnosticMetric("Ток", "%.1f А".format(telemetry.driveBattery.current), Modifier.weight(1f))
                DiagnosticMetric("RSSI", "${telemetry.bleRssiDbm} dBm", Modifier.weight(1f))
            }

            Text("ДАТЧИКИ", color = Color(0xFF6F89AD), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            SensorLine("Начало трубы", telemetry.sensors.pipeStart)
            SensorLine("Конец трубы", telemetry.sensors.pipeEnd)
            SensorLine("Вода", telemetry.sensors.water)
            SensorLine("Препятствие", telemetry.sensors.obstacle)

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("← Главное меню")
            }
        }
    }
}

@Composable
private fun StatusCard(title: String, value: String, good: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1728)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Text(title, color = Color(0xFF8299B9), fontSize = 12.sp)
            Text(
                value,
                color = if (good) LitasSuccess else Color(0xFFFF6B6B),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun DiagnosticMetric(title: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1728)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(title, color = Color(0xFF738AA9), fontSize = 11.sp)
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SensorLine(label: String, active: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(
                if (active) "СРАБОТАЛ" else "OK",
                color = if (active) Color(0xFFFF5E62) else LitasSuccess,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Preview(name = "Диагностика", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun DiagnosticsPreview() {
    LitasTheme {
        DiagnosticsScreen(
            telemetry = PreviewTelemetry,
            connectionStatus = ConnectionStatus.CONNECTED,
            onBack = {}
        )
    }
}
