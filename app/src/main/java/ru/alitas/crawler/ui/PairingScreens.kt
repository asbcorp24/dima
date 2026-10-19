package ru.alitas.crawler.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.DiscoveredCrawler
import ru.alitas.crawler.domain.PairingUiState

@Composable
fun SearchCrawlerScreen(
    state: PairingUiState,
    onScan: () -> Unit,
    onStopScan: () -> Unit,
    onSelect: (DiscoveredCrawler) -> Unit
) {
    Surface(color = LitasBackground, modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            BrandHeader("Подключение", "Найдите и выберите BLE-кроулер")

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatusBanner(state)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onScan,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Найти устройства") }

                    OutlinedButton(
                        onClick = onStopScan,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Остановить") }
                }

                Text(
                    "ДОСТУПНЫЕ УСТРОЙСТВА",
                    color = LitasMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                if (state.devices.isEmpty() && state.connectionStatus != ConnectionStatus.SCANNING) {
                    EmptyDevices()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.devices, key = { it.id }) { device ->
                            CrawlerDeviceCard(device) { onSelect(device) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandHeader(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(LitasBlueDark, LitasBlue)))
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "ЛИТАС",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall)
            Text(subtitle, color = Color(0xFFD8E6FF), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun StatusBanner(state: PairingUiState) {
    val scanning = state.connectionStatus == ConnectionStatus.SCANNING

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (scanning) LitasBlueSoft else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (scanning) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            } else {
                Box(
                    Modifier.size(12.dp)
                        .background(LitasSuccess, RoundedCornerShape(12.dp))
                )
            }

            Column(Modifier.weight(1f)) {
                Text(
                    if (scanning) "Поиск BLE-кроулеров" else "Bluetooth готов",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    state.statusMessage ?: "Выберите устройство из списка ниже",
                    color = LitasMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyDevices() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("BLE", color = LitasBlue, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("Устройства пока не найдены", fontWeight = FontWeight.Bold)
            Text(
                "Нажмите «Найти устройства» и убедитесь, что Bluetooth включён.",
                color = LitasMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CrawlerDeviceCard(device: DiscoveredCrawler, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (device.isSimulator) Color(0xFFEAF8F2) else LitasBlueSoft,
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (device.isSimulator) "SIM" else "BLE",
                    color = if (device.isSimulator) LitasSuccess else LitasBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(device.name, fontWeight = FontWeight.Black, fontSize = 17.sp)
                Text(device.serialNumber, color = LitasMuted, fontSize = 13.sp)
                if (device.isSimulator) {
                    Text("Режим симуляции", color = LitasSuccess, fontWeight = FontWeight.Bold)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${device.rssi} dBm",
                    fontWeight = FontWeight.Bold,
                    color = signalColor(device.rssi)
                )
                Text("Подключить ›", color = LitasBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PinSetupScreen(
    state: PairingUiState,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var repeatPin by remember { mutableStateOf("") }

    PinContainer(
        title = "Создание PIN",
        subtitle = state.selectedCrawler?.serialNumber.orEmpty(),
        state = state,
        onBack = onBack
    ) {
        InfoBox(
            "Первичная привязка",
            "На реальном кроулере должен быть физически включён режим сопряжения."
        )

        PinField("5-значный PIN", pin) { pin = it.take(5).filter(Char::isDigit) }
        PinField("Повторите PIN", repeatPin) {
            repeatPin = it.take(5).filter(Char::isDigit)
        }

        Button(
            onClick = { onSave(pin, repeatPin) },
            enabled = pin.length == 5 && repeatPin.length == 5,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text("Сохранить и продолжить") }
    }
}

@Composable
fun PinUnlockScreen(
    state: PairingUiState,
    onUnlock: (String) -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }

    PinContainer(
        title = "Доступ к кроулеру",
        subtitle = state.selectedCrawler?.serialNumber.orEmpty(),
        state = state,
        onBack = onBack
    ) {
        Text("Введите PIN", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Text(
            "Для управления кроулером требуется подтверждение доступа.",
            color = LitasMuted
        )

        PinField("5-значный PIN", pin, enabled = !state.isLockedOut) {
            pin = it.take(5).filter(Char::isDigit)
        }

        if (state.isLockedOut) {
            val min = state.lockoutSecondsRemaining / 60
            val sec = state.lockoutSecondsRemaining % 60
            InfoBox(
                "Ввод временно заблокирован",
                "Повторная попытка через %d:%02d".format(min, sec),
                danger = true
            )
        }

        Button(
            onClick = {
                onUnlock(pin)
                pin = ""
            },
            enabled = pin.length == 5 && !state.isLockedOut,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text("Разблокировать") }
    }
}

@Composable
private fun PinContainer(
    title: String,
    subtitle: String,
    state: PairingUiState,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(color = LitasBackground, modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            BrandHeader(title, subtitle)

            Column(
                Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        state.pinError?.let {
                            InfoBox("Ошибка PIN", it, danger = true)
                        }
                        content()
                    }
                }

                Spacer(Modifier.height(2.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Выбрать другой кроулер") }
            }
        }
    }
}

@Composable
private fun InfoBox(title: String, text: String, danger: Boolean = false) {
    val background = if (danger) Color(0xFFFFEEEE) else LitasBlueSoft
    val accent = if (danger) LitasDanger else LitasBlue

    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(title, color = accent, fontWeight = FontWeight.Black)
            Text(text, color = LitasInk, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PinField(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LitasBlue,
            focusedLabelColor = LitasBlue
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun signalColor(rssi: Int): Color = when {
    rssi >= -60 -> LitasSuccess
    rssi >= -80 -> LitasWarning
    else -> LitasDanger
}
