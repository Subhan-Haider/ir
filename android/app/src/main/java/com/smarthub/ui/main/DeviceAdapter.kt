package com.smarthub.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smarthub.models.Device
import com.smarthub.R

class DeviceAdapter(
    private val devices: List<Device>,
    private val onCommandSent: (IntArray) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvDeviceName)
        val tvType: TextView = view.findViewById(R.id.tvDeviceType)
        val btnPower: Button = view.findViewById(R.id.btnPower)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]
        holder.tvName.text = device.name
        holder.tvType.text = device.deviceType
        
        holder.btnPower.setOnClickListener {
            // Mocking a Power Command IR Code 9000, 4500, 560...
            val dummyIrCommand = intArrayOf(9000, 4500, 560, 560, 560, 1690, 560, 560)
            onCommandSent(dummyIrCommand)
        }
    }

    override fun getItemCount() = devices.size
}
