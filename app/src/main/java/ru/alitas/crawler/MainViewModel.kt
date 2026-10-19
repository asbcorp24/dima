package ru.alitas.crawler

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.alitas.crawler.ble.CrawlerBleManager
import ru.alitas.crawler.data.CrawlerTransport
import ru.alitas.crawler.data.MockCrawlerTransport
import ru.alitas.crawler.domain.AppScreen
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.Direction
import ru.alitas.crawler.domain.DiscoveredCrawler
import ru.alitas.crawler.domain.PairingUiState
import ru.alitas.crawler.security.PinSecureStore

/**
 * Главная ViewModel приложения.
 *
 * Здесь находятся BLE, PIN, навигационное состояние и команды управления.
 * UI-экраны получают только готовое состояние и callback-функции.
 */
class MainViewModel(application: Application) : AndroidViewModel(application),
    CrawlerBleManager.Listener {

    private val transport: CrawlerTransport = MockCrawlerTransport()
    private val bleManager = CrawlerBleManager(application)
    private val pinStore = PinSecureStore(application)

    val telemetry: StateFlow<CrawlerTelemetry> = transport.telemetry

    // При запуске приложения сначала показываем Splash.
    private val mutablePairing = MutableStateFlow(
        PairingUiState(
            screen = AppScreen.SPLASH
        )
    )
    val pairing: StateFlow<PairingUiState> = mutablePairing.asStateFlow()

    private var scanCallback: ScanCallback? = null
    private var selectedBluetoothDevice: BluetoothDevice? = null
    private var failedPinAttempts = 0
    private var lockoutJob: Job? = null
    private var reconnectJob: Job? = null
    private var userRequestedDisconnect = false

    init {
        bleManager.setListener(this)
        viewModelScope.launch { transport.connect() }
        addSimulator()
    }

    /**
     * Splash завершён — переходим к поиску устройства.
     */
    fun finishSplash() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.SEARCH
        )
    }

    /** Открыть главное меню. */
    fun openHome() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.HOME
        )
    }

    /** Открыть непосредственное управление. */
    fun openDashboard() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.DASHBOARD
        )
    }

    /** Открыть экран рентгеновского узла. */
    fun openXRay() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.XRAY
        )
    }

    /** Открыть диагностику. */
    fun openDiagnostics() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.DIAGNOSTICS
        )
    }

    /** Открыть настройки. */
    fun openSettings() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.SETTINGS
        )
    }

    /** Открыть сервисный раздел. */
    fun openService() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.SERVICE
        )
    }

    /** Открыть журнал событий. */
    fun openLogs() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.LOGS
        )
    }

    /** Открыть дополнительное меню. */
    fun openMore() {
        mutablePairing.value = mutablePairing.value.copy(
            screen = AppScreen.MORE
        )
    }

    fun startScan() {
        if (!bleManager.isBluetoothEnabled()) {
            mutablePairing.value = mutablePairing.value.copy(
                statusMessage = "Bluetooth выключен",
                connectionStatus = ConnectionStatus.DISCONNECTED
            )
            return
        }

        stopScan()
        mutablePairing.value = mutablePairing.value.copy(
            devices = emptyList(),
            connectionStatus = ConnectionStatus.SCANNING,
            statusMessage = "Поиск BLE-кроулеров..."
        )
        addSimulator()

        scanCallback = bleManager.startScan { device, rssi ->
            val id = device.address
            val name = runCatching { device.name }.getOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: "BLE устройство"

            val serial = "BLE-" + id.replace(":", "").takeLast(6)
            val crawler = DiscoveredCrawler(
                id = id,
                name = name,
                serialNumber = serial,
                rssi = rssi,
                isSimulator = false
            )

            val current = mutablePairing.value.devices
                .filterNot { it.id == id } + crawler

            mutablePairing.value = mutablePairing.value.copy(
                devices = current.sortedByDescending { it.rssi }
            )
        }

        viewModelScope.launch {
            delay(10_000)
            stopScan()
        }
    }

    fun stopScan() {
        bleManager.stopScan(scanCallback)
        scanCallback = null

        if (mutablePairing.value.connectionStatus == ConnectionStatus.SCANNING) {
            mutablePairing.value = mutablePairing.value.copy(
                connectionStatus = ConnectionStatus.DISCONNECTED,
                statusMessage = "Поиск завершён"
            )
        }
    }

    fun selectCrawler(crawler: DiscoveredCrawler) {
        stopScan()
        userRequestedDisconnect = false

        mutablePairing.value = mutablePairing.value.copy(
            selectedCrawler = crawler,
            connectionStatus = ConnectionStatus.CONNECTING,
            statusMessage = "Подключение к ${crawler.name}...",
            pinError = null
        )

        if (crawler.isSimulator) {
            viewModelScope.launch {
                delay(450)
                onLogicalConnected(crawler)
            }
            return
        }

        selectedBluetoothDevice = findDevice(crawler.id)
        val device = selectedBluetoothDevice

        if (device == null) {
            mutablePairing.value = mutablePairing.value.copy(
                connectionStatus = ConnectionStatus.DISCONNECTED,
                statusMessage = "Устройство больше не найдено. Запустите поиск ещё раз."
            )
            return
        }

        bleManager.connectCodedPhy(device)
    }

    private fun findDevice(address: String): BluetoothDevice? =
        runCatching {
            getApplication<Application>()
                .getSystemService(BluetoothManager::class.java)
                .adapter
                .getRemoteDevice(address)
        }.getOrNull()

    private fun onLogicalConnected(crawler: DiscoveredCrawler) {
        val hasPin = pinStore.hasPin(crawler.id)

        mutablePairing.value = mutablePairing.value.copy(
            connectionStatus = ConnectionStatus.CONNECTED,
            screen = if (hasPin) AppScreen.PIN_UNLOCK else AppScreen.PIN_SETUP,
            statusMessage = if (hasPin) {
                "Введите PIN кроулера"
            } else {
                "Первичное подключение: задайте 5-значный PIN"
            }
        )
    }

    fun createPin(pin: String, repeatPin: String) {
        val crawler = mutablePairing.value.selectedCrawler ?: return

        when {
            !pin.matches(Regex("\\d{5}")) -> {
                setPinError("PIN должен состоять ровно из 5 цифр")
            }

            pin != repeatPin -> {
                setPinError("PIN-коды не совпадают")
            }

            else -> {
                pinStore.savePin(crawler.id, pin)
                failedPinAttempts = 0

                // После успешного создания PIN открываем новое Home-меню.
                mutablePairing.value = mutablePairing.value.copy(
                    screen = AppScreen.HOME,
                    pinError = null,
                    statusMessage = "PIN сохранён в защищённом хранилище Android"
                )
            }
        }
    }

    fun unlock(pin: String) {
        val crawler = mutablePairing.value.selectedCrawler ?: return
        if (mutablePairing.value.isLockedOut) return

        if (pinStore.verifyPin(crawler.id, pin)) {
            failedPinAttempts = 0

            // После успешного PIN открываем Home, а не Dashboard.
            mutablePairing.value = mutablePairing.value.copy(
                screen = AppScreen.HOME,
                pinError = null,
                statusMessage = "Доступ разрешён"
            )
            return
        }

        failedPinAttempts += 1

        if (failedPinAttempts >= 3) {
            startFiveMinuteLockout()
        } else {
            setPinError("Неверный PIN. Осталось попыток: ${3 - failedPinAttempts}")
        }
    }

    private fun startFiveMinuteLockout() {
        failedPinAttempts = 0
        lockoutJob?.cancel()

        lockoutJob = viewModelScope.launch {
            for (seconds in 300 downTo 1) {
                mutablePairing.value = mutablePairing.value.copy(
                    isLockedOut = true,
                    lockoutSecondsRemaining = seconds,
                    pinError = "Слишком много попыток. Блокировка 5 минут."
                )
                delay(1_000)
            }

            mutablePairing.value = mutablePairing.value.copy(
                isLockedOut = false,
                lockoutSecondsRemaining = 0,
                pinError = null
            )
        }
    }

    fun backToSearch() {
        userRequestedDisconnect = true
        reconnectJob?.cancel()
        bleManager.disconnect()
        selectedBluetoothDevice = null

        // PairingUiState() по умолчанию возвращает SEARCH,
        // поэтому Splash при ручном отключении не повторяется.
        mutablePairing.value = PairingUiState()
        addSimulator()
    }

    fun simulateSignalLoss() {
        val crawler = mutablePairing.value.selectedCrawler ?: return
        if (!crawler.isSimulator) return

        mutablePairing.value = mutablePairing.value.copy(
            connectionStatus = ConnectionStatus.LOST,
            statusMessage = "Сигнал потерян. Автопереподключение..."
        )

        reconnectJob?.cancel()
        reconnectJob = viewModelScope.launch {
            delay(2_000)

            mutablePairing.value = mutablePairing.value.copy(
                connectionStatus = ConnectionStatus.RECONNECTING,
                statusMessage = "Переподключение..."
            )

            delay(1_000)

            mutablePairing.value = mutablePairing.value.copy(
                connectionStatus = ConnectionStatus.CONNECTED,
                statusMessage = "Связь восстановлена автоматически"
            )
        }
    }

    override fun onConnected(device: BluetoothDevice) {
        reconnectJob?.cancel()
        val crawler = mutablePairing.value.selectedCrawler ?: return
        onLogicalConnected(crawler)
    }

    override fun onDisconnected(device: BluetoothDevice) {
        if (userRequestedDisconnect) return

        mutablePairing.value = mutablePairing.value.copy(
            connectionStatus = ConnectionStatus.LOST,
            statusMessage = "BLE-связь потеряна. Переподключение..."
        )
        scheduleReconnect()
    }

    override fun onConnectionError(device: BluetoothDevice?) {
        if (userRequestedDisconnect) return

        mutablePairing.value = mutablePairing.value.copy(
            connectionStatus = ConnectionStatus.LOST,
            statusMessage = "Ошибка BLE. Выполняется переподключение..."
        )
        scheduleReconnect()
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()

        reconnectJob = viewModelScope.launch {
            repeat(5) { attempt ->
                delay(2_000)

                mutablePairing.value = mutablePairing.value.copy(
                    connectionStatus = ConnectionStatus.RECONNECTING,
                    statusMessage = "Переподключение ${attempt + 1}/5..."
                )

                bleManager.reconnect()
                delay(1_500)

                if (mutablePairing.value.connectionStatus == ConnectionStatus.CONNECTED) {
                    return@launch
                }
            }

            mutablePairing.value = mutablePairing.value.copy(
                connectionStatus = ConnectionStatus.DISCONNECTED,
                statusMessage = "Не удалось восстановить BLE-связь"
            )
        }
    }

    private fun setPinError(message: String) {
        mutablePairing.value = mutablePairing.value.copy(
            pinError = message
        )
    }

    /**
     * Виртуальный кроулер для разработки интерфейса без оборудования.
     */
    private fun addSimulator() {
        val simulator = DiscoveredCrawler(
            id = "SIM-LITS-0001",
            name = "ЛИТАС Simulator",
            serialNumber = "LITS-CR-0001",
            rssi = -42,
            isSimulator = true
        )

        mutablePairing.value = mutablePairing.value.copy(
            devices = mutablePairing.value.devices
                .filterNot { it.id == simulator.id } + simulator
        )
    }

    fun startManual(direction: Direction) =
        viewModelScope.launch {
            transport.startManualMove(direction)
        }

    fun stopManual() =
        viewModelScope.launch {
            transport.stopManualMove()
        }

    fun startAuto(direction: Direction) =
        viewModelScope.launch {
            transport.startAutoMove(direction)
        }

    fun stop() =
        viewModelScope.launch {
            transport.stop()
        }

    override fun onCleared() {
        bleManager.setListener(null)
        bleManager.disconnect()
        super.onCleared()
    }
}
