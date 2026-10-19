package ru.alitas.crawler.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.AppScreen
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.DiscoveredCrawler
import ru.alitas.crawler.domain.PairingUiState
import ru.alitas.crawler.ui.components.BleStatusBanner
import ru.alitas.crawler.ui.components.BrandHeader
import ru.alitas.crawler.ui.preview.PreviewRealCrawler
import ru.alitas.crawler.ui.preview.PreviewSimulatorCrawler
import ru.alitas.crawler.ui.theme.LitasBackground
import ru.alitas.crawler.ui.theme.LitasBlue
import ru.alitas.crawler.ui.theme.LitasBlueSoft
import ru.alitas.crawler.ui.theme.LitasDanger
import ru.alitas.crawler.ui.theme.LitasMuted
import ru.alitas.crawler.ui.theme.LitasSuccess
import ru.alitas.crawler.ui.theme.LitasTheme
import ru.alitas.crawler.ui.theme.LitasWarning

/**
 * Страница поиска и выбора BLE-кроулера.
 *
 * Экран не работает с Bluetooth API напрямую: состояние и действия приходят параметрами.
 * Поэтому страницу можно независимо редактировать, тестировать и смотреть через Preview.
 */
@Composable
fun SearchCrawlerScreen(
    state: PairingUiState,
    onScan: () -> Unit,
    onStopScan: () -> Unit,
    onSelect: (DiscoveredCrawler) -> Unit
) {
    Surface(color = LitasBackground, modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            BrandHeader("Подключение", "Найдите и выберите BLE-кроулер")

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BleStatusBanner(state)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onScan,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Найти устройства") }

                    OutlinedButton(
                        onClick = onStopScan,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Остановить") }
                }

                Text(
                    "ДОСТУПНЫЕ УСТРОЙСТВА",
                    color = LitasMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                if (state.devices.isEmpty() && state.connectionStatus != ConnectionStatus.SCANNING) {
                    EmptyDevices()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.devices, key = { it.id }) { device ->
                            CrawlerDeviceCard(device = device, onClick = { onSelect(device) })
                        }
                    }
                }
            }
        }
    }
}

/** Пустое состояние списка устройств. */
@Composable
private fun EmptyDevices() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("BLE", color = LitasBlue, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("Устройства пока не найдены", fontWeight = FontWeight.Bold)
            Text(
                "Нажмите «Найти устройства» и убедитесь, что Bluetooth включён.",
                color = LitasMuted
            )
        }
    }
}

/** Карточка одного найденного устройства. */
@Composable
private fun CrawlerDeviceCard(device: DiscoveredCrawler, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (device.isSimulator) Color(0xFFEAF8F2) else LitasBlueSoft,
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (device.isSimulator) "SIM" else "BLE",
                    color = if (device.isSimulator) LitasSuccess else LitasBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(device.name, fontWeight = FontWeight.Black, fontSize = 17.sp)
                Text(device.serialNumber, color = LitasMuted, fontSize = 13.sp)
                if (device.isSimulator) {
                    Text("Режим симуляции", color = LitasSuccess, fontWeight = FontWeight.Bold)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${device.rssi} dBm",
                    fontWeight = FontWeight.Bold,
                    color = signalColor(device.rssi)
                )
                Text("Подключить ›", color = LitasBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

private fun signalColor(rssi: Int): Color = when {
    rssi >= -60 -> LitasSuccess
    rssi >= -80 -> LitasWarning
    else -> LitasDanger
}

@Preview(name = "Поиск BLE — телефон", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SearchCrawlerPreview() {
    LitasTheme {
        SearchCrawlerScreen(
            state = PairingUiState(
                screen = AppScreen.SEARCH,
                connectionStatus = ConnectionStatus.DISCONNECTED,
                devices = listOf(PreviewSimulatorCrawler, PreviewRealCrawler),
                statusMessage = "Bluetooth готов"
            ),
            onScan = {},
            onStopScan = {},
            onSelect = {}
        )
    }
}

@Preview(name = "Поиск BLE — сканирование", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SearchCrawlerScanningPreview() {
    LitasTheme {
        SearchCrawlerScreen(
            state = PairingUiState(
                screen = AppScreen.SEARCH,
                connectionStatus = ConnectionStatus.SCANNING,
                devices = listOf(PreviewSimulatorCrawler),
                statusMessage = "Поиск BLE-кроулеров..."
            ),
            onScan = {},
            onStopScan = {},
            onSelect = {}
        )
    }
}
