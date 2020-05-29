package com.abs192.ticitacatoey.views.dialogs

import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abs192.ticitacatoey.MainActivity
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.bluetooth.BTDevice
import com.abs192.ticitacatoey.bluetooth.BTService

class BTSearchDialog(activity: MainActivity, private val btService: BTService) :
    Dialog(activity) {

    private var textView: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var listAdapter: RVDevicesListAdapter? = null
    private val deviceFoundList = arrayListOf<BTDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setCanceledOnTouchOutside(false)
        setCancelable(false)
        setContentView(R.layout.layout_bt_search)

        textView = findViewById(R.id.bt_search_text)
        recyclerView = findViewById(R.id.bt_search_rv)

        Log.d(javaClass.simpleName, "scan started ")
        deviceFoundList.addAll(btService.getPairedDevices().map { BTDevice(it, true) })
        recyclerView?.layoutManager = LinearLayoutManager(context)
        listAdapter = RVDevicesListAdapter(
            context,
            deviceFoundList,
            object : RVDevicesListAdapter.OnDeviceConnectListener {
                override fun onConnect(btDevice: BTDevice) {
                    btService.connect(btDevice)
                }
            })
        recyclerView?.adapter = listAdapter

        btService.startScanning(object : BTService.DiscoveryCallback {
//            override fun onDevicePaired(device: BluetoothDevice?) {
//                Log.d(javaClass.simpleName, "device paired " + device?.name + " " + device?.address)
//            }

            override fun onDiscoveryStarted() {
                Log.d(javaClass.simpleName, "discovery started ")
            }

//            override fun onDeviceUnpaired(device: BluetoothDevice?) {
//                Log.d(
//                    javaClass.simpleName,
//                    "device unpaired " + device?.name + " " + device?.address
//                )
//            }

            override fun onError() {
                Log.d(javaClass.simpleName, "discovery error ")
            }

            override fun onDiscoveryFinished() {
                Log.d(javaClass.simpleName, "discovering finished")
            }

            override fun onDeviceFound(device: BluetoothDevice) {
                Log.d(javaClass.simpleName, "device found " + device?.name + " " + device?.address)
                deviceFoundList.add(BTDevice(device, false))
                listAdapter?.notifyDataSetChanged()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.dismiss()
    }

    class RVDevicesListAdapter(
        var context: Context,
        var deviceList: ArrayList<BTDevice>,
        var onDeviceConnectListener: OnDeviceConnectListener
    ) :
        RecyclerView.Adapter<RVDevicesListAdapter.ViewHolder>() {

        private var mInflater: LayoutInflater? = null

        init {
            mInflater = LayoutInflater.from(context)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewDeviceName: TextView = itemView.findViewById(R.id.bt_device_list_item_name)
            var textViewDevicePaired: TextView =
                itemView.findViewById(R.id.bt_device_list_item_paired)
            var buttonConnect: Button = itemView.findViewById(R.id.bt_device_list_item_connect)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                mInflater!!.inflate(
                    R.layout.layout_bt_device_list_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return deviceList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val name = deviceList[position].device.name
            val isPaired = deviceList[position].isPaired
            holder.textViewDeviceName.text = name
            if (isPaired) {
                holder.textViewDevicePaired.visibility = View.VISIBLE
            } else {
                holder.textViewDevicePaired.visibility = View.INVISIBLE
            }
            holder.buttonConnect.setOnClickListener { onDeviceConnectListener.onConnect(deviceList[position]) }
        }

        interface OnDeviceConnectListener {
            fun onConnect(btDevice: BTDevice)
        }
    }

}
