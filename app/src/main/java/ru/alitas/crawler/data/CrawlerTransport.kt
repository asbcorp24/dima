package ru.alitas.crawler.data

import kotlinx.coroutines.flow.StateFlow
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.Direction

/**
 * Транспорт отделён от UI: mock и реальный BLE используют один контракт.
 */
interface CrawlerTransport {
    val telemetry: StateFlow<CrawlerTelemetry>
    suspend fun connect()
    suspend fun disconnect()
    suspend fun startManualMove(direction: Direction)
    suspend fun stopManualMove()
    suspend fun startAutoMove(direction: Direction)
    suspend fun stop()
}
