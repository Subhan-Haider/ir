package com.smarthub.api

import com.smarthub.models.Device
import com.smarthub.models.ESP32Payload
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {
    @GET("api/devices")
    suspend fun getDevices(@Header("x-auth-token") token: String): List<Device>
}

interface ESP32Service {
    @POST("api/ir/send")
    suspend fun sendIr(@Body payload: ESP32Payload)
}
