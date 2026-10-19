package ru.alitas.crawler.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.alitas.crawler.domain.AppScreen
import ru.alitas.crawler.domain.BatteryTelemetry
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerState
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.DiscoveredCrawler
import ru.alitas.crawler.domain.PairingUiState
import ru.alitas.crawler.domain.SensorState

private val PreviewCrawler = DiscoveredCrawler(
    id = "SIM-LITS-0001",
    name = "ЛИТАС Simulator",
    serialNumber = "LITS-CR-0001",
    rssi = -46,
    isSimulator = true
)

private val PreviewRealCrawler = DiscoveredCrawler(
    id = "00:11:22:33:44:55",
    name = "LITAS Crawler",
    serialNumber = "LITS-CR-0021",
    rssi = -67,
    isSimulator = false
)

private val PreviewTelemetry = CrawlerTelemetry(
    name = "ЛИТАС Кроулер",
    serialNumber = "LITS-CR-0001",
    state = CrawlerState.STOP,
    distanceMeters = 128.4,
    rollDeg = 1.8,
    pitchDeg = -0.7,
    brakePercent = 72,
    bleRssiDbm = -46,
    driveBattery = BatteryTelemetry(
        voltage = 51.8,
        current = 3.2,
        temperature = 29,
        soc = 84
    ),
    xrayBattery = BatteryTelemetry(
        voltage = 48.6,
        current = 1.1,
        temperature = 31,
        soc = 67
    ),
    sensors = SensorState(
        pipeStart = false,
        pipeEnd = false,
        water = false,
        obstacle = false
    ),
    xrayKv = 180,
    xrayMa = 4.5,
    connected = true
)

@Preview(
    name = "01 — Поиск BLE",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun SearchCrawlerPhonePreview() {
    LitasTheme {
        SearchCrawlerScreen(
            state = PairingUiState(
                screen = AppScreen.SEARCH,
                connectionStatus = ConnectionStatus.DISCONNECTED,
                devices = listOf(PreviewCrawler, PreviewRealCrawler),
                statusMessage = "Bluetooth готов"
            ),
            onScan = {},
            onStopScan = {},
            onSelect = {}
        )
    }
}

@Preview(
    name = "02 — Поиск BLE, сканирование",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun SearchCrawlerScanningPreview() {
    LitasTheme {
        SearchCrawlerScreen(
            state = PairingUiState(
                screen = AppScreen.SEARCH,
                connectionStatus = ConnectionStatus.SCANNING,
                devices = listOf(PreviewCrawler),
                statusMessage = "Поиск BLE-кроулеров..."
            ),
            onScan = {},
            onStopScan = {},
            onSelect = {}
        )
    }
}

@Preview(
    name = "03 — Создание PIN",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun PinSetupPreview() {
    LitasTheme {
        PinSetupScreen(
            state = PairingUiState(
                screen = AppScreen.PIN_SETUP,
                connectionStatus = ConnectionStatus.CONNECTED,
                selectedCrawler = PreviewCrawler,
                statusMessage = "Первичное подключение"
            ),
            onSave = { _, _ -> },
            onBack = {}
        )
    }
}

@Preview(
    name = "04 — Ввод PIN",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun PinUnlockPreview() {
    LitasTheme {
        PinUnlockScreen(
            state = PairingUiState(
                screen = AppScreen.PIN_UNLOCK,
                connectionStatus = ConnectionStatus.CONNECTED,
                selectedCrawler = PreviewCrawler,
                statusMessage = "Введите PIN кроулера"
            ),
            onUnlock = {},
            onBack = {}
        )
    }
}

@Preview(
    name = "05 — Главный экран",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun DashboardPhonePreview() {
    LitasTheme {
        DashboardScreen(
            telemetry = PreviewTelemetry,
            connectionStatus = ConnectionStatus.CONNECTED,
            selectedCrawler = PreviewCrawler,
            controlsEnabled = true,
            onManualStart = {},
            onManualStop = {},
            onAutoStart = {},
            onStop = {},
            onSimulateLoss = {},
            onChooseCrawler = {}
        )
    }
}

@Preview(
    name = "06 — Потеря связи",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun DashboardConnectionLostPreview() {
    LitasTheme {
        DashboardScreen(
            telemetry = PreviewTelemetry.copy(
                state = CrawlerState.WAITING,
                connected = false
            ),
            connectionStatus = ConnectionStatus.LOST,
            selectedCrawler = PreviewCrawler.copy(rssi = -98),
            controlsEnabled = true,
            onManualStart = {},
            onManualStop = {},
            onAutoStart = {},
            onStop = {},
            onSimulateLoss = {},
            onChooseCrawler = {}
        )
    }
}

@Preview(
    name = "07 — Планшет landscape",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun DashboardTabletPreview() {
    LitasTheme {
        DashboardScreen(
            telemetry = PreviewTelemetry.copy(
                state = CrawlerState.MOVING,
                distanceMeters = 246.7,
                rollDeg = 2.1,
                pitchDeg = -1.2
            ),
            connectionStatus = ConnectionStatus.CONNECTED,
            selectedCrawler = PreviewCrawler,
            controlsEnabled = true,
            onManualStart = {},
            onManualStop = {},
            onAutoStart = {},
            onStop = {},
            onSimulateLoss = {},
            onChooseCrawler = {}
        )
    }
}
