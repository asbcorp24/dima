# Установка и запуск ЛИТАС Кроулер на Windows 10/11

## 1. Установить Git

Скачать Git for Windows: https://git-scm.com/download/win

Проверка:

```bat
git --version
```

## 2. Установить Android Studio

Скачать Android Studio: https://developer.android.com/studio

При установке оставить включёнными:
- Android SDK
- Android SDK Platform
- Android Virtual Device

## 3. Установить Android SDK

Android Studio -> More Actions -> SDK Manager.

Установить:
- Android SDK Platform 35
- Android SDK Build-Tools 35
- Android SDK Platform-Tools
- Android SDK Command-line Tools
- Android Emulator

## 4. Java

Проект использует JDK 17. Android Studio содержит подходящий JBR.

В Android Studio:
Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK

Выбрать Embedded JDK / jbr-17.

## 5. Клонировать проект

```bat
cd C:\
mkdir Projects
cd Projects
git clone https://github.com/asbcorp24/dima.git
cd dima
```

После публикации версии 0.1.0 основной код находится в ветке main.

## 6. Открыть проект

Android Studio -> Open -> C:\Projects\dima

Дождаться Gradle Sync.

Проект содержит Gradle Wrapper 8.11.1, поэтому отдельно устанавливать Gradle не требуется.

## 7. Запуск на эмуляторе

Android Studio -> Device Manager -> Create Device.

Можно создать:
- Pixel 8 для проверки смартфона;
- Pixel Tablet для проверки планшетной компоновки.

Запустить устройство и нажать Run.

## 8. Запуск на физическом Android

На устройстве:
- включить режим разработчика;
- включить USB debugging;
- подключить устройство по USB;
- подтвердить разрешение отладки.

Проверка:

```bat
adb devices
```

После появления устройства нажать Run в Android Studio.

## 9. Сборка APK из командной строки

Debug APK:

```bat
gradlew.bat assembleDebug
```

Файл будет создан здесь:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## 10. BLE

Эмулятор подходит для проверки интерфейса, но не для реальных испытаний BLE.

Для работы с кроулером потребуется физическое Android-устройство с поддержкой BLE 5 и LE Coded PHY. UUID, характеристики и структура пакетов будут добавлены после согласования протокола с разработчиками бортового Контроллера.
