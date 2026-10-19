# ЛИТАС — управление рентгеновским кроулером

Проект реализуется как **нативное Android-приложение на Kotlin**.

React Native и Expo не используются.

## Стек

- Kotlin
- Android SDK
- Jetpack Compose
- Material 3
- Coroutines / StateFlow
- Native Android Bluetooth Low Energy API

## BLE

Минимальная версия Android — API 26, чтобы использовать native BLE API и запрос LE Coded PHY.

UUID сервисов/характеристик и пакетный протокол пока намеренно не заданы: по ТЗ они должны быть согласованы с разработчиками бортового Контроллера.

## Уже есть

- главный экран оператора;
- фирменная сине-белая стилистика ЛИТАС;
- телеметрия;
- аккумуляторы;
- датчики;
- ручное движение по удержанию;
- автоматическое движение;
- крупная STOP-кнопка;
- mock-контроллер;
- native BLE scan/Coded PHY заготовка.


## Структура UI

Интерфейс разделён по страницам, чтобы проект было проще редактировать и масштабировать:

```text
ui/
├── components/              # Переиспользуемые UI-компоненты
│   └── PairingComponents.kt
├── preview/                 # Общие тестовые данные для Compose Preview
│   └── PreviewData.kt
├── screens/                 # Каждая страница в отдельной папке/файле
│   ├── dashboard/
│   │   ├── DashboardScreen.kt
│   │   └── DashboardComponents.kt
│   ├── pin/
│   │   ├── PinSetupScreen.kt
│   │   └── PinUnlockScreen.kt
│   └── search/
│       └── SearchCrawlerScreen.kt
├── templates/
│   └── NewScreenTemplate.kt
└── theme/
    └── LitasTheme.kt
```

Правило для новых страниц: UI получает состояние параметрами и сообщает о действиях через callback-функции. BLE, хранилища и бизнес-логика остаются во ViewModel/data-слое. У каждой страницы должен быть собственный Compose Preview.
