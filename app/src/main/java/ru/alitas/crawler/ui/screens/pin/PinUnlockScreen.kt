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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
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
import ru.alitas.crawler.ui.theme.LitasMuted
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Страница разблокировки уже привязанного кроулера.
 *
 * Счётчик попыток и таймер блокировки находятся в ViewModel.
 */
@Composable
fun PinUnlockScreen(
    state: PairingUiState,
    onUnlock: (String) -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Surface(color = LitasBackground, modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            BrandHeader(
                title = "Доступ к кроулеру",
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

                        Text(
                            "Введите PIN",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            "Для управления кроулером требуется подтверждение доступа.",
                            color = LitasMuted
                        )

                        PinField(
                            label = "5-значный PIN",
                            value = pin,
                            enabled = !state.isLockedOut
                        ) {
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
                        ) {
                            Text("Разблокировать")
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

@Preview(name = "Ввод PIN", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PinUnlockPreview() {
    LitasTheme {
        PinUnlockScreen(
            state = PairingUiState(
                screen = AppScreen.PIN_UNLOCK,
                connectionStatus = ConnectionStatus.CONNECTED,
                selectedCrawler = PreviewSimulatorCrawler,
                statusMessage = "Введите PIN кроулера"
            ),
            onUnlock = {},
            onBack = {}
        )
    }
}
