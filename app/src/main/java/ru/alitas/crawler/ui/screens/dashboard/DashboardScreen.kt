package ru.alitas.crawler.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.CrawlerState
import ru.alitas.crawler.domain.CrawlerTelemetry
import ru.alitas.crawler.domain.Direction
import ru.alitas.crawler.domain.DiscoveredCrawler
import ru.alitas.crawler.ui.components.HomeBottomBar
import ru.alitas.crawler.ui.preview.PreviewSimulatorCrawler
import ru.alitas.crawler.ui.preview.PreviewTelemetry
import ru.alitas.crawler.ui.theme.LitasDanger
import ru.alitas.crawler.ui.theme.LitasMuted
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Основной экран управления кроулером.
 *
 * Экран построен так, чтобы оператор сразу видел:
 * - состояние связи;
 * - состояние кроулера;
 * - основную телеметрию;
 * - заряд обоих аккумуляторов;
 * - датчики безопасности;
 * - органы ручного и автоматического управления;
 * - состояние рентгеновского узла.
 *
 * Нижняя навигация закреплена отдельно от прокручиваемого содержимого.
 */
@Composable
fun DashboardScreen(
    telemetry: CrawlerTelemetry,
    connectionStatus: ConnectionStatus,
    selectedCrawler: DiscoveredCrawler?,
    controlsEnabled: Boolean,
    onManualStart: (Direction) -> Unit,
    onManualStop: () -> Unit,
    onAutoStart: (Direction) -> Unit,
    onStop: () -> Unit,
    onSimulateLoss: () -> Unit,
    onChooseCrawler: () -> Unit,
    onHome: () -> Unit,
    onLogs: () -> Unit,
    onMore: () -> Unit,
    onXRay: () -> Unit
) {
    val manualAllowed = controlsEnabled &&
        (telemetry.state == CrawlerState.WAITING || telemetry.state == CrawlerState.STOP)

    val autoAllowed = controlsEnabled && telemetry.state == CrawlerState.STOP

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF030817)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF020612),
                            Color(0xFF041027),
                            Color(0xFF07152C)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DashboardHeroHeader(
                    telemetry = telemetry,
                    connectionStatus = connectionStatus,
                    selectedCrawler = selectedCrawler
                )

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!controlsEnabled) {
                        WarningCard(
                            "Реальное BLE-соединение установлено",
                            "Управляющие команды отключены до согласования UUID и протокола контроллера."
                        )
                    }

                    SectionTitle("ТЕЛЕМЕТРИЯ")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            "Расстояние",
                            "%.1f".format(telemetry.distanceMeters),
                            "м",
                            Modifier.weight(1f)
                        )

                        MetricCard(
                            "Крен",
                            "%.1f".format(telemetry.rollDeg),
                            "°",
                            Modifier.weight(1f)
                        )

                        MetricCard(
                            "Тангаж",
                            "%.1f".format(telemetry.pitchDeg),
                            "°",
                            Modifier.weight(1f)
                        )
                    }

                    MetricCard(
                        title = "Тормоз",
                        value = telemetry.brakePercent.toString(),
                        unit = "%",
                        modifier = Modifier.fillMaxWidth()
                    )

                    SectionTitle("ПИТАНИЕ")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CompactBatteryCard(
                            title = "Ходовой",
                            value = telemetry.driveBattery,
                            modifier = Modifier.weight(1f)
                        )

                        CompactBatteryCard(
                            title = "Рентген",
                            value = telemetry.xrayBattery,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    SectionTitle("ДАТЧИКИ БЕЗОПАСНОСТИ")
                    SensorGrid(telemetry)

                    SectionTitle("УПРАВЛЕНИЕ")

                    ControlPanel(
                        manualAllowed = manualAllowed,
                        autoAllowed = autoAllowed,
                        controlsEnabled = controlsEnabled,
                        onManualStart = onManualStart,
                        onManualStop = onManualStop,
                        onAutoStart = onAutoStart,
                        onStop = onStop
                    )

                    SectionTitle("РЕНТГЕНОВСКИЙ АППАРАТ")
                    XRayCard(
                        telemetry = telemetry,
                        onOpen = onXRay
                    )

                    if (selectedCrawler?.isSimulator == true) {
                        OutlinedButton(
                            onClick = onSimulateLoss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Симулировать потерю BLE")
                        }
                    }

                    OutlinedButton(
                        onClick = onChooseCrawler,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Отключиться и выбрать кроулер")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                HomeBottomBar(
                    active = "control",
                    onHome = onHome,
                    onControl = {},
                    onLogs = onLogs,
                    onMore = onMore
                )
            }
        }
    }
}

/**
 * Панель ручного и автоматического движения.
 *
 * Ручные кнопки работают только во время удержания.
 */
@Composable
private fun ControlPanel(
    manualAllowed: Boolean,
    autoAllowed: Boolean,
    controlsEnabled: Boolean,
    onManualStart: (Direction) -> Unit,
    onManualStop: () -> Unit,
    onAutoStart: (Direction) -> Unit,
    onStop: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B1524)
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Ручное движение",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )

            Text(
                "Движение выполняется только пока кнопка удерживается.",
                color = LitasMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HoldButton(
                    symbol = "↑",
                    label = "Вперёд",
                    enabled = manualAllowed,
                    modifier = Modifier.weight(1f),
                    onPress = {
                        onManualStart(Direction.FORWARD)
                    },
                    onRelease = onManualStop
                )

                HoldButton(
                    symbol = "↓",
                    label = "Назад",
                    enabled = manualAllowed,
                    modifier = Modifier.weight(1f),
                    onPress = {
                        onManualStart(Direction.BACKWARD)
                    },
                    onRelease = onManualStop
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onAutoStart(Direction.FORWARD)
                    },
                    enabled = autoAllowed,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Авто вперёд")
                }

                OutlinedButton(
                    onClick = {
                        onAutoStart(Direction.BACKWARD)
                    },
                    enabled = autoAllowed,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Авто назад")
                }
            }

            Button(
                onClick = onStop,
                enabled = controlsEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LitasDanger
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Text(
                    "STOP",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Preview(
    name = "Управление — телефон",
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
            selectedCrawler = PreviewSimulatorCrawler,
            controlsEnabled = true,
            onManualStart = {},
            onManualStop = {},
            onAutoStart = {},
            onStop = {},
            onSimulateLoss = {},
            onChooseCrawler = {},
            onHome = {},
            onLogs = {},
            onMore = {},
            onXRay = {}
        )
    }
}

@Preview(
    name = "Управление — планшет",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun DashboardTabletPreview() {
    LitasTheme {
        DashboardScreen(
            telemetry = PreviewTelemetry.copy(
                state = CrawlerState.MOVING
            ),
            connectionStatus = ConnectionStatus.CONNECTED,
            selectedCrawler = PreviewSimulatorCrawler,
            controlsEnabled = true,
            onManualStart = {},
            onManualStop = {},
            onAutoStart = {},
            onStop = {},
            onSimulateLoss = {},
            onChooseCrawler = {},
            onHome = {},
            onLogs = {},
            onMore = {},
            onXRay = {}
        )
    }
}
