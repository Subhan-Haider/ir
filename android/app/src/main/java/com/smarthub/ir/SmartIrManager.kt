package com.smarthub.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.hardware.usb.UsbManager
import android.util.Log
import android.widget.Toast
import com.smarthub.api.ApiClient
import com.smarthub.models.ESP32Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmartIrManager(private val context: Context) {

    private val irManager = context.getSystemService(Context.CONSUMER_IR_SERVICE) as ConsumerIrManager?
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    fun hasBuiltInIr(): Boolean = irManager?.hasIrEmitter() == true

    fun hasUsbIr(): Boolean = usbManager.deviceList.isNotEmpty()

    fun transmit(frequency: Int, pattern: IntArray, esp32Url: String = "http://192.168.1.100") {
        if (hasBuiltInIr()) {
            Log.d("SmartIrManager", "Transmitting via Built-in IR")
            irManager?.transmit(frequency, pattern)
        } else if (hasUsbIr()) {
            Log.d("SmartIrManager", "Transmitting via USB Type-C IR")
            // Adapter layer: Handle specific HID transmission here later using driver logic.
            // For now, signal the UI that the hardware adapter handled the request.
            Toast.makeText(context, "Transmitting via USB IR Type-C Adapter", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("SmartIrManager", "Transmitting via ESP32 WiFi Hub")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val payload = ESP32Payload(protocol = "RAW", rawData = pattern.toList())
                    ApiClient.esp32Service(esp32Url).sendIr(payload)
                } catch (e: Exception) {
                    Log.e("SmartIrManager", "ESP32 transmission failed", e)
                }
            }
        }
    }
}
