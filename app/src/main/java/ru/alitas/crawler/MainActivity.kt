package ru.alitas.crawler

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.alitas.crawler.domain.AppScreen
import ru.alitas.crawler.ui.LitasTheme
import ru.alitas.crawler.ui.DashboardScreen
import ru.alitas.crawler.ui.PinSetupScreen
import ru.alitas.crawler.ui.PinUnlockScreen
import ru.alitas.crawler.ui.SearchCrawlerScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LitasTheme {
                val vm: MainViewModel = viewModel()
                val telemetry by vm.telemetry.collectAsStateWithLifecycle()
                val pairing by vm.pairing.collectAsStateWithLifecycle()

                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                } else {
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    if (result.values.all { it }) vm.startScan()
                }

                LaunchedEffect(Unit) {
                    permissionLauncher.launch(permissions)
                }

                when (pairing.screen) {
                    AppScreen.SEARCH -> SearchCrawlerScreen(
                        state = pairing,
                        onScan = { permissionLauncher.launch(permissions) },
                        onStopScan = vm::stopScan,
                        onSelect = vm::selectCrawler
                    )

                    AppScreen.PIN_SETUP -> PinSetupScreen(
                        state = pairing,
                        onSave = vm::createPin,
                        onBack = vm::backToSearch
                    )

                    AppScreen.PIN_UNLOCK -> PinUnlockScreen(
                        state = pairing,
                        onUnlock = vm::unlock,
                        onBack = vm::backToSearch
                    )

                    AppScreen.DASHBOARD -> DashboardScreen(
                        telemetry = telemetry,
                        connectionStatus = pairing.connectionStatus,
                        selectedCrawler = pairing.selectedCrawler,
                        controlsEnabled = pairing.selectedCrawler?.isSimulator == true,
                        onManualStart = vm::startManual,
                        onManualStop = vm::stopManual,
                        onAutoStart = vm::startAuto,
                        onStop = vm::stop,
                        onSimulateLoss = vm::simulateSignalLoss,
                        onChooseCrawler = vm::backToSearch
                    )
                }
            }
        }
    }
}
