package ru.alitas.crawler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.ui.theme.LitasBlue
import ru.alitas.crawler.ui.theme.LitasCyan
import ru.alitas.crawler.ui.theme.LitasSuccess

@Composable
fun HomeHeader(connectionStatus: ConnectionStatus) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LitasLogo(modifier = Modifier.size(38.dp))
            Column {
                Text(
                    "ЛИТАС",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Управление кроулером",
                    color = Color(0xFF7188A8),
                    fontSize = 11.sp
                )
            }
        }

        val (text, color) = when (connectionStatus) {
            ConnectionStatus.CONNECTED -> "● BLE" to LitasSuccess
            ConnectionStatus.RECONNECTING,
            ConnectionStatus.CONNECTING,
            ConnectionStatus.SCANNING -> "● Подключение" to Color(0xFFFFB84D)
            else -> "● Нет связи" to Color(0xFFFF6B6B)
        }

        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CrawlerStatusCard(
    telemetry: CrawlerTelemetry,
    connectionStatus: ConnectionStatus
) {
    val connected = connectionStatus == ConnectionStatus.CONNECTED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF142643), Color(0xFF0A1426))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Кроулер", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    Text(telemetry.serialNumber, color = Color(0xFF819BC0), fontSize = 13.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        if (connected) "Подключен" else "Нет связи",
                        color = if (connected) LitasSuccess else Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("${telemetry.bleRssiDbm} dBm", color = Color(0xFF819BC0), fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ходовой аккумулятор", color = Color(0xFFB6C7DF), fontSize = 13.sp)
                Text(
                    "${telemetry.driveBattery.soc}%",
                    color = batteryColor(telemetry.driveBattery.soc),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { telemetry.driveBattery.soc.coerceIn(0, 100) / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = batteryColor(telemetry.driveBattery.soc),
                trackColor = Color(0xFF263950)
            )

            Spacer(Modifier.height(20.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryValue("Расстояние", "%.1f м".format(telemetry.distanceMeters))
                TelemetryValue("RSSI", "${telemetry.bleRssiDbm} dBm")
                TelemetryValue("Температура", "${telemetry.driveBattery.temperature} °C")
            }
        }
    }
}

@Composable
private fun TelemetryValue(label: String, value: String) {
    Column {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(3.dp))
        Text(label, color = Color(0xFF718BAE), fontSize = 10.sp)
    }
}

@Composable
fun HomeMenuCard(
    title: String,
    subtitle: String,
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(145.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1526))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF104582), Color(0xFF09274E))),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(symbol, color = LitasCyan, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }

            Column {
                Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = Color(0xFF7088AA), fontSize = 11.sp)
            }
        }
    }
}

/**
 * Нижняя навигация главного экрана.
 * В макете нет камеры: по ТЗ приложение не содержит видеопоток.
 */
@Composable
fun HomeBottomBar(
    onHome: () -> Unit,
    onControl: () -> Unit,
    onLogs: () -> Unit,
    onMore: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF080F1D))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationItem("Главная", "◆", true, onHome)
            BottomNavigationItem("Управление", "▶", false, onControl)
            BottomNavigationItem("Журнал", "▤", false, onLogs)
            BottomNavigationItem("Ещё", "•••", false, onMore)
        }
    }
}

@Composable
private fun BottomNavigationItem(
    title: String,
    symbol: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            symbol,
            color = if (active) LitasBlue else Color(0xFF687D9A),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            title,
            color = if (active) LitasBlue else Color(0xFF71829B),
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun batteryColor(soc: Int): Color = when {
    soc >= 50 -> LitasSuccess
    soc >= 20 -> Color(0xFFFFB84D)
    else -> Color(0xFFFF5E62)
}
