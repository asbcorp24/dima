package ru.alitas.crawler.ui.screens.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
 * Журнал текущего состояния.
 *
 * Пока постоянный репозиторий событий не подключён, экран показывает
 * текущие диагностические отметки из доступного состояния.
 */
@Composable
fun LogsScreen(
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

            Text("Журнал событий", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(
                "Текущие события и диагностические отметки",
                color = Color(0xFF7F98BA),
                fontSize = 13.sp
            )

            LogRow(
                level = "OK",
                title = if (connectionStatus == ConnectionStatus.CONNECTED) {
                    "Кроулер подключен"
                } else {
                    "BLE не подключён"
                },
                detail = telemetry.serialNumber,
                color = if (connectionStatus == ConnectionStatus.CONNECTED) LitasSuccess else Color(0xFFFFB84D)
            )

            LogRow(
                level = "INFO",
                title = "Состояние: ${telemetry.state.name}",
                detail = "Расстояние %.1f м".format(telemetry.distanceMeters),
                color = Color(0xFF29A9FF)
            )

            LogRow(
                level = "INFO",
                title = "Ходовой аккумулятор ${telemetry.driveBattery.soc}%",
                detail = "%.1f В · %.1f А".format(
                    telemetry.driveBattery.voltage,
                    telemetry.driveBattery.current
                ),
                color = LitasSuccess
            )

            if (telemetry.sensors.water || telemetry.sensors.obstacle) {
                LogRow(
                    level = "WARN",
                    title = "Сработал датчик безопасности",
                    detail = "Проверьте воду/препятствие",
                    color = Color(0xFFFFB84D)
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Постоянное хранение, фильтры и экспорт журнала будут подключены к локальному репозиторию событий.",
                    modifier = Modifier.padding(14.dp),
                    color = Color(0xFF7189AA),
                    fontSize = 12.sp
                )
            }

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
private fun LogRow(
    level: String,
    title: String,
    detail: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(level, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(detail, color = Color(0xFF7088AA), fontSize = 12.sp)
            }
        }
    }
}

@Preview(name = "Журнал", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun LogsPreview() {
    LitasTheme {
        LogsScreen(
            telemetry = PreviewTelemetry,
            connectionStatus = ConnectionStatus.CONNECTED,
            onBack = {}
        )
    }
}
