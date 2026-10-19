package ru.alitas.crawler.domain

/**
 * Текущее состояние BLE-соединения.
 */
enum class ConnectionStatus {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    LOST
}

/**
 * Устройство, найденное во время BLE-сканирования.
 */
data class DiscoveredCrawler(
    val id: String,
    val name: String,
    val serialNumber: String,
    val rssi: Int,
    val isSimulator: Boolean = false
)

/**
 * Все основные страницы приложения.
 *
 * При добавлении нового значения сюда необходимо добавить
 * соответствующую ветку в when(pairing.screen) в MainActivity.
 */
enum class AppScreen {
    SPLASH,
    SEARCH,
    PIN_SETUP,
    PIN_UNLOCK,
    HOME,
    DASHBOARD,
    DIAGNOSTICS,
    SETTINGS,
    SERVICE
}

/**
 * Состояние интерфейса подключения.
 */
data class PairingUiState(
    val screen: AppScreen = AppScreen.SEARCH,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val devices: List<DiscoveredCrawler> = emptyList(),
    val selectedCrawler: DiscoveredCrawler? = null,
    val isLockedOut: Boolean = false,
    val lockoutSecondsRemaining: Int = 0,
    val pinError: String? = null,
    val statusMessage: String? = null
)
