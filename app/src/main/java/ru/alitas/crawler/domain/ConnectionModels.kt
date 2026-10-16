package ru.alitas.crawler.domain

enum class ConnectionStatus {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    LOST
}

data class DiscoveredCrawler(
    val id: String,
    val name: String,
    val serialNumber: String,
    val rssi: Int,
    val isSimulator: Boolean = false
)

enum class AppScreen {
    SEARCH,
    PIN_SETUP,
    PIN_UNLOCK,
    DASHBOARD
}

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
