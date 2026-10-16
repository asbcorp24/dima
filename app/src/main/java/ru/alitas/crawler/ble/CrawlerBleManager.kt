package ru.alitas.crawler.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context

/**
 * Native Android BLE.
 *
 * UUID и формат пакетов не выдумываются: они будут добавлены после согласования
 * протокола с разработчиками Контроллера.
 *
 * minSdk=26 позволяет запросить BLE LE Coded PHY напрямую через Android API.
 */
class CrawlerBleManager(private val context: Context) {
    private val manager = context.getSystemService(BluetoothManager::class.java)
    private val adapter get() = manager.adapter
    private var gatt: BluetoothGatt? = null

    @SuppressLint("MissingPermission")
    fun startScan(onDeviceFound: (BluetoothDevice, Int) -> Unit): ScanCallback {
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                onDeviceFound(result.device, result.rssi)
            }
        }
        adapter.bluetoothLeScanner.startScan(callback)
        return callback
    }

    @SuppressLint("MissingPermission")
    fun stopScan(callback: ScanCallback) {
        adapter.bluetoothLeScanner.stopScan(callback)
    }

    @SuppressLint("MissingPermission")
    fun connectCodedPhy(device: BluetoothDevice, callback: BluetoothGattCallback) {
        gatt?.close()
        gatt = device.connectGatt(
            context,
            false,
            callback,
            BluetoothDevice.TRANSPORT_LE,
            BluetoothDevice.PHY_LE_CODED_MASK
        )
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }
}
