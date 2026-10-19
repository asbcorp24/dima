package ru.alitas.crawler.ui.screens.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.alitas.crawler.domain.AppScreen
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.PairingUiState
import ru.alitas.crawler.ui.components.BrandHeader
import ru.alitas.crawler.ui.components.InfoBox
import ru.alitas.crawler.ui.components.PinField
import ru.alitas.crawler.ui.preview.PreviewSimulatorCrawler
import ru.alitas.crawler.ui.theme.LitasBackground
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Страница первичного создания пятизначного PIN.
 *
 * Сохранение выполняет ViewModel. Экран отвечает только за ввод и отображение.
 */
@Composable
fun PinSetupScreen(
    state: PairingUiState,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var repeatPin by remember { mutableStateOf("") }

    Surface(color = LitasBackground, modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            BrandHeader(
                title = "Создание PIN",
                subtitle = state.selectedCrawler?.serialNumber.orEmpty()
            )

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

                        InfoBox(
                            "Первичная привязка",
                            "На реальном кроулере должен быть физически включён режим сопряжения."
                        )

                        PinField("5-значный PIN", pin) {
                            pin = it.take(5).filter(Char::isDigit)
                        }

                        PinField("Повторите PIN", repeatPin) {
                            repeatPin = it.take(5).filter(Char::isDigit)
                        }

                        Button(
                            onClick = { onSave(pin, repeatPin) },
                            enabled = pin.length == 5 && repeatPin.length == 5,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Сохранить и продолжить")
                        }
                    }
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Выбрать другой кроулер")
                }
            }
        }
    }
}

@Preview(name = "Создание PIN", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PinSetupPreview() {
    LitasTheme {
        PinSetupScreen(
            state = PairingUiState(
                screen = AppScreen.PIN_SETUP,
                connectionStatus = ConnectionStatus.CONNECTED,
                selectedCrawler = PreviewSimulatorCrawler,
                statusMessage = "Первичное подключение"
            ),
            onSave = { _, _ -> },
            onBack = {}
        )
    }
}
