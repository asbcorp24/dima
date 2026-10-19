package ru.alitas.crawler.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Шаблон новой страницы ЛИТАС.
 *
 * Как использовать:
 * 1. Скопируйте файл.
 * 2. Переименуйте NewScreenTemplate -> имя вашей страницы.
 * 3. Добавьте нужные параметры состояния и callback-функции.
 * 4. Оставьте Preview для телефона и планшета.
 * 5. Не вызывайте здесь BLE/ViewModel напрямую — передавайте готовые данные параметрами.
 */
@Composable
fun NewScreenTemplate(
    title: String = "Новая страница",
    subtitle: String = "Описание страницы",
    onAction: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LitasBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = LitasMuted
            )

            // TODO: добавьте элементы интерфейса страницы.
            // TODO: onAction вызывайте из кнопки/контрола, когда добавите действие.
        }
    }
}

@Preview(
    name = "Шаблон — телефон",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun NewScreenTemplatePhonePreview() {
    LitasTheme {
        NewScreenTemplate(
            title = "Новая страница",
            subtitle = "Предпросмотр для телефона"
        )
    }
}

@Preview(
    name = "Шаблон — планшет landscape",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun NewScreenTemplateTabletPreview() {
    LitasTheme {
        NewScreenTemplate(
            title = "Новая страница",
            subtitle = "Предпросмотр для планшета"
        )
    }
}
