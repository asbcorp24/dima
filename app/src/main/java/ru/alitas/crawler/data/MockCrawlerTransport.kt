package ru.alitas.crawler.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.alitas.crawler.domain.CrawlerState
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.Direction

/**
 * Симулятор до утверждения BLE-протокола.
 */
class MockCrawlerTransport : CrawlerTransport {
    private val mutableTelemetry = MutableStateFlow(CrawlerTelemetry())
    override val telemetry: StateFlow<CrawlerTelemetry> = mutableTelemetry.asStateFlow()

    override suspend fun connect() {
        mutableTelemetry.value = mutableTelemetry.value.copy(connected = true)
    }

    override suspend fun disconnect() {
        mutableTelemetry.value = mutableTelemetry.value.copy(connected = false)
    }

    override suspend fun startManualMove(direction: Direction) {
        mutableTelemetry.value = mutableTelemetry.value.copy(
            state = CrawlerState.MOVING,
            direction = direction,
            speedPercent = 28
        )
    }

    override suspend fun stopManualMove() = stop()

    override suspend fun startAutoMove(direction: Direction) {
        if (mutableTelemetry.value.state != CrawlerState.STOP) return
        mutableTelemetry.value = mutableTelemetry.value.copy(
            state = CrawlerState.MOVING,
            direction = direction,
            speedPercent = 55
        )
    }

    override suspend fun stop() {
        mutableTelemetry.value = mutableTelemetry.value.copy(
            state = CrawlerState.STOP,
            direction = null,
            speedPercent = 0
        )
    }
}
