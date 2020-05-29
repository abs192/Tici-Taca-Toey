package com.abs192.ticitacatoey.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.abs192.ticitacatoey.MainActivity
import com.abs192.ticitacatoey.views.dialogs.ToastDialog

class BTService(private val activity: MainActivity) {

    private val tag = "BTService"
    private val requestCodeMakeDiscoverable = 131
    private val requestCodeEnableBT = 133

    private val requestCodePermissionBT = 125
    private val requestCodePermissionBTAdmin = 215
    private val requestCodePermissionAccessCoarseLocation = 216

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    private var makeDiscoverableCallback: MakeDiscoverableCallback? = null

    fun onStart() {
        bluetoothManager =
            activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager?.adapter
        }
        activity.registerReceiver(
            bluetoothReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }


    fun isSupportedOnDevice(): Boolean {
        bluetoothAdapter ?: return false
        return true
    }

    fun showEnableDialog() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(enableBtIntent, requestCodeEnableBT)
            }
        }
    }

    fun requestPermissions() {
        requestPermission(Manifest.permission.BLUETOOTH, requestCodePermissionBT)
        requestPermission(
            Manifest.permission.BLUETOOTH_ADMIN,
            requestCodePermissionBTAdmin
        )
        requestPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            requestCodePermissionAccessCoarseLocation
        )
    }

    private fun requestPermission(permissionString: String, requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(activity, permissionString)
            == PackageManager.PERMISSION_DENIED
        ) {
            Log.d("permission", "denied $permissionString")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permissionString),
                requestCode
            )
        } else {
            Log.d("permission", "granted $permissionString")
        }
    }


    fun makeDiscoverable(makeDiscoverableCallback: MakeDiscoverableCallback) {
        this.makeDiscoverableCallback = makeDiscoverableCallback
        if (bluetoothAdapter?.scanMode !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
        ) {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            activity.startActivityForResult(discoverableIntent, requestCodeMakeDiscoverable)
        }
        this.makeDiscoverableCallback?.onStarted()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(tag, " onRequestPermissionsResult $requestCode $permissions")
//        when (requestCode) {
//            requestCodePermissionBT -> {
        if ((grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            Log.d(tag, "permission denied")
            ToastDialog(
                activity,
                "Permission denied for accessing phone's bluetooth"
            ).show()
            activity.backgroundCanvas?.showErrorTint()
        }
        return
//            }
//        }
    }

    fun onStop() {
        activity.unregisterReceiver(bluetoothReceiver)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            requestCodeMakeDiscoverable -> {
                Log.d(tag, "activity result $requestCode $resultCode")
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    makeDiscoverableCallback?.onStarted()
                } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                    makeDiscoverableCallback?.onCancelled()
                }
            }
        }
    }


    private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
//                if (bluetoothCallback != null) {
//                    ThreadHelper.run(runOnUi, activity) {
//                        when (state) {
//                            BluetoothAdapter.STATE_OFF -> bluetoothCallback.onBluetoothOff()
//                            BluetoothAdapter.STATE_TURNING_OFF -> bluetoothCallback.onBluetoothTurningOff()
//                            BluetoothAdapter.STATE_ON -> bluetoothCallback.onBluetoothOn()
//                            BluetoothAdapter.STATE_TURNING_ON -> bluetoothCallback.onBluetoothTurningOn()
//                        }
//                    }
//            }
            }
        }
    }


    fun startScanning(discoveryCallback: DiscoveryCallback) {
        activity.setDiscoveryCallback(discoveryCallback)

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)

        activity.registerReceiver(activity.bluetoothScanReceiver, filter)
        bluetoothAdapter?.startDiscovery()
    }

    fun getPairedDevices(): ArrayList<BluetoothDevice> {
        val list = arrayListOf<BluetoothDevice>()
        bluetoothAdapter?.bondedDevices?.let { list.addAll(it) }
        return list
    }

    fun connect(btDevice: BTDevice) {
//        bluetooth.connectToDevice(btDevice.device)
    }

    fun getDeviceName(): String? {
        return bluetoothAdapter?.name
    }

    interface MakeDiscoverableCallback {
        fun onStarted()
        fun onCancelled()
    }

    interface DiscoveryCallback {
        fun onDiscoveryStarted()
        fun onError()
        fun onDiscoveryFinished()
        fun onDeviceFound(device: BluetoothDevice)
    }

}