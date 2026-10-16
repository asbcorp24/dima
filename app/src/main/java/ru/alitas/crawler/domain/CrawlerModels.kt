package ru.alitas.crawler.domain

enum class CrawlerState { WAITING, STOP, MOVING, EXPOSURE, CALIBRATION }
enum class Direction { FORWARD, BACKWARD }
enum class XRayState { EXPOSURE_FORBIDDEN, ERROR, TRAINING_REQUIRED, READY }

data class BatteryTelemetry(
    val voltage: Double,
    val current: Double,
    val temperature: Int,
    val soc: Int
)

data class SensorState(
    val pipeStart: Boolean = false,
    val pipeEnd: Boolean = false,
    val water: Boolean = false,
    val obstacle: Boolean = false
)

data class CrawlerTelemetry(
    val name: String = "Crawler 01",
    val serialNumber: String = "ALTS-CR-0001",
    val state: CrawlerState = CrawlerState.STOP,
    val direction: Direction? = null,
    val speedPercent: Int = 0,
    val distanceMeters: Double = 128.4,
    val rollDeg: Double = 1.8,
    val pitchDeg: Double = -0.7,
    val brakePercent: Int = 72,
    val bleRssiDbm: Int = -52,
    val driveBattery: BatteryTelemetry = BatteryTelemetry(51.8, 3.2, 29, 84),
    val xrayBattery: BatteryTelemetry = BatteryTelemetry(48.6, 1.1, 31, 67),
    val sensors: SensorState = SensorState(),
    val xrayState: XRayState = XRayState.READY,
    val xrayKv: Int = 180,
    val xrayMa: Double = 4.5,
    val connected: Boolean = true
)
