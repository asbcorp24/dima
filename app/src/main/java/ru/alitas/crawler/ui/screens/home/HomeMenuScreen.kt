package ru.alitas.crawler.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.ui.components.CrawlerStatusCard
import ru.alitas.crawler.ui.components.HomeBottomBar
import ru.alitas.crawler.ui.components.HomeHeader
import ru.alitas.crawler.ui.components.HomeMenuCard
import ru.alitas.crawler.ui.preview.PreviewTelemetry
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Центральное меню приложения.
 *
 * После PIN оператор попадает сюда и уже отсюда открывает
 * управление, диагностику, настройки, сервис и журнал.
 */
@Composable
fun HomeMenuScreen(
    telemetry: CrawlerTelemetry,
    connectionStatus: ConnectionStatus,
    onControlClick: () -> Unit,
    onDiagnosticsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onServiceClick: () -> Unit,
    onLogsClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF030817)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF020612),
                            Color(0xFF030A18),
                            Color(0xFF061329)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeHeader(connectionStatus)

                CrawlerStatusCard(
                    telemetry = telemetry,
                    connectionStatus = connectionStatus
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeMenuCard(
                        title = "Управление",
                        subtitle = "Движение и режимы",
                        symbol = "▶",
                        modifier = Modifier.weight(1f),
                        onClick = onControlClick
                    )
                    HomeMenuCard(
                        title = "Диагностика",
                        subtitle = "Датчики и состояние",
                        symbol = "∿",
                        modifier = Modifier.weight(1f),
                        onClick = onDiagnosticsClick
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeMenuCard(
                        title = "Настройки",
                        subtitle = "Параметры системы",
                        symbol = "⚙",
                        modifier = Modifier.weight(1f),
                        onClick = onSettingsClick
                    )
                    HomeMenuCard(
                        title = "Сервис",
                        subtitle = "Обслуживание",
                        symbol = "◆",
                        modifier = Modifier.weight(1f),
                        onClick = onServiceClick
                    )
                }

                HomeBottomBar(
                    onHome = {},
                    onControl = onControlClick,
                    onLogs = onLogsClick,
                    onMore = onMoreClick
                )
            }
        }
    }
}

@Preview(name = "Home — телефон", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun HomePhonePreview() {
    LitasTheme {
        HomeMenuScreen(
            telemetry = PreviewTelemetry,
            connectionStatus = ConnectionStatus.CONNECTED,
            onControlClick = {},
            onDiagnosticsClick = {},
            onSettingsClick = {},
            onServiceClick = {},
            onLogsClick = {},
            onMoreClick = {}
        )
    }
}
