package ru.alitas.crawler.ui.screens.more

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
 * Дополнительное меню приложения.
 */
@Composable
fun MoreScreen(
    connectionStatus: ConnectionStatus,
    onBack: () -> Unit,
    onLogs: () -> Unit,
    onSettings: () -> Unit,
    onService: () -> Unit
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

            Text("Ещё", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)

            MoreItem("▤", "Журнал событий", "Состояния, предупреждения и диагностика", onLogs)
            MoreItem("⚙", "Настройки", "Интерфейс и подключение", onSettings)
            MoreItem("◆", "Сервис", "Обслуживание и калибровка", onService)
            MoreItem("?", "Справка", "Краткая помощь оператору", {})
            MoreItem("i", "О приложении", "ЛИТАС Кроулер · локальное Android-приложение", {})

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF27131A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Приложение работает офлайн. Облачные сервисы, видео и удалённое управление не используются.",
                    modifier = Modifier.padding(14.dp),
                    color = Color(0xFFE69AA9),
                    fontSize = 12.sp
                )
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
private fun MoreItem(
    symbol: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1524)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(symbol, color = Color(0xFF29A9FF), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color(0xFF7189AA), fontSize = 12.sp)
            }
            Text("›", color = Color(0xFF7189AA), fontSize = 22.sp)
        }
    }
}

@Preview(name = "Ещё", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun MorePreview() {
    LitasTheme {
        MoreScreen(
            connectionStatus = ConnectionStatus.CONNECTED,
            onBack = {},
            onLogs = {},
            onSettings = {},
            onService = {}
        )
    }
}
