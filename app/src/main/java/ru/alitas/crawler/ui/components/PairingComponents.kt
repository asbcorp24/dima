package ru.alitas.crawler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alitas.crawler.domain.ConnectionStatus
import ru.alitas.crawler.domain.PairingUiState
import ru.alitas.crawler.ui.theme.LitasBlue
import ru.alitas.crawler.ui.theme.LitasBlueDark
import ru.alitas.crawler.ui.theme.LitasBlueSoft
import ru.alitas.crawler.ui.theme.LitasDanger
import ru.alitas.crawler.ui.theme.LitasInk
import ru.alitas.crawler.ui.theme.LitasMuted
import ru.alitas.crawler.ui.theme.LitasSuccess

/**
 * Общая фирменная шапка экранов подключения и PIN.
 */
@Composable
fun BrandHeader(title: String, subtitle: String) {
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

/**
 * Статусный блок BLE. Используется на экране поиска устройств.
 */
@Composable
fun BleStatusBanner(state: PairingUiState) {
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
                    Modifier
                        .size(12.dp)
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

/**
 * Универсальный информационный/ошибочный блок для небольших сообщений.
 */
@Composable
fun InfoBox(title: String, text: String, danger: Boolean = false) {
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

/**
 * Стандартное поле пятизначного PIN.
 */
@Composable
fun PinField(
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
