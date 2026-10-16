package ru.alitas.crawler.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("АЛИТАС", fontSize = 28.sp, fontWeight = FontWeight.Black, color = AlitasBlueDark)
        Text("Подключение к кроулеру", style = MaterialTheme.typography.headlineSmall)
        Text("Выберите устройство по имени и серийному номеру.")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onScan, modifier = Modifier.weight(1f)) {
                Text("Найти BLE")
            }
            OutlinedButton(onClick = onStopScan, modifier = Modifier.weight(1f)) {
                Text("Остановить")
            }
        }

        if (state.connectionStatus == ConnectionStatus.SCANNING) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircularProgressIndicator()
                Text("Идёт поиск...")
            }
        }

        state.statusMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.devices, key = { it.id }) { device ->
                CrawlerDeviceCard(device = device, onClick = { onSelect(device) })
            }
        }
    }
}

@Composable
private fun CrawlerDeviceCard(device: DiscoveredCrawler, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(device.name, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("Серийный номер: ${device.serialNumber}")
            Text("RSSI: ${device.rssi} dBm")
            if (device.isSimulator) {
                Text("СИМУЛЯТОР — можно проверять без оборудования", color = AlitasSuccess)
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
        title = "Первичное подключение",
        subtitle = "Задайте 5-значный PIN для ${state.selectedCrawler?.serialNumber.orEmpty()}",
        state = state,
        onBack = onBack
    ) {
        Text(
            "Перед первой привязкой на реальном кроулере должен быть физически включён режим сопряжения.",
            color = AlitasBlueDark
        )

        PinField("PIN", pin) { pin = it.take(5).filter(Char::isDigit) }
        PinField("Повторите PIN", repeatPin) {
            repeatPin = it.take(5).filter(Char::isDigit)
        }

        Button(
            onClick = { onSave(pin, repeatPin) },
            enabled = pin.length == 5 && repeatPin.length == 5,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить PIN")
        }
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
        title = "Введите PIN",
        subtitle = state.selectedCrawler?.serialNumber.orEmpty(),
        state = state,
        onBack = onBack
    ) {
        PinField("5-значный PIN", pin, enabled = !state.isLockedOut) {
            pin = it.take(5).filter(Char::isDigit)
        }

        if (state.isLockedOut) {
            val min = state.lockoutSecondsRemaining / 60
            val sec = state.lockoutSecondsRemaining % 60
            Text("Повтор через %d:%02d".format(min, sec), color = AlitasDanger)
        }

        Button(
            onClick = {
                onUnlock(pin)
                pin = ""
            },
            enabled = pin.length == 5 && !state.isLockedOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Разблокировать")
        }
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
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("АЛИТАС", fontSize = 28.sp, fontWeight = FontWeight.Black, color = AlitasBlueDark)
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(subtitle)
        HorizontalDivider()

        state.pinError?.let {
            Text(it, color = AlitasDanger, fontWeight = FontWeight.Bold)
        }

        content()

        Spacer(Modifier.height(6.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Выбрать другой кроулер")
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
        modifier = Modifier.fillMaxWidth()
    )
}
