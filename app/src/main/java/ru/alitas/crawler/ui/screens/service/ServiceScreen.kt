package ru.alitas.crawler.ui.screens.service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * Сервисный раздел.
 *
 * Обновление прошивки намеренно не добавлено:
 * по текущему ТЗ приложение не выполняет firmware update.
 */
@Composable
fun ServiceScreen(
    connectionStatus: ConnectionStatus,
    onBack: () -> Unit
) {
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

            Text("Сервис", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(
                "Обслуживание, калибровка и диагностика",
                color = Color(0xFF7F98BA),
                fontSize = 13.sp
            )

            ServiceItem("⚙", "Техническое обслуживание", "Регламент и сервисные проверки")
            ServiceItem("◎", "Калибровка датчиков", "Проверка и калибровка доступных датчиков")
            ServiceItem("◫", "Тест рентгеновского аппарата", "Только диагностический режим, без обхода блокировок")
            ServiceItem("⇩", "Экспорт диагностических данных", "Логи и техническая информация")
            ServiceItem("🔒", "Сервисный режим", "Доступ только для сервисного инженера")

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(
                        "Обновление прошивки",
                        color = Color(0xFF8299B9),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Не поддерживается приложением по текущему техническому заданию.",
                        color = Color(0xFF667D9E),
                        fontSize = 12.sp
                    )
                }
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
private fun ServiceItem(symbol: String, title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(symbol, color = Color(0xFF29A9FF), fontSize = 24.sp)
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color(0xFF7189AA), fontSize = 12.sp)
            }
            Text("›", color = Color(0xFF7189AA), fontSize = 22.sp)
        }
    }
}

@Preview(name = "Сервис", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ServicePreview() {
    LitasTheme {
        ServiceScreen(
            connectionStatus = ConnectionStatus.CONNECTED,
            onBack = {}
        )
    }
}
