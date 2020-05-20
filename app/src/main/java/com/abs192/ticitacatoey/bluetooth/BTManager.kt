package com.abs192.ticitacatoey.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

class BTManager {

    val requestCodeEnableBT = 214
    val requestCodePermissionBT = 125

    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var btDiscoveredDevices = arrayListOf<BTDevice>()

    fun isSupportedOnDevice(): Boolean {
        bluetoothAdapter ?: return false
        return true
    }

    fun enableBT(activity: Activity) {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, requestCodeEnableBT)
        }
    }

    fun requestPermissions(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
            == PackageManager.PERMISSION_DENIED
        ) {
            Log.d("permission", "denied")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH),
                requestCodePermissionBT
            )
        } else {
            Log.d("permission", "granted")
        }
    }

    fun startDiscovery(activity: Activity) {
        btDiscoveredDevices.clear()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(receiver, filter)
        bluetoothAdapter.startDiscovery()
    }

    fun stopDiscovery(activity: Activity) {
        activity.unregisterReceiver(receiver)
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        btDiscoveredDevices.add(BTDevice(it.name, device.address))
                    }
                    Log.d("BTManager", "found device " + device?.name)
                }
            }
        }
    }

    fun beDiscoverable(activity: Activity) {
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
        activity.startActivity(discoverableIntent)
    }

    class BTDevice(
        deviceName: String,
        macAddress: String
    )
}