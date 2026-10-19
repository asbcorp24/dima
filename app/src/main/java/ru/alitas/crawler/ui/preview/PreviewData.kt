package ru.alitas.crawler.ui.preview

import ru.alitas.crawler.domain.BatteryTelemetry
import ru.alitas.crawler.domain.CrawlerState
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.DiscoveredCrawler
import ru.alitas.crawler.domain.SensorState

/**
 * Тестовые данные только для Compose Preview.
 *
 * Важно: Preview не должен подключаться к BLE, ViewModel или реальному оборудованию.
 * Все визуальные состояния экранов строятся на этих локальных моделях.
 */
val PreviewSimulatorCrawler = DiscoveredCrawler(
    id = "SIM-LITS-0001",
    name = "ЛИТАС Simulator",
    serialNumber = "LITS-CR-0001",
    rssi = -46,
    isSimulator = true
)

val PreviewRealCrawler = DiscoveredCrawler(
    id = "00:11:22:33:44:55",
    name = "LITAS Crawler",
    serialNumber = "LITS-CR-0021",
    rssi = -67,
    isSimulator = false
)

val PreviewTelemetry = CrawlerTelemetry(
    name = "ЛИТАС Кроулер",
    serialNumber = "LITS-CR-0001",
    state = CrawlerState.STOP,
    distanceMeters = 128.4,
    rollDeg = 1.8,
    pitchDeg = -0.7,
    brakePercent = 72,
    bleRssiDbm = -46,
    driveBattery = BatteryTelemetry(51.8, 3.2, 29, 84),
    xrayBattery = BatteryTelemetry(48.6, 1.1, 31, 67),
    sensors = SensorState(),
    xrayKv = 180,
    xrayMa = 4.5,
    connected = true
)
