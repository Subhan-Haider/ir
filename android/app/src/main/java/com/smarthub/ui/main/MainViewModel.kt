package com.smarthub.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarthub.api.ApiClient
import com.smarthub.models.Device
import com.smarthub.models.ESP32Payload
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val devices = MutableLiveData<List<Device>>()

    fun fetchDevices() {
        viewModelScope.launch {
            try {
                // To do: Retrieve auth token from SharedPreferences or inject it
                val token = "mock_jwt_token_for_now"
                val response = ApiClient.apiService.getDevices("Bearer $token")
                devices.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendViaESP32(command: IntArray, esp32url: String = "http://YOUR_ESP32_IP") {
        viewModelScope.launch {
            try {
                // Using Retrofit to hit the ESP32 directly or via Backend
                // Assume ApiClient has an ESP32 specific call
                val payload = ESP32Payload(
                    protocol = "RAW",
                    rawData = command.toList()
                )
                ApiClient.esp32Service(esp32url).sendIr(payload)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
