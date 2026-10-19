package ru.alitas.crawler.ui.components

import androidx.compose.foundation.background
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

/**
 * Верхняя строка главного меню.
 */
@Composable
fun HomeHeader(
    connectionStatus: ConnectionStatus
) {
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
                    text = "ЛИТАС",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Управление кроулером",
                    color = Color(0xFF7188A8),
                    fontSize = 11.sp
                )
            }
        }

        val connected = connectionStatus == ConnectionStatus.CONNECTED
        val reconnecting = connectionStatus == ConnectionStatus.RECONNECTING

        val text = when {
            connected -> "● BLE"
            reconnecting -> "● Подключение"
            else -> "● Нет связи"
        }

        val color = when {
            connected -> LitasSuccess
            reconnecting -> Color(0xFFFFB84D)
            else -> Color(0xFFFF6B6B)
        }

        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Большая карточка текущего кроулера.
 */
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
                        colors = listOf(
                            Color(0xFF142643),
                            Color(0xFF0A1426)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Кроулер",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = telemetry.serialNumber,
                        color = Color(0xFF819BC0),
                        fontSize = 13.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (connected) "Подключен" else "Нет связи",
                        color = if (connected) LitasSuccess else Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${telemetry.bleRssiDbm} dBm",
                        color = Color(0xFF819BC0),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ходовой аккумулятор",
                    color = Color(0xFFB6C7DF),
                    fontSize = 13.sp
                )

                Text(
                    text = "${telemetry.driveBattery.soc}%",
                    color = batteryColor(telemetry.driveBattery.soc),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = {
                    telemetry.driveBattery.soc.coerceIn(0, 100) / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = batteryColor(telemetry.driveBattery.soc),
                trackColor = Color(0xFF263950)
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryValue(
                    label = "Расстояние",
                    value = "%.1f м".format(telemetry.distanceMeters)
                )
                TelemetryValue(
                    label = "RSSI",
                    value = "${telemetry.bleRssiDbm} dBm"
                )
                TelemetryValue(
                    label = "Температура",
                    value = "${telemetry.driveBattery.temperature} °C"
                )
            }
        }
    }
}

@Composable
private fun TelemetryValue(
    label: String,
    value: String
) {
    Column {
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            color = Color(0xFF718BAE),
            fontSize = 10.sp
        )
    }
}

/**
 * Универсальная плитка главного меню.
 */
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C1526)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF104582),
                                Color(0xFF09274E)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    color = LitasCyan,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF7088AA),
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Нижняя панель навигации главного меню.
 */
@Composable
fun HomeBottomBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF080F1D)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationItem("Главная", "◆", true)
            BottomNavigationItem("Управление", "▶")
            BottomNavigationItem("Журнал", "▤")
            BottomNavigationItem("Ещё", "•••")
        }
    }
}

@Composable
private fun BottomNavigationItem(
    title: String,
    symbol: String,
    active: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = symbol,
            color = if (active) LitasBlue else Color(0xFF687D9A),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            color = if (active) LitasBlue else Color(0xFF71829B),
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun batteryColor(
    soc: Int
): Color = when {
    soc >= 50 -> LitasSuccess
    soc >= 20 -> Color(0xFFFFB84D)
    else -> Color(0xFFFF5E62)
}
