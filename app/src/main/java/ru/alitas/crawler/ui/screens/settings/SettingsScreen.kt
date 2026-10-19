package ru.alitas.crawler.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.ui.components.HomeHeader
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Настройки интерфейса приложения.
 *
 * Пока параметры локальные для UI. Позже их можно перенести
 * в DataStore/настройки контроллера без переделки экрана.
 */
@Composable
fun SettingsScreen(
    connectionStatus: ConnectionStatus,
    onBack: () -> Unit
) {
    var sounds by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(true) }
    var autoReconnect by remember { mutableStateOf(true) }

    Surface(Modifier.fillMaxSize(), color = Color(0xFF030817)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF020612), Color(0xFF061329))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HomeHeader(connectionStatus)

            Text("Настройки", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)

            SettingsSection("ОБЩИЕ") {
                SettingsValueRow("Язык", "Русский")
                SettingsValueRow("Тема", "Тёмная")
                SettingsSwitchRow("Звуки критических событий", sounds) { sounds = it }
                SettingsSwitchRow("Уведомления", notifications) { notifications = it }
            }

            SettingsSection("ПОДКЛЮЧЕНИЕ") {
                SettingsSwitchRow("Автопереподключение BLE", autoReconnect) { autoReconnect = it }
                SettingsValueRow("Время сканирования", "10 секунд")
                SettingsValueRow("Режим BLE", "LE Coded PHY")
            }

            SettingsSection("ПРИЛОЖЕНИЕ") {
                SettingsValueRow("Единицы измерения", "Метрические")
                SettingsValueRow("Версия", "0.2.0")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("← Главное меню")
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Color(0xFF6F89AD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsValueRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White)
        Text(value, color = Color(0xFF8299B9), fontSize = 13.sp)
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview(name = "Настройки", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SettingsPreview() {
    LitasTheme {
        SettingsScreen(
            connectionStatus = ConnectionStatus.CONNECTED,
            onBack = {}
        )
    }
}
