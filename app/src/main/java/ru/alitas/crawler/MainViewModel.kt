package ru.alitas.crawler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.alitas.crawler.data.CrawlerTransport
import ru.alitas.crawler.data.MockCrawlerTransport
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.Direction

class MainViewModel(
    private val transport: CrawlerTransport = MockCrawlerTransport()
) : ViewModel() {

    val telemetry: StateFlow<CrawlerTelemetry> = transport.telemetry

    init {
        viewModelScope.launch { transport.connect() }
    }

    fun startManual(direction: Direction) =
        viewModelScope.launch { transport.startManualMove(direction) }

    fun stopManual() =
        viewModelScope.launch { transport.stopManualMove() }

    fun startAuto(direction: Direction) =
        viewModelScope.launch { transport.startAutoMove(direction) }

    fun stop() =
        viewModelScope.launch { transport.stop() }
}
