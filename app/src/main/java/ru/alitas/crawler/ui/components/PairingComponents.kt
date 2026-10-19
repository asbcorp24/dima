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
import ru.alitas.crawler.ui.theme.LitasCyan
import ru.alitas.crawler.ui.theme.LitasDanger
import ru.alitas.crawler.ui.theme.LitasSuccess

/**
 * Тёмная фирменная шапка экранов подключения и PIN.
 */
@Composable
fun BrandHeader(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020612), Color(0xFF071B3A))
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LitasLogo(Modifier.size(42.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "ЛИТАС",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text(subtitle, color = Color(0xFF7893B7), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun BleStatusBanner(state: PairingUiState) {
    val scanning = state.connectionStatus == ConnectionStatus.SCANNING

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1728)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (scanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    strokeWidth = 3.dp,
                    color = LitasCyan
                )
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    state.statusMessage ?: "Выберите устройство из списка",
                    color = Color(0xFF7890B0),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun InfoBox(title: String, text: String, danger: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (danger) Color(0xFF35161D) else Color(0xFF0D213C)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
                title,
                color = if (danger) Color(0xFFFF6B75) else LitasCyan,
                fontWeight = FontWeight.Black
            )
            Text(text, color = Color(0xFFC3D1E5), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

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
            focusedLabelColor = LitasCyan,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            unfocusedBorderColor = Color(0xFF294462),
            unfocusedLabelColor = Color(0xFF7C92B0),
            disabledTextColor = Color(0xFF677990)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
