package ru.alitas.crawler.ui.templates

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
import ru.alitas.crawler.ui.theme.LitasBackground
import ru.alitas.crawler.ui.theme.LitasMuted
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Шаблон для создания новой страницы.
 *
 * Рекомендуемый порядок:
 * 1. Скопировать файл в ui/screens/<имя_раздела>/.
 * 2. Переименовать NewScreenTemplate в имя страницы.
 * 3. Передавать состояние параметрами, а действия — callback-функциями.
 * 4. Не обращаться к BLE, ViewModel и хранилищам прямо из UI.
 * 5. Оставить Preview для телефона и планшета.
 *
 * Такой подход делает экран независимым и позволяет легко менять дизайн.
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

            // TODO: Добавьте элементы интерфейса этой страницы.
            // TODO: onAction передавайте в кнопку/контрол, когда появится действие.
        }
    }
}

@Preview(name = "Шаблон — телефон", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun NewScreenTemplatePhonePreview() {
    LitasTheme {
        NewScreenTemplate(
            title = "Новая страница",
            subtitle = "Предпросмотр для телефона"
        )
    }
}

@Preview(name = "Шаблон — планшет", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun NewScreenTemplateTabletPreview() {
    LitasTheme {
        NewScreenTemplate(
            title = "Новая страница",
            subtitle = "Предпросмотр для планшета"
        )
    }
}
