package ru.alitas.crawler.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context

class CrawlerBleManager(private val context: Context) {

    interface Listener {
        fun onConnected(device: BluetoothDevice)
        fun onDisconnected(device: BluetoothDevice)
        fun onConnectionError(device: BluetoothDevice?)
    }

    private val manager = context.getSystemService(BluetoothManager::class.java)
    private val adapter get() = manager.adapter
    private var gatt: BluetoothGatt? = null
    private var connectedDevice: BluetoothDevice? = null
    private var listener: Listener? = null

    fun setListener(value: Listener?) {
        listener = value
    }

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    @SuppressLint("MissingPermission")
    fun startScan(onDeviceFound: (BluetoothDevice, Int) -> Unit): ScanCallback {
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                onDeviceFound(result.device, result.rssi)
            }

            override fun onScanFailed(errorCode: Int) {
                listener?.onConnectionError(null)
            }
        }
        adapter.bluetoothLeScanner.startScan(callback)
        return callback
    }

    @SuppressLint("MissingPermission")
    fun stopScan(callback: ScanCallback?) {
        if (callback != null) adapter.bluetoothLeScanner.stopScan(callback)
    }

    @SuppressLint("MissingPermission")
    fun connectCodedPhy(device: BluetoothDevice) {
        gatt?.close()
        connectedDevice = device

        gatt = device.connectGatt(
            context,
            false,
            object : BluetoothGattCallback() {
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt,
                    status: Int,
                    newState: Int
                ) {
                    when {
                        status != BluetoothGatt.GATT_SUCCESS -> {
                            listener?.onConnectionError(device)
                            gatt.close()
                        }

                        newState == BluetoothProfile.STATE_CONNECTED -> {
                            connectedDevice = device
                            listener?.onConnected(device)
                        }

                        newState == BluetoothProfile.STATE_DISCONNECTED -> {
                            listener?.onDisconnected(device)
                            gatt.close()
                        }
                    }
                }
            },
            BluetoothDevice.TRANSPORT_LE,
            BluetoothDevice.PHY_LE_CODED_MASK
        )
    }

    @SuppressLint("MissingPermission")
    fun reconnect() {
        connectedDevice?.let { connectCodedPhy(it) }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        connectedDevice = null
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }
}
