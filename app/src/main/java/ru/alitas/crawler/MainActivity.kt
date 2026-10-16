package ru.alitas.crawler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.alitas.crawler.ui.AlitasTheme
import ru.alitas.crawler.ui.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AlitasTheme {
                val vm: MainViewModel = viewModel()
                val telemetry by vm.telemetry.collectAsStateWithLifecycle()

                DashboardScreen(
                    telemetry = telemetry,
                    onManualStart = vm::startManual,
                    onManualStop = vm::stopManual,
                    onAutoStart = vm::startAuto,
                    onStop = vm::stop
                )
            }
        }
    }
}
