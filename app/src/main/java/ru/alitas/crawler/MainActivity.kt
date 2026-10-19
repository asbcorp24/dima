package ru.alitas.crawler

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.alitas.crawler.domain.AppScreen
import ru.alitas.crawler.ui.screens.common.PlaceholderScreen
import ru.alitas.crawler.ui.screens.dashboard.DashboardScreen
import ru.alitas.crawler.ui.screens.home.HomeMenuScreen
import ru.alitas.crawler.ui.screens.pin.PinSetupScreen
import ru.alitas.crawler.ui.screens.pin.PinUnlockScreen
import ru.alitas.crawler.ui.screens.search.SearchCrawlerScreen
import ru.alitas.crawler.ui.screens.splash.SplashScreen
import ru.alitas.crawler.ui.theme.LitasTheme

/**
 * Единственная Activity приложения.
 *
 * Здесь остаётся только связывание ViewModel с экранами,
 * системные Android-разрешения и маршрутизация.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LitasTheme {
                val vm: MainViewModel = viewModel()

                val telemetry by vm.telemetry.collectAsStateWithLifecycle()
                val pairing by vm.pairing.collectAsStateWithLifecycle()

                // Разрешения BLE отличаются для Android 12+ и старых версий.
                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                } else {
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                }

                // Запрашиваем разрешения только по действию пользователя,
                // чтобы системный диалог не появлялся поверх Splash.
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    if (result.values.all { it }) {
                        vm.startScan()
                    }
                }

                /**
                 * Главная маршрутизация приложения.
                 *
                 * Все значения AppScreen обработаны явно, поэтому when exhaustive.
                 */
                when (pairing.screen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onFinished = vm::finishSplash
                        )
                    }

                    AppScreen.SEARCH -> {
                        SearchCrawlerScreen(
                            state = pairing,
                            onScan = {
                                permissionLauncher.launch(permissions)
                            },
                            onStopScan = vm::stopScan,
                            onSelect = vm::selectCrawler
                        )
                    }

                    AppScreen.PIN_SETUP -> {
                        PinSetupScreen(
                            state = pairing,
                            onSave = vm::createPin,
                            onBack = vm::backToSearch
                        )
                    }

                    AppScreen.PIN_UNLOCK -> {
                        PinUnlockScreen(
                            state = pairing,
                            onUnlock = vm::unlock,
                            onBack = vm::backToSearch
                        )
                    }

                    AppScreen.HOME -> {
                        HomeMenuScreen(
                            telemetry = telemetry,
                            connectionStatus = pairing.connectionStatus,
                            onControlClick = vm::openDashboard,
                            onDiagnosticsClick = vm::openDiagnostics,
                            onSettingsClick = vm::openSettings,
                            onServiceClick = vm::openService
                        )
                    }

                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            telemetry = telemetry,
                            connectionStatus = pairing.connectionStatus,
                            selectedCrawler = pairing.selectedCrawler,
                            controlsEnabled = pairing.selectedCrawler?.isSimulator == true,
                            onManualStart = vm::startManual,
                            onManualStop = vm::stopManual,
                            onAutoStart = vm::startAuto,
                            onStop = vm::stop,
                            onSimulateLoss = vm::simulateSignalLoss,
                            onChooseCrawler = vm::backToSearch,
                            onHome = vm::openHome
                        )
                    }

                    AppScreen.DIAGNOSTICS -> {
                        PlaceholderScreen(
                            title = "Диагностика",
                            onBack = vm::openHome
                        )
                    }

                    AppScreen.SETTINGS -> {
                        PlaceholderScreen(
                            title = "Настройки",
                            onBack = vm::openHome
                        )
                    }

                    AppScreen.SERVICE -> {
                        PlaceholderScreen(
                            title = "Сервис",
                            onBack = vm::openHome
                        )
                    }
                }
            }
        }
    }
}
